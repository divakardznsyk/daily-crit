package app.dailycrit.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.dailycrit.data.Art
import app.dailycrit.data.ArtRepo
import app.dailycrit.data.Block
import app.dailycrit.data.Store

fun headline(store: Store): String {
    val n = store.doneCount(Store.today())
    val name = store.name.ifBlank { "you" }.uppercase()
    return when {
        n >= Block.values().size -> "$name CLEARS THE BOARD: A FULL EDITION"
        n >= 5 -> "$name FILLS THE FRONT PAGE"
        else -> "$name KEEPS THE PRESSES RUNNING"
    }
}

@Composable
fun FrontPage(store: Store, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val day = Store.today()
    val arts by produceState<List<Art>?>(null) { value = ArtRepo.today(ctx, day) }
    val head = store.pressedHeadline(day) ?: headline(store)
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().background(C.Bg).statusBarsPadding()) {
        FlowTop("Front page", C.Coral, onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Masthead(store.issueNo())
            Text(head, style = T.display(42))
            Label("Streak ${store.streak()} days · Level ${store.level} ${store.levelTitle}", C.Muted)
            arts?.firstOrNull()?.let { a ->
                NetImage(a.url, Modifier.fillMaxWidth().aspectRatio(1.3f).clip(RoundedCornerShape(20.dp)))
                if (store.isDone(day, Block.ART)) Text("${a.title}, ${a.artist}", style = T.sans(13, FontWeight.Medium, C.Muted))
            }
            Block.values().filter { store.isDone(day, it) }.forEach { b ->
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(b.color()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(b.title, style = T.display(26, C.Ink), modifier = Modifier.weight(1f))
                        val r = store.result(day, b)
                        if (r != null && r.length < 14) Label(r, C.Ink)
                    }
                    if (b == Block.STUDIO) store.result(day, b)?.let { Text(it, style = T.sans(14, FontWeight.Medium, C.Ink)) }
                }
            }
            store.photoFile(day)?.let {
                Box(Modifier.fillMaxWidth().aspectRatio(1.2f).clip(RoundedCornerShape(20.dp)).background(C.Surface2)) { PhotoThumb(it) }
                Label("Today's studio sketch", C.Muted)
            }
            Box(Modifier.height(12.dp))
        }
    }
}
