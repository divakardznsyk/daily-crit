package app.dailycrit.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.dailycrit.data.Block
import app.dailycrit.data.Idea
import app.dailycrit.data.Prompts
import kotlin.random.Random
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import app.dailycrit.data.Store
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
private fun Choice(
    text: String,
    sub: String,
    selected: Boolean,
    accent: Color,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier
            .tap(enabled, onClick)
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) accent else C.Surface2)
            .border(2.dp, if (selected) accent else C.Line, RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(text, style = T.sans(16, FontWeight.Bold, if (selected) C.Ink else C.Paper))
        if (sub.isNotEmpty()) Text(sub, style = T.sans(12, FontWeight.Medium, if (selected) C.Ink else C.Muted))
    }
}

@Composable
fun StudioScreen(store: Store, onExit: () -> Unit) {
    val accent = Block.STUDIO.color()
    val day = Store.today()
    var options by remember { mutableStateOf<List<Idea>>(emptyList()) }
    var picked by remember { mutableIntStateOf(-1) }
    var spinning by remember { mutableStateOf(false) }
    val spin = remember { Animatable(0f) }
    val prompt = if (picked >= 0) options[picked].text else ""

    var warm by remember { mutableStateOf(false) }
    var mins by remember { mutableIntStateOf(15) }
    var left by remember { mutableIntStateOf(15 * 60) }
    var running by remember { mutableStateOf(false) }
    var started by remember { mutableStateOf(false) }
    var early by remember { mutableStateOf(false) }
    var stars by remember { mutableIntStateOf(0) }
    var photoSaved by remember { mutableStateOf(false) }

    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val doSpin: () -> Unit = {
        scope.launch {
            spinning = true
            spin.snapTo(0f)
            spin.animateTo(1080f, tween(1100, easing = FastOutSlowInEasing))
            options = Prompts.spin(Random(System.nanoTime()))
            picked = -1
            spinning = false
        }
    }
    val pick = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            scope.launch {
                store.savePhoto(uri)
                photoSaved = true
            }
        }
    }

    LaunchedEffect(running) {
        while (running && left > 0) {
            delay(1000)
            left -= 1
        }
        if (left <= 0) running = false
    }
    DisposableEffect(running) {
        view.keepScreenOn = running
        onDispose { view.keepScreenOn = false }
    }

    val selfCheck = started && (left <= 0 || early)
    BackHandler(onBack = onExit)

    Column(Modifier.fillMaxSize().background(C.Bg).statusBarsPadding()) {
        FlowTop(Block.STUDIO.title, accent, onExit)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (picked < 0) {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(accent).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Label("What will you draw today?", C.Ink)
                    Canvas(Modifier.size(84.dp).rotate(spin.value)) {
                        drawRoundRect(C.Ink, cornerRadius = CornerRadius(size.width * 0.22f))
                        val pip = size.width * 0.07f
                        listOf(0.28f to 0.28f, 0.72f to 0.28f, 0.5f to 0.5f, 0.28f to 0.72f, 0.72f to 0.72f).forEach { (x, y) ->
                            drawCircle(accent, pip, Offset(size.width * x, size.height * y))
                        }
                    }
                    Text(if (options.isEmpty()) "Spin the dice. Draw whichever one grabs you." else "Pick one. Or spin again.", style = T.sans(15, FontWeight.Medium, C.Ink))
                }
                if (!spinning) options.forEachIndexed { i, idea ->
                    Column(
                        Modifier.tap(true) { picked = i }.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(C.Surface)
                            .border(1.5.dp, C.Ink, RoundedCornerShape(24.dp)).padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Label(idea.tag, C.Coral)
                        Text(idea.text, style = T.display(26))
                    }
                }
            } else {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(accent).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Label(options[picked].tag, C.Ink)
                    Text(prompt, style = T.display(30, C.Ink))
                    if (!started) Text("Change it", style = T.sans(13, FontWeight.Bold, C.Ink), modifier = Modifier.tap(true) { picked = -1 })
                }
            }

            if (picked < 0) Unit else if (!selfCheck) {
                Choice(
                    text = if (warm) "Warm-up done" else "Warm-up first (2 min)",
                    sub = "Loose circles, fast lines, no erasing. Your third sketch is always better than your first.",
                    selected = warm,
                    accent = C.Mint,
                ) { warm = !warm }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(C.Surface)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("%02d:%02d".format(left / 60, left % 60), style = T.display(84))
                    if (!started) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            for (m in listOf(10, 15, 20)) {
                                Choice(
                                    text = "$m min",
                                    sub = "",
                                    selected = mins == m,
                                    accent = accent,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    mins = m
                                    left = m * 60
                                }
                            }
                        }
                    } else {
                        Text(
                            if (running) "Drawing. Keep the pencil moving." else "Paused.",
                            style = T.sans(14, FontWeight.Medium, C.Muted),
                        )
                    }
                }
                if (started) {
                    GhostButton("I'm done early") { early = true; running = false }
                }
            } else {
                Text("Now be your own examiner.", style = T.display(30))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Choice("Meh", "Learned something", stars == 1, accent, modifier = Modifier.weight(1f)) { stars = 1 }
                    Choice("Solid", "I'd keep it", stars == 2, accent, modifier = Modifier.weight(1f)) { stars = 2 }
                    Choice("Proud", "Pin it up", stars == 3, accent, modifier = Modifier.weight(1f)) { stars = 3 }
                }
                GhostButton(if (photoSaved) "Photo added to your log" else "Add a photo of it (optional)") {
                    pick.launch("image/*")
                }
                Text(
                    "Self-rating only. Be harsh on the drawing, kind to yourself.",
                    style = T.sans(13, FontWeight.Normal, C.Muted),
                )
            }
            Spacer(Modifier.height(12.dp))
        }
        BottomAction {
            when {
                picked < 0 -> BigButton(if (options.isEmpty()) "Spin for ideas" else "Spin again", bg = accent, enabled = !spinning) { doSpin() }
                !started -> BigButton("Start the timer", bg = accent) {
                    started = true
                    running = true
                }
                !selfCheck -> BigButton(if (running) "Pause" else "Resume", bg = accent) {
                    running = !running
                }
                else -> BigButton("Print it", bg = accent, enabled = stars > 0) {
                    store.complete(Block.STUDIO, stars * 5 + if (warm) 5 else 0, prompt)
                    onExit()
                }
            }
        }
    }
}

