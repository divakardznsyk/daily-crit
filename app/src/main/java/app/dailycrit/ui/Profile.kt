package app.dailycrit.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.dailycrit.data.Block
import app.dailycrit.data.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate

@Composable
fun PhotoThumb(file: File) {
    val bmp by produceState<ImageBitmap?>(null, file) {
        value = withContext(Dispatchers.IO) {
            try {
                BitmapFactory.decodeFile(file.absolutePath, BitmapFactory.Options().apply { inSampleSize = 4 })?.asImageBitmap()
            } catch (e: Exception) { null }
        }
    }
    bmp?.let { Image(bitmap = it, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
}

@Composable
fun Field(value: String, hint: String, onChange: (String) -> Unit) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(C.Surface).border(1.5.dp, C.Line, RoundedCornerShape(16.dp)).padding(14.dp)) {
        if (value.isEmpty()) Text(hint, style = T.sans(16, FontWeight.Normal, C.Muted))
        BasicTextField(
            value = value, onValueChange = onChange, singleLine = true,
            textStyle = T.sans(16, FontWeight.Medium, C.Ink), cursorBrush = SolidColor(C.Ink), modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun ExamChips(store: Store) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("CEED", "NID", "NIFT").forEach { e ->
            val on = e in store.exams
            FilterChip(
                selected = on,
                onClick = { store.updateExams(if (on) store.exams - e else store.exams + e) },
                label = { Text(e, style = T.mono(12, C.Ink)) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = C.Butter, containerColor = C.Surface),
            )
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Label(title, C.Ink)
        content()
    }
}

@Composable
fun ProfileTab(store: Store) {
    var name by remember { mutableStateOf(store.name) }
    var mock by remember { mutableStateOf(store.mockUrl) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = C.Lilac)) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Label("Level ${store.level}", C.Ink)
                    Text(store.name.ifBlank { "You" }, style = T.display(38, C.Ink))
                    Text(store.levelTitle, style = T.sans(15, FontWeight.SemiBold, C.Ink))
                    XpBar(store.xpInLevel / 200f, C.Ink)
                    Label("${store.xp} xp total", C.Ink)
                }
                Crit(mood = Mood.CHEER, boxSize = 92.dp)
            }
        }
        Section("Aiming for") { ExamChips(store) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Stat("Streak", "${store.streak()}", C.Coral, Modifier.weight(1f))
            Stat("Days", "${store.daysQualified()}", C.Mint, Modifier.weight(1f))
            Stat("Sections", "${store.blocksTotal()}", C.Butter, Modifier.weight(1f))
        }
        Section("Accuracy") {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = C.Surface)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    listOf(Block.GK, Block.ART, Block.PUZZLE, Block.MATHS).forEach { b ->
                        val (r, t) = store.accuracy(b)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row { Text(b.title, style = T.sans(14, FontWeight.SemiBold), modifier = Modifier.weight(1f)); Label(if (t == 0) "no data" else "$r / $t", C.Muted) }
                            XpBar(if (t == 0) 0f else r / t.toFloat(), b.color())
                        }
                    }
                    Label("PYQ log: ${store.pyqRight} right of ${store.pyqTotal}", C.Muted)
                }
            }
        }
        Section("Back issues") {
            val issues = store.issues()
            if (issues.isEmpty()) Text("Send an edition to press and it lands here.", style = T.sans(14, FontWeight.Normal, C.Muted))
            issues.take(14).forEach { (d, h) ->
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(C.Surface).border(1.dp, C.Line, RoundedCornerShape(18.dp)).padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Label(LocalDate.ofEpochDay(d).toString(), C.Muted)
                    Text(h, style = T.display(22))
                }
            }
        }
        val photos = store.photoDays()
        if (photos.isNotEmpty()) Section("Studio log") {
            photos.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    pair.forEach { d ->
                        Box(Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(20.dp)).background(C.Surface2)) { store.photoFile(d)?.let { PhotoThumb(it) } }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
        Section("Settings") {
            Field(name, "Your name") { name = it; store.saveName(it) }
            Field(mock, "Mock test link (https://…)") { mock = it; store.setMock(it) }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun Stat(label: String, value: String, color: Color, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(value, style = T.display(40, C.Ink))
            Label(label, C.Ink)
        }
    }
}

@Composable
fun SavedTab(store: Store, onPractice: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Label("Saved questions", C.Ink)
        Text("Ones to come back to.", style = T.display(40))
        if (store.saved.isEmpty()) {
            Text("Tap \"Save for later\" under any question you got wrong. They collect here.", style = T.sans(15, FontWeight.Normal, C.Muted))
        } else {
            BigButton("Practice all (${store.saved.size})", bg = C.Butter) { onPractice() }
            store.saved.forEach { q ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = C.Surface)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Label(q.kicker, C.Coral, Modifier.weight(1f))
                            IconButton(onClick = { store.unsave(q.id) }) { Icon(Icons.Default.Close, contentDescription = "Remove", tint = C.Muted) }
                        }
                        Text(q.prompt, style = T.display(24))
                        Text("Answer: ${q.options[q.answer]}", style = T.sans(15, FontWeight.Bold, C.Good))
                        Text(q.explain, style = T.sans(14, FontWeight.Normal, C.Muted))
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}
