package app.dailycrit.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import app.dailycrit.Reminder
import app.dailycrit.data.Store
import java.time.LocalDate

private const val IG = "com.instagram.android"

private fun igInstalled(ctx: Context): Boolean =
    try {
        ctx.packageManager.getPackageInfo(IG, 0)
        true
    } catch (e: Exception) {
        false
    }

private fun openInstagramStorage(ctx: Context) {
    try {
        ctx.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$IG"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    } catch (e: Exception) {
        // settings not available
    }
}

@Composable
fun ResetTab(store: Store, onToast: (String) -> Unit) {
    val ctx = LocalContext.current
    val today = Store.today()
    val doneToday = store.lastReset == today
    val installed = remember { igInstalled(ctx) }
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    fun ensureNotifPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        Label("Night reset")
        Text("Make tomorrow's first scroll harder.", style = T.display(40))
        Text(
            "Clearing Instagram's storage logs you out. Tomorrow morning you have to type your password to get back in, and that pause is the whole trick.",
            style = T.sans(15, FontWeight.Normal, C.Muted),
        )

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(C.Coral)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Label(if (doneToday) "Done tonight" else "Tonight", C.Ink)
                Text(
                    if (doneToday) "Logged out. Sleep well." else "Clear it before bed",
                    style = T.display(30, C.Ink),
                )
            }
            Crit(mood = if (doneToday) Mood.SLEEPY else Mood.HAPPY, boxSize = 86.dp)
        }

        if (!installed) {
            Text(
                "Instagram doesn't look installed on this phone, so there's nothing to clear.",
                style = T.sans(14, FontWeight.Normal, C.Muted),
            )
        }

        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(C.Surface).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Label("How", C.Coral)
            listOf(
                "Tap the button. Instagram's App info page opens.",
                "Tap Storage, then Clear storage (or Clear data).",
                "Come back here and tick it off for +15 XP.",
            ).forEachIndexed { i, line ->
                Row {
                    Text("${i + 1}", style = T.mono(13, C.Coral), modifier = Modifier.width(22.dp))
                    Text(line, style = T.sans(15, FontWeight.Medium, C.Paper))
                }
            }
        }

        BigButton("Open Instagram storage", bg = C.Coral, enabled = installed) { openInstagramStorage(ctx) }
        GhostButton(if (doneToday) "Done for tonight" else "I cleared it") {
            val gain = store.markReset()
            if (gain > 0) onToast("+$gain XP. Instagram can wait.")
        }

        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(C.Surface).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Label("Nightly nudge", C.Lilac)
            Text(
                if (store.remindOn) "Reminder at %02d:%02d every night".format(store.remindH, store.remindM)
                else "Get a notification at bedtime so you don't forget.",
                style = T.sans(15, FontWeight.Medium, C.Paper),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.weight(1f)) {
                    GhostButton(if (store.remindOn) "Change time" else "Set a time") {
                        ensureNotifPermission()
                        TimePickerDialog(
                            ctx,
                            { _, h, m ->
                                store.setRemind(true, h, m)
                                Reminder.schedule(ctx)
                            },
                            store.remindH,
                            store.remindM,
                            false,
                        ).show()
                    }
                }
                if (store.remindOn) {
                    Box(Modifier.weight(1f)) {
                        GhostButton("Turn off") {
                            store.setRemind(false, store.remindH, store.remindM)
                            Reminder.schedule(ctx)
                        }
                    }
                }
            }
        }

        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(C.Surface).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Label("Honest note")
            Text(
                "Android doesn't let one app wipe another app's data on its own, so the last tap is yours. A fully automatic version is possible with a helper called Shizuku; ask if you want it added later.",
                style = T.sans(14, FontWeight.Normal, C.Muted),
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun ArchiveTab(store: Store) {
    val today = Store.today()
    val days = (0 until 28).map { today - it }
    val photoDays = store.photoDays()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        Label("Archive")
        Text("Your back issues.", style = T.display(40))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatTile("Streak", "${store.streak()}", C.Coral, Modifier.weight(1f))
            StatTile("Days read", "${store.daysQualified()}", C.Mint, Modifier.weight(1f))
            StatTile("Sections", "${store.blocksTotal()}", C.Lilac, Modifier.weight(1f))
        }

        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(C.Surface).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Label("Last 4 weeks")
            for (week in 0 until 4) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (i in 0 until 7) {
                        val d = today - (3 - week) * 7L - (6 - i)
                        val n = if (d > today) -1 else store.doneCount(d)
                        val bg = when {
                            n < 0 -> C.Bg
                            n >= 7 -> C.Butter
                            n >= 4 -> C.Mint
                            n >= 1 -> C.Surface2
                            else -> C.Bg
                        }
                        Box(
                            Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bg),
                            contentAlignment = Alignment.Center,
                        ) {
                            val label = LocalDate.ofEpochDay(d).dayOfMonth.toString()
                            Text(label, style = T.mono(11, if (n >= 4) C.Ink else C.Muted))
                        }
                    }
                }
            }
            Text(
                "Mint = 4+ sections. Yellow = the full edition.",
                style = T.sans(12, FontWeight.Normal, C.Muted),
            )
        }

        Label("Studio log")
        if (photoDays.isEmpty()) {
            Text(
                "Add a photo after a studio session and your drawings collect here.",
                style = T.sans(14, FontWeight.Normal, C.Muted),
            )
        } else {
            photoDays.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    pair.forEach { d ->
                        val file = store.photoFile(d)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(C.Surface2),
                            ) {
                                if (file != null) PhotoThumb(file)
                            }
                            Label(LocalDate.ofEpochDay(d).toString())
                        }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatTile(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(22.dp)).background(color).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(value, style = T.display(40, C.Ink))
        Label(label, C.Ink)
    }
}

