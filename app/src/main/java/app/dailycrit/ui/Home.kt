package app.dailycrit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.dailycrit.data.Art
import app.dailycrit.data.ArtRepo
import app.dailycrit.data.Block
import app.dailycrit.data.QUALIFY
import app.dailycrit.data.Store
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun Stamp(text: String, modifier: Modifier = Modifier) {
    Box(modifier.size(26.dp).clip(CircleShape).background(C.Ink), contentAlignment = Alignment.Center) {
        Text("✓", style = T.sans(14, FontWeight.Bold, C.Cream))
    }
}

@Composable
fun Masthead(issue: Int) {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.ENGLISH))
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("The Daily Crit", style = T.display(44))
        Text("$date · Issue $issue", style = T.sans(14, FontWeight.Medium, C.Muted))
    }
}

@Composable
private fun LeadStory(store: Store, onOpen: () -> Unit) {
    val ctx = LocalContext.current
    val day = Store.today()
    val arts by produceState<List<Art>?>(null) { value = ArtRepo.today(ctx, day) }
    val done = store.isDone(day, Block.ART)
    val a = arts?.firstOrNull()
    Column(Modifier.fillMaxWidth().tap(true, onOpen).clip(RoundedCornerShape(28.dp)).background(C.Surface)) {
        if (a != null) NetImage(a.url, Modifier.fillMaxWidth().aspectRatio(1.3f))
        else Box(Modifier.fillMaxWidth().aspectRatio(1.9f).background(C.Lilac), contentAlignment = Alignment.Center) { Crit(mood = Mood.THINK, boxSize = 96.dp) }
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Art desk", style = T.sans(13, FontWeight.SemiBold, C.Muted))
                Text(if (done && a != null) a.title else "Who painted this?", style = T.display(28))
                Text(if (done) (store.result(day, Block.ART) ?: "Done") else "3 paintings · +${Block.ART.xp} xp", style = T.sans(14, FontWeight.Medium, C.Muted))
            }
            if (done) Stamp("") else Box(Modifier.size(44.dp).clip(CircleShape).background(C.Ink), contentAlignment = Alignment.Center) { Text("→", style = T.sans(18, FontWeight.Bold, C.Cream)) }
        }
    }
}

@Composable
private fun SectionRow(block: Block, store: Store, onClick: () -> Unit) {
    val day = Store.today()
    val done = store.isDone(day, block)
    androidx.compose.foundation.layout.Row(
        Modifier.fillMaxWidth().tap(true, onClick).clip(RoundedCornerShape(22.dp)).background(C.Surface).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(block.color()), contentAlignment = Alignment.Center) { Deco(block, Modifier.size(26.dp)) }
        Column(Modifier.weight(1f).padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(block.title, style = T.sans(17, FontWeight.SemiBold))
            Text(if (done) (store.result(day, block)?.takeIf { it.length < 30 } ?: "Done") else "${block.mins} min · +${block.xp} xp", style = T.sans(13, FontWeight.Medium, C.Muted))
        }
        if (done) Stamp("") else Box(Modifier.size(26.dp).border(1.5.dp, C.Line, CircleShape))
    }
}

@Composable
private fun Progress(store: Store, doneN: Int) {
    val today = Store.today()
    val monday = today - (LocalDate.now().dayOfWeek.value - 1)
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(C.Ink).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("$doneN of ${Block.values().size} printed", style = T.display(34, C.Cream))
                Text("${store.streak()} day streak · Lv ${store.level} ${store.levelTitle}", style = T.sans(14, FontWeight.Medium, C.Cream.copy(alpha = 0.65f)))
            }
        }
        androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            for (i in 0..6) {
                val d = monday + i
                val n = if (d > today) -1 else store.doneCount(d)
                val bg = when { n >= QUALIFY -> C.Butter; n >= 1 -> C.Cream.copy(alpha = 0.35f); else -> C.Cream.copy(alpha = 0.12f) }
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(34.dp).clip(CircleShape).background(bg).then(if (d == today) Modifier.border(2.dp, C.Cream, CircleShape) else Modifier), contentAlignment = Alignment.Center) {
                        if (n >= QUALIFY) Text("✓", style = T.sans(14, FontWeight.Bold, C.Ink))
                    }
                    Text("MTWTFSS"[i].toString(), style = T.sans(12, FontWeight.Medium, C.Cream.copy(alpha = 0.6f)))
                }
            }
        }
        XpBar(store.xpInLevel / 200f, C.Butter)
    }
}

@Composable
fun HomeTab(store: Store, onOpen: (Block) -> Unit, onMenu: (String) -> Unit, onPress: () -> Unit) {
    val day = Store.today()
    val doneN = store.doneCount(day)
    var menu by remember { mutableStateOf(false) }
    val evening = LocalTime.now().hour >= 20 && store.lastReset != day

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("Hey, ${store.name.ifBlank { "you" }}", style = T.sans(15, FontWeight.SemiBold, C.Muted))
                Masthead(store.issueNo())
            }
            Box {
                IconButton(onClick = { menu = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = C.Ink) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("Night reset", style = T.sans(15)) }, onClick = { menu = false; onMenu("reset") })
                    DropdownMenuItem(text = { Text("Saved questions (${store.saved.size})", style = T.sans(15)) }, onClick = { menu = false; onMenu("saved") })
                    DropdownMenuItem(text = { Text("My profile", style = T.sans(15)) }, onClick = { menu = false; onMenu("me") })
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Progress(store, doneN)
        LeadStory(store) { onOpen(Block.ART) }
        Text("More for today", style = T.sans(15, FontWeight.SemiBold, C.Muted), modifier = Modifier.padding(top = 8.dp))
        Block.values().filter { it != Block.ART }.forEach { b -> SectionRow(b, store) { onOpen(b) } }

        if (evening) {
            androidx.compose.foundation.layout.Row(
                Modifier.fillMaxWidth().tap(true) { onMenu("reset") }.clip(RoundedCornerShape(22.dp)).background(C.Lilac).padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Before bed", style = T.sans(13, FontWeight.SemiBold, C.Ink.copy(alpha = 0.6f)))
                    Text("Log Instagram out for tomorrow", style = T.display(24))
                }
                Crit(mood = Mood.SLEEPY, boxSize = 52.dp)
            }
        }

        Spacer(Modifier.height(4.dp))
        if (store.isPressed(day)) GhostButton("Read today's front page") { onMenu("front") }
        else BigButton(if (doneN >= QUALIFY) "Send today's edition to press" else "Print ${QUALIFY - doneN} more to go to press", bg = C.Ink, enabled = doneN >= QUALIFY) { onPress() }
        Spacer(Modifier.height(16.dp))
    }
}