@Composable
private fun StepCard(n: Int, title: String, accent: Color, content: @Composable () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(C.Surface)
            .padding(16.dp),
    ) {
        Box(
            Modifier.size(32.dp).clip(CircleShape).background(accent),
            contentAlignment = Alignment.Center,
        ) { Text("$n", style = T.mono(13, C.Ink)) }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = T.display(24))
            content()
        }
    }
}

@Composable
fun PyqScreen(store: Store, onExit: () -> Unit) {
    val ctx = LocalContext.current
    val accent = Block.PYQ.color()
    var verdict by remember { mutableIntStateOf(-1) } // 0 missed, 1 got it, 2 skipped
    BackHandler(onBack = onExit)

    Column(Modifier.fillMaxSize().background(C.Bg).statusBarsPadding()) {
        FlowTop(Block.PYQ.title, accent, onExit)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "One real question from a real past paper, three minutes, on paper. Then be honest about how it went.",
                style = T.sans(16, FontWeight.Normal, C.Muted),
            )
            StepCard(1, "Open a past paper", accent) {
                GhostButton("CEED papers (IDC, IIT Bombay)") { openUrl(ctx, "https://www.ceed.iitb.ac.in/") }
                GhostButton("NID admissions") { openUrl(ctx, "https://admissions.nid.edu/") }
                GhostButton("NIFT admissions") { openUrl(ctx, "https://www.nift.ac.in/") }
            }
            StepCard(2, "Solve one on paper", accent) {
                Text(
                    "Pick a question you haven't seen. Set a 3 minute timer. No peeking at the key.",
                    style = T.sans(14, FontWeight.Normal, C.Muted),
                )
            }
            StepCard(3, "How did it go?", accent) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Choice("Got it", "", verdict == 1, accent, modifier = Modifier.weight(1f)) { verdict = 1 }
                    Choice("Missed", "", verdict == 0, accent, modifier = Modifier.weight(1f)) { verdict = 0 }
                    Choice("Skipped", "", verdict == 2, accent, modifier = Modifier.weight(1f)) { verdict = 2 }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(C.Surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Label("Feeling brave?", accent)
                Text("Take a full mock", style = T.display(24))
                GhostButton("Open my mock test site") { openUrl(ctx, store.mockUrl) }
                Text(
                    "Change the link any time in Me. Logged so far: ${store.pyqRight} right out of ${store.pyqTotal}.",
                    style = T.sans(13, FontWeight.Normal, C.Muted),
                )
            }
            Spacer(Modifier.height(12.dp))
        }
        BottomAction {
            BigButton("Log it", bg = accent, enabled = verdict >= 0) {
                val bonus = store.logPyq(verdict)
                store.complete(Block.PYQ, bonus, listOf("missed", "got it", "skipped")[verdict])
                onExit()
            }
        }
    }
}
