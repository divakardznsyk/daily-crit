@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package app.dailycrit.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.dailycrit.R

/** Off-white "newsprint" palette. Ink on cream, with bright spot colours for each section. */
object C {
    val Bg = Color(0xFFF6F4EE)       // off-white
    val Surface = Color(0xFFFFFFFF)  // card
    val Surface2 = Color(0xFFECE8DE) // deeper
    val Line = Color(0xFFDDD8CB)     // hairlines
    val Ink = Color(0xFF16130F)
    val Paper = Ink                  // body text colour (kept for older call sites)
    val Cream = Color(0xFFF6F4EE)    // text on ink
    val Muted = Color(0xFF7A7266)

    val Coral = Color(0xFFFF6B4A)
    val Lilac = Color(0xFFC9B8FF)
    val Butter = Color(0xFFFFD84D)
    val Mint = Color(0xFF7FDDB4)
    val Sky = Color(0xFF8DB8FF)
    val Pink = Color(0xFFFFB3CF)
    val Blue = Color(0xFF2B3FD9)

    val Eraser = Color(0xFFFFB5CB)
    val Sleeve = Color(0xFF3B5BFF)

    val Good = Color(0xFF1FA463)
    val Bad = Color(0xFFE5484D)
}

/** Colours used by the shape puzzles. */
val PALETTE = listOf(C.Coral, C.Sky, C.Butter, C.Mint, C.Lilac, C.Pink)

val Display = FontFamily(
    Font(R.font.instrument_serif_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.instrument_serif_italic, FontWeight.Normal, FontStyle.Italic),
)

val Sans = FontFamily(
    Font(R.font.dm_sans, FontWeight.Normal, FontStyle.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.dm_sans, FontWeight.Medium, FontStyle.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.dm_sans, FontWeight.SemiBold, FontStyle.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.dm_sans, FontWeight.Bold, FontStyle.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

val Mono = FontFamily(
    Font(R.font.dm_mono_regular, FontWeight.Normal),
    Font(R.font.dm_mono_medium, FontWeight.Medium),
)

object T {
    fun display(size: Int, color: Color = C.Ink, italic: Boolean = false) = TextStyle(
        fontFamily = Display,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        fontSize = size.sp,
        lineHeight = (size * 1.08f).sp,
        color = color,
    )

    fun sans(size: Int, weight: FontWeight = FontWeight.Medium, color: Color = C.Ink) = TextStyle(
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

private val scheme = lightColorScheme(
    primary = C.Ink,
    onPrimary = C.Cream,
    primaryContainer = C.Surface2,
    onPrimaryContainer = C.Ink,
    secondary = C.Coral,
    onSecondary = C.Ink,
    secondaryContainer = C.Butter,
    onSecondaryContainer = C.Ink,
    tertiary = C.Blue,
    onTertiary = C.Cream,
    background = C.Bg,
    onBackground = C.Ink,
    surface = C.Surface,
    onSurface = C.Ink,
    surfaceVariant = C.Surface2,
    onSurfaceVariant = C.Muted,
    surfaceContainer = C.Surface,
    surfaceContainerHigh = C.Surface2,
    outline = C.Line,
    outlineVariant = C.Line,
    error = C.Bad,
)

private val typography = Typography(
    displayLarge = T.display(57),
    displayMedium = T.display(45),
    displaySmall = T.display(36),
    headlineLarge = T.display(32),
    headlineMedium = T.display(28),
    headlineSmall = T.display(24),
    titleLarge = T.display(22),
    titleMedium = T.sans(16, FontWeight.SemiBold),
    titleSmall = T.sans(14, FontWeight.SemiBold),
    bodyLarge = T.sans(16, FontWeight.Normal),
    bodyMedium = T.sans(14, FontWeight.Normal),
    bodySmall = T.sans(12, FontWeight.Normal),
    labelLarge = T.sans(14, FontWeight.SemiBold),
    labelMedium = T.mono(12, C.Ink),
    labelSmall = T.mono(11, C.Ink),
)

@Composable
fun CritTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, typography = typography, content = content)
}
