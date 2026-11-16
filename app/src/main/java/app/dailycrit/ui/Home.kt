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
    Box(
        modifier.rotate(-8f).clip(RoundedCornerShape(6.dp)).background(C.Surface.copy(alpha = 0.9f))
            .border(2.dp, C.Coral, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 3.dp),
    ) { Text(text.uppercase(), style = T.mono(11, C.Coral)) }
}

@Composable
fun Masthead(issue: Int) {
    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE d MMM yyyy", Locale.ENGLISH)).uppercase()
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("The Daily Crit", style = T.display(54), textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(3.dp).background(C.Ink))
        Spacer(Modifier.height(2.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(C.Ink))
        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Label("Vol. 1 · No. $issue", C.Ink)
            Label(date, C.Ink)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(C.Ink))
    }
}

@Composable
private fun LeadStory(store: Store, onOpen: () -> Unit) {
    val ctx = LocalContext.current
    val day = Store.today()
    val arts by produceState<List<Art>?>(null) { value = ArtRepo.today(ctx, day) }
    val done = store.isDone(day, Block.ART)
    val a = arts?.firstOrNull()
    Box(Modifier.fillMaxWidth().tap(true, onOpen)) {
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(C.Surface).border(1.5.dp, C.Ink, RoundedCornerShape(28.dp)),
        ) {
            if (a != null) {
                NetImage(a.url, Modifier.fillMaxWidth().aspectRatio(1.2f))
            } else {
                Box(Modifier.fillMaxWidth().aspectRatio(1.6f).background(C.Lilac), contentAlignment = Alignment.Center) {
                    Crit(mood = Mood.THINK, boxSize = 110.dp)
                }
            }
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Label("Lead story · Art desk", C.Coral)
                Text(if (done && a != null) "${a.title}, by ${a.artist}" else "Who made this?", style = T.display(32))
                Label(if (done) "Printed · ${store.result(day, Block.ART) ?: ""}" else "3 paintings · +${Block.ART.xp} xp", C.Muted)
            }
        }
        if (done) Stamp("Printed", Modifier.align(Alignment.TopEnd).padding(16.dp))
    }
}

@Composable
private fun Tile(block: Block, store: Store, modifier: Modifier, onClick: () -> Unit) {
    val day = Store.today()
    val done = store.isDone(day, block)
    Box(modifier.tap(true, onClick)) {
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = block.color()),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Deco(block, Modifier.size(26.dp))
                Spacer(Modifier.height(14.dp))
                Text(block.title, style = T.display(28, C.Ink))
                Label(if (done) (store.result(day, block)?.takeIf { it.length < 14 } ?: "done") else "${block.mins} min · +${block.xp} xp", C.Ink)
            }
        }
        if (done) Stamp("Printed", Modifier.align(Alignment.TopEnd).padding(10.dp))
    }
}

@Composable
private fun WeekCard(store: Store) {
    val today = Store.today()
    val monday = today - (LocalDate.now().dayOfWeek.value - 1)
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = C.Surface),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Label("This week")
                    Text("${store.streak()} day streak", style = T.display(30))
                }
                Pill("Lv ${store.level} · ${store.levelTitle}", C.Lilac, C.Ink)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (i in 0..6) {
                    val d = monday + i
                    val n = if (d > today) -1 else store.doneCount(d)
                    val bg = when { n >= 7 -> C.Coral; n >= QUALIFY -> C.Mint; n >= 1 -> C.Butter; else -> C.Surface2 }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            Modifier.size(36.dp).clip(CircleShape).background(bg)
                                .then(if (d == today) Modifier.border(2.dp, C.Ink, CircleShape) else Modifier),
                            contentAlignment = Alignment.Center,
                        ) { if (n >= QUALIFY) Text("✓", style = T.sans(14, FontWeight.Bold, C.Ink)) }
                        Label("MTWTFSS"[i].toString(), if (d == today) C.Ink else C.Muted)
                    }
                }
            }
            XpBar(store.xpInLevel / 200f, C.Coral)
            Label("${store.xpInLevel} / 200 xp to next level")
        }
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Hey, ${store.name.ifBlank { "you" }}", style = T.sans(15, FontWeight.SemiBold), modifier = Modifier.weight(1f))
            Box {
                IconButton(onClick = { menu = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = C.Ink) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("Night reset", style = T.sans(15)) }, onClick = { menu = false; onMenu("reset") })
                    DropdownMenuItem(text = { Text("Saved questions (${store.saved.size})", style = T.sans(15)) }, onClick = { menu = false; onMenu("saved") })
                    DropdownMenuItem(text = { Text("My profile", style = T.sans(15)) }, onClick = { menu = false; onMenu("me") })
                }
            }
        }
        Masthead(store.issueNo())
        LeadStory(store) { onOpen(Block.ART) }

        Label("Inside today", C.Ink)
        Block.values().filter { it != Block.ART }.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                pair.forEach { b -> Tile(b, store, Modifier.weight(1f)) { onOpen(b) } }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        if (evening) {
            Card(
                Modifier.fillMaxWidth().tap(true) { onMenu("reset") },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = C.Ink),
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Label("Before bed", C.Butter)
                        Text("Log Instagram out for tomorrow", style = T.display(24, C.Cream))
                    }
                    Crit(mood = Mood.SLEEPY, boxSize = 56.dp)
                }
            }
        }

        WeekCard(store)

        if (store.isPressed(day)) {
            GhostButton("Read today's front page") { onMenu("front") }
        } else {
            BigButton(
                if (doneN >= QUALIFY) "Send today's edition to press" else "Print ${QUALIFY - doneN} more to go to press",
                bg = C.Coral, enabled = doneN >= QUALIFY,
            ) { onPress() }
        }
        Spacer(Modifier.height(16.dp))
    }
}
