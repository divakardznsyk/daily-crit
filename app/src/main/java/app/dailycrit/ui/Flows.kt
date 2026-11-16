package app.dailycrit.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.dailycrit.data.ArtRepo
import app.dailycrit.data.Block
import app.dailycrit.data.Content
import app.dailycrit.data.Maths
import app.dailycrit.data.Puzzles
import app.dailycrit.data.Q
import app.dailycrit.data.Reads
import app.dailycrit.data.Store
import app.dailycrit.data.Story

fun openUrl(ctx: Context, url: String) {
    try {
        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (e: Exception) {
        // no browser available
    }
}

@Composable
fun FlowHost(block: Block, store: Store, onExit: () -> Unit) {
    val day = Store.today()
    when (block) {
        Block.READ -> ReadScreen(store, onExit)
        Block.GK -> {
            val qs = remember(day) { Content.gkQuestions(day) }
            QuizScreen(
                title = block.title,
                accent = block.color(),
                questions = qs,
                xpFor = { store.xpPreview(block, it * 3) },
                onExit = onExit,
            ) { score ->
                store.complete(block, score * 3)
                onExit()
            }
        }
        Block.ART -> ArtFlow(store, onExit)
        Block.PUZZLE -> {
            val qs = remember(day) { Puzzles.day(day) }
            QuizScreen(
                title = block.title,
                accent = block.color(),
                questions = qs,
                xpFor = { store.xpPreview(block, it * 4) },
                onExit = onExit,
            ) { score ->
                store.complete(block, score * 4)
                onExit()
            }
        }
        Block.MATHS -> {
            val lvl = store.level
            val qs = remember(day, lvl) { Maths.day(lvl, day) }
            QuizScreen(
                title = block.title,
                accent = block.color(),
                questions = qs,
                xpFor = { store.xpPreview(block, it * 3) },
                onExit = onExit,
            ) { score ->
                store.complete(block, score * 3)
                onExit()
            }
        }
        Block.STUDIO -> StudioScreen(store, onExit)
        Block.PYQ -> PyqScreen(store, onExit)
    }
}

@Composable
private fun ArtFlow(store: Store, onExit: () -> Unit) {
    val ctx = LocalContext.current
    val day = Store.today()
    val loaded by produceState<List<Q>?>(null) {
        value = ArtRepo.questions(ctx, day) ?: Content.artOffline(day)
    }
    val qs = loaded
    if (qs == null) {
        BackHandler(onBack = onExit)
        Column(Modifier.fillMaxSize().background(C.Bg).statusBarsPadding()) {
            FlowTop(Block.ART.title, Block.ART.color(), onExit)
            Column(
                Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Crit(mood = Mood.THINK, boxSize = 140.dp)
                Spacer(Modifier.height(14.dp))
                Text("Hanging today's paintings…", style = T.sans(16, FontWeight.Medium, C.Muted))
            }
        }
    } else {
        QuizScreen(
            title = Block.ART.title,
            accent = Block.ART.color(),
            questions = qs,
            xpFor = { store.xpPreview(Block.ART, it * 4) },
            onExit = onExit,
        ) { score ->
            store.complete(Block.ART, score * 4)
            onExit()
        }
    }
}

@Composable
private fun StoryCard(story: Story, tint: androidx.compose.ui.graphics.Color, onOpen: () -> Unit) {
    Column(
        Modifier
            .tap(true, onOpen)
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(C.Surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Label(story.source, tint)
        Text(story.title, style = T.display(24))
    }
}

@Composable
fun ReadScreen(store: Store, onExit: () -> Unit) {
    val ctx = LocalContext.current
    val day = Store.today()
    val stories by produceState<List<Story>?>(null) { value = Reads.today(ctx, day) }
    var opened by remember { mutableStateOf(false) }
    val accent = Block.READ.color()
    BackHandler(onBack = onExit)

    Column(Modifier.fillMaxSize().background(C.Bg).statusBarsPadding()) {
        FlowTop(Block.READ.title, accent, onExit)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Read one story. Then notice one thing: the type, the layout, a colour, an idea. Say it out loud.",
                style = T.sans(16, FontWeight.Normal, C.Muted),
            )
            val list = stories
            Label("Fresh off the press", accent, Modifier.padding(top = 6.dp))
            if (list == null) {
                Text("Fetching today's stories…", style = T.mono(12, C.Muted))
            } else if (list.isEmpty()) {
                Text(
                    "No new stories right now (no internet?). The classics shelf below works anyway.",
                    style = T.sans(14, FontWeight.Normal, C.Muted),
                )
            } else {
                list.forEach { s ->
                    StoryCard(s, accent) {
                        opened = true
                        openUrl(ctx, s.url)
                    }
                }
            }
            Label("Classics shelf", accent, Modifier.padding(top = 10.dp))
            Content.classicsFor(day).forEach { s ->
                StoryCard(s, accent) {
                    opened = true
                    openUrl(ctx, s.url)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        BottomAction {
            BigButton(
                text = if (opened) "I read one and noticed something" else "Open a story first",
                bg = accent,
                enabled = opened,
            ) {
                store.complete(Block.READ)
                onExit()
            }
        }
    }
}
