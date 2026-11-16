package app.dailycrit.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.dailycrit.data.Opt
import app.dailycrit.data.Q
import app.dailycrit.data.Store
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton

private fun optionBg(i: Int, picked: Int, answer: Int): Color = when {
    picked < 0 -> C.Surface2
    i == answer -> C.Good.copy(alpha = 0.26f)
    i == picked -> C.Bad.copy(alpha = 0.26f)
    else -> C.Surface
}

private fun optionBorder(i: Int, picked: Int, answer: Int): Color = when {
    picked < 0 -> C.Line
    i == answer -> C.Good
    i == picked -> C.Bad
    else -> C.Line
}

@Composable
fun QuizScreen(
    title: String,
    accent: Color,
    questions: List<Q>,
    xpFor: (Int) -> Int,
    onExit: () -> Unit,
    onFinish: (Int) -> Unit,
    store: Store? = null,
) {
    var idx by remember { mutableIntStateOf(0) }
    var picked by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    val finished = idx >= questions.size
    BackHandler(onBack = onExit)

    Column(Modifier.fillMaxSize().background(C.Bg).statusBarsPadding()) {
        val step = if (finished) questions.size else idx + (if (picked >= 0) 1 else 0)
        FlowTop(title, accent, onExit, step, questions.size)

        if (finished) {
            Column(
                Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Crit(mood = if (score * 2 >= questions.size) Mood.CHEER else Mood.HAPPY, boxSize = 170.dp)
                Spacer(Modifier.height(12.dp))
                Text("$score / ${questions.size}", style = T.display(72))
                Spacer(Modifier.height(6.dp))
                val line = when {
                    score == questions.size -> "Flawless. Crit is showing off."
                    score * 2 >= questions.size -> "Solid work. The edition is getting thicker."
                    else -> "Rough one. That's exactly how it sticks."
                }
                Text(line, style = T.sans(16, FontWeight.Medium, C.Muted), textAlign = TextAlign.Center)
                Spacer(Modifier.height(18.dp))
                Pill("+${xpFor(score)} XP", accent, C.Ink)
            }
            BottomAction {
                BigButton("Print this section", bg = accent) { onFinish(score) }
            }
        } else {
            val q = questions[idx]
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Crit(
                        mood = when {
                            picked < 0 -> Mood.THINK
                            picked == q.answer -> Mood.CHEER
                            else -> Mood.OOPS
                        },
                        boxSize = 64.dp,
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Label(q.kicker, accent)
                        Label("Question ${idx + 1} of ${questions.size}")
                    }
                }
                Text(q.prompt, style = T.display(30))

                q.imageUrl?.let {
                    NetImage(it, Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(20.dp)))
                }

                if (q.stem.isNotEmpty()) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (spec in q.stem) {
                            Box(
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(C.Surface),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (spec == null) {
                                    Text("?", style = T.display(34, accent))
                                } else {
                                    ShapeView(spec, Modifier.fillMaxSize().padding(4.dp))
                                }
                            }
                        }
                    }
                }

                val allShapes = q.options.all { it is Opt.Shp }
                if (allShapes) {
                    for (rowI in 0..1) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (col in 0..1) {
                                val i = rowI * 2 + col
                                val o = q.options[i] as Opt.Shp
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .tap(picked < 0) {
                                            picked = i
                                            if (i == q.answer) score++
                                        }
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(optionBg(i, picked, q.answer))
                                        .border(2.dp, optionBorder(i, picked, q.answer), RoundedCornerShape(20.dp)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    ShapeView(o.spec, Modifier.fillMaxSize().padding(18.dp))
                                }
                            }
                        }
                    }
                } else {
                    q.options.forEachIndexed { i, o ->
                        val text = (o as? Opt.Txt)?.s ?: ""
                        Row(
                            Modifier
                                .tap(picked < 0) {
                                    picked = i
                                    if (i == q.answer) score++
                                }
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(optionBg(i, picked, q.answer))
                                .border(2.dp, optionBorder(i, picked, q.answer), RoundedCornerShape(20.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                Modifier.size(28.dp).clip(CircleShape).background(C.Bg),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(("ABCD"[i]).toString(), style = T.mono(12, C.Paper))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(text, style = T.sans(16, FontWeight.Medium, C.Paper), modifier = Modifier.weight(1f))
                        }
                    }
                }

                if (picked >= 0) {
                    val right = picked == q.answer
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(C.Surface)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Label(if (right) "Nailed it" else "Not quite", if (right) C.Good else C.Bad)
                        Text(q.explain, style = T.sans(15, FontWeight.Normal, C.Paper))
                        if (store != null && q.options.all { it is Opt.Txt }) {
                            val saved = store.isSaved(q)
                            OutlinedButton(
                                onClick = { store.toggleSaved(q) },
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.5.dp, C.Ink),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = C.Ink),
                            ) { Text(if (saved) "Saved for later ✓" else "Save for later", style = T.sans(14, FontWeight.SemiBold, Color.Unspecified)) }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            BottomAction {
                BigButton(
                    text = when {
                        picked < 0 -> "Pick an answer"
                        idx == questions.lastIndex -> "Finish"
                        else -> "Next"
                    },
                    bg = accent,
                    enabled = picked >= 0,
                ) {
                    idx++
                    picked = -1
                }
            }
        }
    }
}
