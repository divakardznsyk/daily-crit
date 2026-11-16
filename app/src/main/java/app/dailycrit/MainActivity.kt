package app.dailycrit

import android.graphics.Color as AColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.dailycrit.data.Block
import app.dailycrit.data.Q
import app.dailycrit.data.Store
import app.dailycrit.ui.BigButton
import app.dailycrit.ui.C
import app.dailycrit.ui.CritTheme
import app.dailycrit.ui.Crit
import app.dailycrit.ui.Deco
import app.dailycrit.ui.ExamChips
import app.dailycrit.ui.Field
import app.dailycrit.ui.FlowHost
import app.dailycrit.ui.FlowTop
import app.dailycrit.ui.FrontPage
import app.dailycrit.ui.HomeTab
import app.dailycrit.ui.Label
import app.dailycrit.ui.Mood
import app.dailycrit.ui.ProfileTab
import app.dailycrit.ui.QuizScreen
import app.dailycrit.ui.ResetTab
import app.dailycrit.ui.SavedTab
import app.dailycrit.ui.T
import app.dailycrit.ui.headline
import kotlinx.coroutines.delay

private enum class Tab(val label: String, val icon: Block) {
    TODAY("Today", Block.READ), SAVED("Saved", Block.PYQ), ME("Me", Block.GK)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(AColor.TRANSPARENT, AColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(AColor.TRANSPARENT, AColor.TRANSPARENT),
        )
        val startReset = intent?.getStringExtra("tab") == "reset"
        val store = Store(applicationContext)
        setContent { CritTheme { Root(store, startReset) } }
    }
}

@Composable
private fun Root(store: Store, startReset: Boolean) {
    var onboarded by remember { mutableStateOf(store.name.isNotBlank()) }
    var tab by remember { mutableStateOf(Tab.TODAY) }
    var screen by remember { mutableStateOf(if (startReset) "reset" else "") }
    var flow by remember { mutableStateOf<Block?>(null) }
    var practice by remember { mutableStateOf<List<Q>?>(null) }
    var xpBefore by remember { mutableStateOf(0) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(toast) { if (toast != null) { delay(2600); toast = null } }

    Box(Modifier.fillMaxSize().background(C.Bg)) {
        val open = flow
        val pq = practice
        when {
            !onboarded -> Onboarding(store) { onboarded = true }
            pq != null -> QuizScreen("Saved questions", C.Butter, pq, { 0 }, { practice = null }, { practice = null }, store)
            open != null -> FlowHost(open, store) {
                val gained = store.xp - xpBefore
                if (gained > 0) toast = "+$gained XP · ${open.title} printed"
                flow = null
            }
            screen == "reset" -> { BackHandler { screen = "" }; Column(Modifier.fillMaxSize().statusBarsPadding()) { FlowTop("Night reset", C.Coral, { screen = "" }); Box(Modifier.weight(1f)) { ResetTab(store) { toast = it } } } }
            screen == "front" -> FrontPage(store) { screen = "" }
            else -> Column(Modifier.fillMaxSize().statusBarsPadding()) {
                Box(Modifier.weight(1f)) {
                    when (tab) {
                        Tab.TODAY -> HomeTab(
                            store,
                            onOpen = { b -> xpBefore = store.xp; flow = b },
                            onMenu = { s -> if (s == "saved") tab = Tab.SAVED else if (s == "me") tab = Tab.ME else screen = s },
                            onPress = {
                                val g = store.press(headline(store))
                                if (g > 0) toast = "+$g XP · Edition sent to press"
                                screen = "front"
                            },
                        )
                        Tab.SAVED -> SavedTab(store) { practice = store.saved.map { it.toQ() } }
                        Tab.ME -> ProfileTab(store)
                    }
                }
                NavigationBar(containerColor = C.Surface, contentColor = C.Ink) {
                    Tab.values().forEach { t ->
                        NavigationBarItem(
                            selected = t == tab,
                            onClick = { tab = t },
                            icon = { Deco(t.icon, Modifier.size(22.dp)) },
                            label = { Text(t.label, style = T.sans(12, FontWeight.SemiBold)) },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = C.Butter, selectedTextColor = C.Ink, unselectedTextColor = C.Muted),
                        )
                    }
                }
            }
        }

        toast?.let {
            Box(
                Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 96.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(50)).background(C.Ink).padding(horizontal = 20.dp, vertical = 12.dp),
            ) { Text(it, style = T.sans(15, FontWeight.Bold, C.Cream)) }
        }
    }
}

@Composable
private fun Onboarding(store: Store, onDone: () -> Unit) {
    var name by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().background(C.Coral).statusBarsPadding().navigationBarsPadding().padding(24.dp)) {
        Spacer(Modifier.height(24.dp))
        Label("The Daily Crit", C.Ink)
        Spacer(Modifier.height(10.dp))
        Text("Your daily design edition.", style = T.display(54, C.Ink))
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Crit(mood = Mood.CHEER, boxSize = 190.dp) }
        Text(
            "Hi, I'm Crit. I rub out excuses. Paintings, puzzles, maths and sketches, a little every day, for CEED, NID and NIFT.",
            style = T.sans(16, FontWeight.Medium, C.Ink),
        )
        Spacer(Modifier.height(14.dp))
        ExamChips(store)
        Spacer(Modifier.height(14.dp))
        Field(name, "What should I call you?") { name = it.take(24) }
        Spacer(Modifier.height(12.dp))
        BigButton("Let's go", bg = C.Ink, fg = C.Cream, enabled = name.isNotBlank()) { store.saveName(name); onDone() }
    }
}
