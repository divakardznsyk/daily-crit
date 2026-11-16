package app.dailycrit.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class Mood { HAPPY, CHEER, THINK, SLEEPY, OOPS }

/**
 * Crit, the eraser. A pink rubber with a blue paper sleeve, pencil-dot eyes and a habit of
 * rubbing out excuses. Drawn entirely with vector paths so it stays crisp and costs no battery.
 */
@Composable
fun Crit(modifier: Modifier = Modifier, mood: Mood = Mood.HAPPY, boxSize: Dp = 120.dp) {
    Canvas(modifier.size(boxSize)) {
        val s = this.size.minDimension / 100f
        fun p(x: Float, y: Float) = Offset(x * s, y * s)
        val ink = Color(0xFF111111)
        val bodyTop = p(24f, 14f)
        val bodySize = Size(52f * s, 72f * s)
        val corner = CornerRadius(20f * s, 20f * s)

        // little rubbings under the feet
        drawRoundRect(C.Eraser.copy(alpha = 0.55f), p(14f, 91f), Size(7f * s, 3.5f * s), CornerRadius(2f * s))
        drawRoundRect(C.Eraser.copy(alpha = 0.55f), p(80f, 93f), Size(5f * s, 3f * s), CornerRadius(1.5f * s))
        drawRoundRect(C.Eraser.copy(alpha = 0.35f), p(24f, 95f), Size(4f * s, 2.5f * s), CornerRadius(1.2f * s))

        // arms
        val raised = mood == Mood.CHEER
        val armY = if (raised) 34f else 56f
        drawRoundRect(C.Eraser, p(11f, armY), Size(15f * s, 9f * s), CornerRadius(4.5f * s))
        drawRoundRect(C.Eraser, p(74f, armY), Size(15f * s, 9f * s), CornerRadius(4.5f * s))

        // body
        drawRoundRect(C.Eraser, bodyTop, bodySize, corner)

        // sleeve, clipped to the body outline
        val clip = Path().apply {
            addRoundRect(RoundRect(Rect(bodyTop, bodySize), corner))
        }
        clipPath(clip) {
            drawRect(C.Sleeve, p(20f, 60f), Size(60f * s, 40f * s))
            drawRect(Color(0x33000000), p(20f, 60f), Size(60f * s, 1.6f * s))
        }

        // cheeks
        drawCircle(Color(0xFFFF7F9F).copy(alpha = 0.6f), 4.2f * s, p(33f, 47f))
        drawCircle(Color(0xFFFF7F9F).copy(alpha = 0.6f), 4.2f * s, p(67f, 47f))

        // eyes
        val eyeY = if (mood == Mood.THINK) 36f else 38f
        val look = if (mood == Mood.THINK) 1.2f else 0f
        when (mood) {
            Mood.SLEEPY -> {
                val l = Path().apply {
                    moveTo(p(36f, 39f).x, p(36f, 39f).y)
                    quadraticBezierTo(p(41f, 43f).x, p(41f, 43f).y, p(46f, 39f).x, p(46f, 39f).y)
                }
                val r = Path().apply {
                    moveTo(p(54f, 39f).x, p(54f, 39f).y)
                    quadraticBezierTo(p(59f, 43f).x, p(59f, 43f).y, p(64f, 39f).x, p(64f, 39f).y)
                }
                drawPath(l, ink, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round))
                drawPath(r, ink, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round))
            }
            else -> {
                drawCircle(ink, 4.4f * s, p(41f + look, eyeY))
                drawCircle(ink, 4.4f * s, p(59f + look, eyeY))
                drawCircle(Color.White, 1.4f * s, p(42.4f + look, eyeY - 1.6f))
                drawCircle(Color.White, 1.4f * s, p(60.4f + look, eyeY - 1.6f))
            }
        }
        if (mood == Mood.OOPS) {
            val bl = Path().apply {
                moveTo(p(36f, 30f).x, p(36f, 30f).y)
                lineTo(p(46f, 32.5f).x, p(46f, 32.5f).y)
            }
            val br = Path().apply {
                moveTo(p(64f, 30f).x, p(64f, 30f).y)
                lineTo(p(54f, 32.5f).x, p(54f, 32.5f).y)
            }
            drawPath(bl, ink, style = Stroke(width = 2f * s, cap = StrokeCap.Round))
            drawPath(br, ink, style = Stroke(width = 2f * s, cap = StrokeCap.Round))
        }

        // mouth
        when (mood) {
            Mood.CHEER -> {
                val m = Path().apply {
                    moveTo(p(42f, 49f).x, p(42f, 49f).y)
                    quadraticBezierTo(p(50f, 62f).x, p(50f, 62f).y, p(58f, 49f).x, p(58f, 49f).y)
                    close()
                }
                drawPath(m, ink)
                drawCircle(Color(0xFFFF7F9F), 2.6f * s, p(50f, 55.5f))
            }
            Mood.THINK -> drawCircle(ink, 2.2f * s, p(51f, 52f))
            Mood.OOPS -> {
                val m = Path().apply {
                    moveTo(p(43f, 54f).x, p(43f, 54f).y)
                    quadraticBezierTo(p(50f, 47f).x, p(50f, 47f).y, p(57f, 54f).x, p(57f, 54f).y)
                }
                drawPath(m, ink, style = Stroke(width = 2.4f * s, cap = StrokeCap.Round))
            }
            Mood.SLEEPY -> drawCircle(ink, 1.8f * s, p(50f, 52f))
            Mood.HAPPY -> {
                val m = Path().apply {
                    moveTo(p(43f, 49f).x, p(43f, 49f).y)
                    quadraticBezierTo(p(50f, 57f).x, p(50f, 57f).y, p(57f, 49f).x, p(57f, 49f).y)
                }
                drawPath(m, ink, style = Stroke(width = 2.6f * s, cap = StrokeCap.Round))
            }
        }
    }
}
