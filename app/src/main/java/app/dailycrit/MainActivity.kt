package app.dailycrit

import android.graphics.Color as AColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.dailycrit.data.Block
import app.dailycrit.data.Store
import app.dailycrit.ui.ArchiveTab
import app.dailycrit.ui.BigButton
import app.dailycrit.ui.C
import app.dailycrit.ui.Crit
import app.dailycrit.ui.FlowHost
import app.dailycrit.ui.HomeTab
import app.dailycrit.ui.Label
import app.dailycrit.ui.MeTab
import app.dailycrit.ui.Mood
import app.dailycrit.ui.ResetTab
import app.dailycrit.ui.T
import app.dailycrit.ui.tap
import kotlinx.coroutines.delay

private enum class Tab(val label: String) {
    TODAY("Today"), ARCHIVE("Archive"), RESET("Reset"), ME("Me")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AColor.TRANSPARENT),
        )
        val start = if (intent?.getStringExtra("tab") == "reset") Tab.RESET else Tab.TODAY
        setContent { Root(Store(applicationContext), start) }
    }
}

@Composable
private fun Root(store: Store, startTab: Tab) {
    var onboarded by remember { mutableStateOf(store.name.isNotBlank()) }
    var tab by remember { mutableStateOf(startTab) }
    var flow by remember { mutableStateOf<Block?>(null) }
    var xpBefore by remember { mutableStateOf(0) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2600)
            toast = null
        }
    }

    Box(Modifier.fillMaxSize().background(C.Bg)) {
        val open = flow
        when {
            !onboarded -> Onboarding(store) { onboarded = true }
            open != null -> FlowHost(open, store) {
                val gained = store.xp - xpBefore
                if (gained > 0) toast = "+$gained XP · ${open.title} printed"
                flow = null
            }
            else -> Column(Modifier.fillMaxSize().statusBarsPadding()) {
                Box(Modifier.weight(1f)) {
                    when (tab) {
                        Tab.TODAY -> HomeTab(store) { b ->
                            xpBefore = store.xp
                            flow = b
                        }
                        Tab.ARCHIVE -> ArchiveTab(store)
                        Tab.RESET -> ResetTab(store) { toast = it }
                        Tab.ME -> MeTab(store)
                    }
                }
                NavBar(tab) { tab = it }
            }
        }

        toast?.let {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 96.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(50))
                    .background(C.Paper)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Text(it, style = T.sans(15, FontWeight.Bold, C.Ink))
            }
        }
    }
}

@Composable
private fun NavBar(current: Tab, onPick: (Tab) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(50))
            .background(C.Surface)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Tab.values().forEach { t ->
            val sel = t == current
            Box(
                Modifier
                    .weight(1f)
                    .tap(true) { onPick(t) }
                    .clip(RoundedCornerShape(50))
                    .background(if (sel) C.Paper else C.Surface)
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    t.label,
                    style = T.sans(14, FontWeight.Bold, if (sel) C.Ink else C.Muted),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun Onboarding(store: Store, onDone: () -> Unit) {
    var name by remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxSize()
            .background(C.Coral)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Label("The Daily Crit", C.Ink)
        Spacer(Modifier.height(10.dp))
        Text("Your daily design edition.", style = T.display(54, C.Ink))
        Spacer(Modifier.weight(1f))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Crit(mood = Mood.CHEER, boxSize = 190.dp)
        }
        Spacer(Modifier.weight(1f))
        Text(
            "Hi, I'm Crit. I rub out excuses. Read, look, puzzle, draw, and chip away at CEED a little every day.",
            style = T.sans(16, FontWeight.Medium, C.Ink),
        )
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(C.Paper)
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            if (name.isEmpty()) Text("What should I call you?", style = T.sans(17, FontWeight.Medium, C.Muted))
            BasicTextField(
                value = name,
                onValueChange = { name = it.take(24) },
                singleLine = true,
                textStyle = T.sans(17, FontWeight.Bold, C.Ink),
                cursorBrush = SolidColor(C.Ink),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(12.dp))
        BigButton("Let's go", bg = C.Ink, fg = C.Paper, enabled = name.isNotBlank()) {
            store.setName(name)
            onDone()
        }
    }
}
