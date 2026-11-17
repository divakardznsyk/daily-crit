package app.dailycrit.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.dailycrit.data.Block
import app.dailycrit.data.Kind
import app.dailycrit.data.Net
import app.dailycrit.data.ShapeSpec
import kotlin.math.cos
import kotlin.math.sin

/** Press feedback: a quick squeeze. The only "animation" most buttons have. */
@Composable
fun Modifier.tap(enabled: Boolean = true, onClick: () -> Unit): Modifier {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val sc by animateFloatAsState(if (pressed) 0.965f else 1f, tween(90), label = "tap")
    return this
        .scale(sc)
        .clickable(interactionSource = src, indication = null, enabled = enabled, onClick = onClick)
}

@Composable
fun Label(text: String, color: Color = C.Muted, modifier: Modifier = Modifier) {
    Text(text, style = T.sans(13, FontWeight.SemiBold, color), modifier = modifier)
}

@Composable
fun BigButton(
    text: String,
    modifier: Modifier = Modifier,
    bg: Color = C.Ink,
    fg: Color = C.Cream,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    // On the bright accent colours the label stays ink; on ink it is cream.
    val label = if (bg == C.Ink) fg else C.Ink
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bg,
            contentColor = label,
            disabledContainerColor = C.Surface2,
            disabledContentColor = C.Muted,
        ),
    ) {
        Text(text, style = T.sans(17, FontWeight.Bold, Color.Unspecified))
    }
}

@Composable
fun GhostButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.5.dp, C.Ink),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = C.Ink),
    ) {
        Text(text, style = T.sans(16, FontWeight.SemiBold, Color.Unspecified))
    }
}

@Composable
fun Pill(text: String, bg: Color, fg: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = T.sans(12, FontWeight.SemiBold, fg))
    }
}

/** Bottom action area used by the flows. */
@Composable
fun BottomAction(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(C.Bg)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) { content() }
}

@Composable
fun XpBar(progress: Float, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(CircleShape)
            .background(C.Surface2),
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(10.dp)
                .clip(CircleShape)
                .background(color),
        )
    }
}

/** Top bar of the flows: close button, title and optional progress segments. */
@Composable
fun FlowTop(title: String, accent: Color, onExit: () -> Unit, step: Int = 0, total: Int = 0) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .tap(true, onExit)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(C.Surface2),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(Modifier.size(14.dp)) {
                    val w = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(C.Paper, Offset(0f, 0f), Offset(size.width, size.height), w.width, StrokeCap.Round)
                    drawLine(C.Paper, Offset(size.width, 0f), Offset(0f, size.height), w.width, StrokeCap.Round)
                }
            }
            Box(Modifier.padding(start = 14.dp)) {
                Text(title, style = T.display(26))
            }
        }
        if (total > 0) {
            Row(Modifier.padding(top = 12.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)) {
                for (i in 0 until total) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(if (i < step) accent else C.Surface2),
                    )
                }
            }
        }
    }
}

