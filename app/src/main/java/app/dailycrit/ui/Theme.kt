@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package app.dailycrit.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.dailycrit.R

object C {
    val Bg = Color(0xFF000000)
    val Surface = Color(0xFF131315)
    val Surface2 = Color(0xFF1E1E21)
    val Line = Color(0xFF2C2C30)
    val Paper = Color(0xFFF4EFE6)
    val Muted = Color(0xFF8E8B85)
    val Ink = Color(0xFF111111)

    val Coral = Color(0xFFFF6B4A)
    val Lilac = Color(0xFFC8B6FF)
    val Butter = Color(0xFFFFD84D)
    val Mint = Color(0xFF79E2B2)
    val Sky = Color(0xFF7DB0FF)
    val Pink = Color(0xFFFF9EC7)

    val Eraser = Color(0xFFFFB5CB)
    val Sleeve = Color(0xFF3B5BFF)

    val Good = Color(0xFF3DDC84)
    val Bad = Color(0xFFFF5C5C)
}

/** Colours used by the shape puzzles. */
val PALETTE = listOf(C.Coral, C.Sky, C.Butter, C.Mint, C.Lilac, C.Pink)

val Display = FontFamily(
    Font(R.font.instrument_serif_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.instrument_serif_italic, FontWeight.Normal, FontStyle.Italic),
)

val Sans = FontFamily(
    Font(R.font.dm_sans, FontWeight.Normal, FontStyle.Normal, FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.dm_sans, FontWeight.Medium, FontStyle.Normal, FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.dm_sans, FontWeight.SemiBold, FontStyle.Normal, FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.dm_sans, FontWeight.Bold, FontStyle.Normal, FontVariation.Settings(FontVariation.weight(700))),
)

val Mono = FontFamily(
    Font(R.font.dm_mono_regular, FontWeight.Normal),
    Font(R.font.dm_mono_medium, FontWeight.Medium),
)

object T {
    fun display(size: Int, color: Color = C.Paper, italic: Boolean = false) = TextStyle(
        fontFamily = Display,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        fontSize = size.sp,
        lineHeight = (size * 1.08f).sp,
        color = color,
    )

    fun sans(size: Int, weight: FontWeight = FontWeight.Medium, color: Color = C.Paper) = TextStyle(
        fontFamily = Sans,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.38f).sp,
        color = color,
    )

    fun mono(size: Int = 11, color: Color = C.Muted) = TextStyle(
        fontFamily = Mono,
        fontWeight = FontWeight.Medium,
        fontSize = size.sp,
        letterSpacing = 1.sp,
        color = color,
    )
}
