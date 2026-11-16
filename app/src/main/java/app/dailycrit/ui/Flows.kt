package app.dailycrit.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.dailycrit.data.ArtRepo
import app.dailycrit.data.Block
import app.dailycrit.data.Content
import app.dailycrit.data.Maths
import app.dailycrit.data.Net
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
private fun Quiz(block: Block, qs: List<Q>, per: Int, store: Store, onExit: () -> Unit) {
    QuizScreen(
        title = block.title,
        accent = block.color(),
        questions = qs,
        xpFor = { store.xpPreview(block, it * per) },
        onExit = onExit,
        onFinish = { score ->
            store.recordAnswers(block, score, qs.size)
            store.complete(block, score * per, "$score/${qs.size}")
            onExit()
        },
        store = store,
    )
}

@Composable
fun FlowHost(block: Block, store: Store, onExit: () -> Unit) {
    val day = Store.today()
    val lvl = store.level
    when (block) {
        Block.READ -> ReadScreen(store, onExit)
        Block.GK -> Quiz(block, remember(day, lvl) { Content.gkQuestions(day, lvl, store.exams) }, 3, store, onExit)
        Block.ART -> ArtFlow(store, onExit)
        Block.PUZZLE -> Quiz(block, remember(day, lvl) { Puzzles.day(day, lvl) }, 4, store, onExit)
        Block.MATHS -> Quiz(block, remember(day, lvl) { Maths.day(lvl, day) }, 3, store, onExit)
        Block.STUDIO -> StudioScreen(store, onExit)
        Block.PYQ -> PyqScreen(store, onExit)
    }
}

@Composable
private fun ArtFlow(store: Store, onExit: () -> Unit) {
    val ctx = LocalContext.current
    val day = Store.today()
    var offline by remember { mutableStateOf(false) }
    val qs by produceState<List<Q>?>(null) {
        val online = ArtRepo.questions(ctx, day, store.level)
        offline = online == null
        value = online ?: Content.artOffline(day)
    }
    val list = qs
    if (list == null) {
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
        Column(Modifier.fillMaxSize().background(C.Bg)) {
            if (offline) {
                Text(
                    "Couldn't load paintings (${Net.err.ifBlank { "no connection" }}). Showing text questions.",
                    style = T.mono(10, C.Bad),
                    modifier = Modifier.statusBarsPadding().padding(horizontal = 20.dp, vertical = 6.dp),
                )
            }
            Quiz(Block.ART, list, 4, store, onExit)
        }
    }
}

@Composable
private fun StoryCard(story: Story, onOpen: () -> Unit) {
    Column(
        Modifier.tap(true, onOpen).fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(C.Surface),
    ) {
        if (story.img.isNotEmpty()) NetImage(story.img, Modifier.fillMaxWidth().aspectRatio(1.5f))
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Label(story.source, C.Coral)
            Text(story.title, style = T.display(24))
        }
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
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Look at one project. Notice one thing: the type, the layout, a colour, an idea.", style = T.sans(16, FontWeight.Normal, C.Muted))
            val list = stories
            when {
                list == null -> Text("Fetching today's projects…", style = T.mono(12, C.Muted))
                list.isEmpty() -> Text("No projects loaded (${Net.err.ifBlank { "no connection" }}).", style = T.mono(11, C.Bad))
                else -> list.forEach { s -> StoryCard(s) { opened = true; openUrl(ctx, s.url) } }
            }
            Spacer(Modifier.height(12.dp))
        }
        BottomAction {
            BigButton(if (opened || stories?.isEmpty() == true) "I noticed something" else "Open one first", bg = accent, enabled = opened || stories?.isEmpty() == true) {
                store.complete(Block.READ, 0, "read")
                onExit()
            }
        }
    }
}
