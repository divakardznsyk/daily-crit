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