/** Loads an image from the web without any extra library. */
@Composable
fun NetImage(url: String, modifier: Modifier = Modifier) {
    val state by produceState<Pair<ImageBitmap?, Boolean>>(Pair(null, false), url) {
        val bmp = Net.bitmap(url)
        value = Pair(bmp, bmp == null)
    }
    Box(modifier.background(C.Surface2), contentAlignment = Alignment.Center) {
        val bmp = state.first
        if (bmp != null) {
            Image(bitmap = bmp, contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
        } else if (state.second) {
            Text("Couldn't load the picture", style = T.mono(11, C.Muted))
        } else {
            Text("Developing the photo…", style = T.mono(11, C.Muted))
        }
    }
}

private fun regularPolygon(c: Offset, r: Float, n: Int, rotDeg: Float): Path {
    val path = Path()
    for (i in 0 until n) {
        val a = Math.toRadians((rotDeg + i * 360f / n - 90f).toDouble()).toFloat()
        val x = c.x + r * cos(a)
        val y = c.y + r * sin(a)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun starPath(c: Offset, r: Float, rotDeg: Float): Path {
    val path = Path()
    for (i in 0 until 10) {
        val rad = if (i % 2 == 0) r else r * 0.45f
        val a = Math.toRadians((rotDeg + i * 36f - 90f).toDouble()).toFloat()
        val x = c.x + rad * cos(a)
        val y = c.y + rad * sin(a)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun arrowPath(c: Offset, r: Float, rotDeg: Float): Path {
    val pts = listOf(
        0f to -1f, 0.72f to -0.05f, 0.3f to -0.05f, 0.3f to 0.95f,
        -0.3f to 0.95f, -0.3f to -0.05f, -0.72f to -0.05f,
    )
    val a = Math.toRadians(rotDeg.toDouble()).toFloat()
    val ca = cos(a)
    val sa = sin(a)
    val path = Path()
    pts.forEachIndexed { i, (px, py) ->
        val x = c.x + r * (px * ca - py * sa)
        val y = c.y + r * (px * sa + py * ca)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

@Composable
fun ShapeView(spec: ShapeSpec, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension * 0.4f
        val col = PALETTE[spec.color % PALETTE.size]
        val path = when (spec.kind) {
            Kind.POLY -> regularPolygon(c, r, spec.sides.coerceAtLeast(3), spec.rot)
            Kind.STAR -> starPath(c, r, spec.rot)
            Kind.ARROW -> arrowPath(c, r, spec.rot)
            Kind.CIRCLE -> Path().apply {
                addOval(androidx.compose.ui.geometry.Rect(c.x - r, c.y - r, c.x + r, c.y + r))
            }
        }
        if (spec.filled) {
            drawPath(path, col)
        } else {
            drawPath(path, col, style = Stroke(width = r * 0.16f, join = StrokeJoin.Round))
        }
    }
}

/** Tiny line illustrations for the section cards, drawn in ink on the coloured card. */
@Composable
fun Deco(block: Block, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val ink = C.Ink
        val w = size.width
        val h = size.height
        val sw = w * 0.07f
        val st = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (block) {
            Block.READ -> {
                drawRoundRect(ink, Offset(0f, 0f), Size(w * 0.46f, h * 0.46f), CornerRadius(w * 0.06f))
                for (i in 0..2) {
                    val y = h * (0.1f + i * 0.17f)
                    drawLine(ink, Offset(w * 0.56f, y), Offset(w, y), sw, StrokeCap.Round)
                }
                for (i in 0..2) {
                    val y = h * (0.62f + i * 0.16f)
                    drawLine(ink, Offset(0f, y), Offset(w, y), sw, StrokeCap.Round)
                }
            }
            Block.GK -> {
                drawCircle(ink, w * 0.48f, Offset(w / 2f, h / 2f), style = st)
                drawArc(
                    ink, 200f, 250f, false,
                    topLeft = Offset(w * 0.32f, h * 0.24f), size = Size(w * 0.36f, h * 0.30f), style = st,
                )
                drawCircle(ink, sw * 0.7f, Offset(w / 2f, h * 0.74f))
            }
            Block.ART -> {
                drawRoundRect(ink, Offset(0f, h * 0.06f), Size(w, h * 0.88f), CornerRadius(w * 0.06f), style = st)
                drawCircle(ink, w * 0.1f, Offset(w * 0.68f, h * 0.34f))
                val m = Path().apply {
                    moveTo(w * 0.08f, h * 0.86f)
                    lineTo(w * 0.38f, h * 0.46f)
                    lineTo(w * 0.58f, h * 0.7f)
                    lineTo(w * 0.72f, h * 0.56f)
                    lineTo(w * 0.92f, h * 0.86f)
                }
                drawPath(m, ink, style = st)
            }
            Block.PUZZLE -> {
                drawCircle(ink, w * 0.26f, Offset(w * 0.3f, h * 0.32f))
                drawRect(ink, Offset(w * 0.46f, h * 0.1f), Size(w * 0.44f, h * 0.44f), style = st)
                val t = Path().apply {
                    moveTo(w * 0.5f, h * 0.95f)
                    lineTo(w * 0.15f, h * 0.55f)
                    lineTo(w * 0.85f, h * 0.55f)
                    close()
                }
                drawPath(t, ink, style = st)
            }
            Block.MATHS -> {
                drawLine(ink, Offset(w * 0.2f, h * 0.2f), Offset(w * 0.2f, h * 0.6f), sw, StrokeCap.Round)
                drawLine(ink, Offset(0f, h * 0.4f), Offset(w * 0.4f, h * 0.4f), sw, StrokeCap.Round)
                drawLine(ink, Offset(w * 0.6f, h * 0.2f), Offset(w * 0.95f, h * 0.58f), sw, StrokeCap.Round)
                drawLine(ink, Offset(w * 0.95f, h * 0.2f), Offset(w * 0.6f, h * 0.58f), sw, StrokeCap.Round)
                drawLine(ink, Offset(0f, h * 0.82f), Offset(w * 0.4f, h * 0.82f), sw, StrokeCap.Round)
                drawCircle(ink, sw * 0.7f, Offset(w * 0.78f, h * 0.72f))
                drawCircle(ink, sw * 0.7f, Offset(w * 0.78f, h * 0.92f))
                drawLine(ink, Offset(w * 0.6f, h * 0.82f), Offset(w * 0.95f, h * 0.82f), sw, StrokeCap.Round)
            }
            Block.STUDIO -> {
                val body = Path().apply {
                    moveTo(w * 0.78f, h * 0.04f)
                    lineTo(w * 0.96f, h * 0.22f)
                    lineTo(w * 0.34f, h * 0.84f)
                    lineTo(w * 0.1f, h * 0.9f)
                    lineTo(w * 0.16f, h * 0.66f)
                    close()
                }
                drawPath(body, ink, style = st)
                drawLine(ink, Offset(w * 0.64f, h * 0.18f), Offset(w * 0.82f, h * 0.36f), sw, StrokeCap.Round)
                drawLine(ink, Offset(w * 0.16f, h * 0.66f), Offset(w * 0.34f, h * 0.84f), sw, StrokeCap.Round)
            }
            Block.PYQ -> {
                val sheet = Path().apply {
                    moveTo(w * 0.1f, h * 0.04f)
                    lineTo(w * 0.62f, h * 0.04f)
                    lineTo(w * 0.9f, h * 0.32f)
                    lineTo(w * 0.9f, h * 0.96f)
                    lineTo(w * 0.1f, h * 0.96f)
                    close()
                }
                drawPath(sheet, ink, style = st)
                drawLine(ink, Offset(w * 0.62f, h * 0.04f), Offset(w * 0.62f, h * 0.32f), sw, StrokeCap.Round)
                drawLine(ink, Offset(w * 0.62f, h * 0.32f), Offset(w * 0.9f, h * 0.32f), sw, StrokeCap.Round)
                for (i in 0..2) {
                    val y = h * (0.5f + i * 0.14f)
                    drawLine(ink, Offset(w * 0.26f, y), Offset(w * 0.74f, y), sw, StrokeCap.Round)
                }
            }
        }
    }
}

fun Block.color(): Color = when (this) {
    Block.READ -> C.Coral
    Block.GK -> C.Butter
    Block.ART -> C.Lilac
    Block.PUZZLE -> C.Mint
    Block.MATHS -> C.Sky
    Block.STUDIO -> C.Pink
    Block.PYQ -> C.Butter
}
