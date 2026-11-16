package app.dailycrit.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.dailycrit.data.Block
import app.dailycrit.data.QUALIFY
import app.dailycrit.data.Store
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun greeting(): String {
    val h = LocalTime.now().hour
    return when {
        h < 5 -> "Still up"
        h < 12 -> "Good morning"
        h < 17 -> "Good afternoon"
        h < 21 -> "Good evening"
        else -> "Good night"
    }
}

@Composable
private fun SectionCard(
    block: Block,
    done: Boolean,
    wide: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier
            .tap(true, onClick)
            .clip(RoundedCornerShape(28.dp))
            .background(block.color())
            .alpha(if (done) 0.55f else 1f)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Label(block.kicker, C.Ink)
            Spacer(Modifier.weight(1f))
            if (done) {
                Box(
                    Modifier.size(24.dp).clip(CircleShape).background(C.Ink),
                    contentAlignment = Alignment.Center,
                ) { Text("✓", style = T.sans(13, FontWeight.Bold, C.Paper)) }
            } else {
                Label("+${block.xp} xp", C.Ink)
            }
        }
        Deco(block, Modifier.size(if (wide) 56.dp else 44.dp))
        Spacer(Modifier.height(if (wide) 6.dp else 14.dp))
        Text(block.title, style = T.display(if (wide) 38 else 30, C.Ink))
        Text(block.blurb, style = T.sans(13, FontWeight.Medium, C.Ink))
        Label("${block.mins} min", C.Ink)
    }
}

@Composable
fun HomeTab(store: Store, onOpen: (Block) -> Unit) {
    val today = Store.today()
    val blocks = Block.values().toList()
    val doneN = store.doneCount(today)
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault()))
    val name = store.name.ifBlank { "you" }
    val streak = store.streak()
    val totalMins = blocks.sumOf { it.mins }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Label("The Daily Crit · $date")
                Spacer(Modifier.height(6.dp))
                Text("${greeting()}, $name.", style = T.display(40))
            }
            Crit(
                mood = when {
                    doneN >= blocks.size -> Mood.CHEER
                    doneN >= QUALIFY -> Mood.HAPPY
                    doneN == 0 -> Mood.SLEEPY
                    else -> Mood.THINK
                },
                boxSize = 72.dp,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("$streak day streak", if (streak > 0) C.Coral else C.Surface2, if (streak > 0) C.Ink else C.Muted)
            Pill("Lv ${store.level} · ${store.levelTitle}", C.Surface2, C.Paper)
        }

        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(C.Surface).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f)) {
                    Label("Today's edition")
                    Text("$doneN of ${blocks.size} printed", style = T.display(32))
                }
                Label("~$totalMins min")
            }
            XpBar(doneN / blocks.size.toFloat(), if (doneN >= QUALIFY) C.Mint else C.Paper)
            Text(
                when {
                    doneN >= blocks.size -> "Full edition. Crit is unbearable now."
                    doneN >= QUALIFY -> "Streak is safe. Go for the full set for a bonus."
                    else -> "Print ${QUALIFY - doneN} more to keep today's streak."
                },
                style = T.sans(14, FontWeight.Normal, C.Muted),
            )
        }

        // Front page is the big lead story, everything else sits in two columns.
        SectionCard(blocks[0], store.isDone(today, blocks[0]), true, Modifier.fillMaxWidth()) { onOpen(blocks[0]) }
        blocks.drop(1).chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                pair.forEach { b ->
                    SectionCard(b, store.isDone(today, b), false, Modifier.weight(1f)) { onOpen(b) }
                }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}