@Composable
private fun PhotoThumb(file: java.io.File) {
    val bmp by androidx.compose.runtime.produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, file) {
        value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val o = android.graphics.BitmapFactory.Options().apply { inSampleSize = 4 }
                android.graphics.BitmapFactory.decodeFile(file.absolutePath, o)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }
    bmp?.let {
        androidx.compose.foundation.Image(
            bitmap = it,
            contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun MeTab(store: Store) {
    var name by remember { mutableStateOf(store.name) }
    var mock by remember { mutableStateOf(store.mockUrl) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        Label("Me")
        Text(if (store.name.isBlank()) "Hey, you." else "Hey, ${store.name}.", style = T.display(40))

        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(C.Lilac).padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Label("Level ${store.level}", C.Ink)
                Text(store.levelTitle, style = T.display(34, C.Ink))
                XpBar(store.xpInLevel / 200f, C.Ink)
                Label("${store.xp} XP total", C.Ink)
            }
            Spacer(Modifier.width(12.dp))
            Crit(mood = Mood.CHEER, boxSize = 88.dp)
        }

        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(C.Surface).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Label("Your name")
            Field(name, "What should Crit call you?") {
                name = it
                store.setName(it)
            }
        }
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(C.Surface).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Label("Mock test link")
            Field(mock, "https://…") {
                mock = it
                store.setMock(it)
            }
            Text("Opened from the PYQ desk in one tap.", style = T.sans(13, FontWeight.Normal, C.Muted))
        }
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(C.Surface).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Label("How it works")
            Text(
                "Finish 4 of the 7 sections to keep your streak. Finish all 7 for a bonus. Every 200 XP is a new level, and maths gets harder as you climb.",
                style = T.sans(14, FontWeight.Normal, C.Muted),
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun Field(value: String, hint: String, onChange: (String) -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(C.Surface2)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        if (value.isEmpty()) Text(hint, style = T.sans(16, FontWeight.Normal, C.Muted))
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = T.sans(16, FontWeight.Medium, C.Paper),
            cursorBrush = SolidColor(C.Paper),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Suppress("unused")
private val keep = Modifier.size(0.dp)
