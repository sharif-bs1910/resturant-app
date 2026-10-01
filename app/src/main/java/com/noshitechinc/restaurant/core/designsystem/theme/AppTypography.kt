package com.noshitechinc.restaurant.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.noshitechinc.restaurant.R

@Immutable
data class AppTypography(
    val displayLarge: TextStyle,
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
    val priceLarge: TextStyle,
    val priceMedium: TextStyle,
    val priceSmall: TextStyle,
    val numericDisplay: TextStyle,
)

private val Fraunces = FontFamily(
    Font(R.font.fraunces_semibold, FontWeight.SemiBold),
    Font(R.font.fraunces_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
)

private val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extrabold, FontWeight.ExtraBold),
)

private val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
)

private fun style(
    family: FontFamily,
    size: Int,
    lineHeight: Int,
    weight: FontWeight,
    tabular: Boolean = false,
    letterSpacingEm: Float = 0f,
) = TextStyle(
    fontFamily = family,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    fontWeight = weight,
    letterSpacing = letterSpacingEm.em,
    fontFeatureSettings = if (tabular) "tnum" else null,
)

val DefaultAppTypography = AppTypography(
    displayLarge = style(Fraunces, 56, 54, FontWeight.SemiBold, letterSpacingEm = -0.025f),
    headlineLarge = style(Inter, 40, 42, FontWeight.ExtraBold, letterSpacingEm = -0.025f),
    headlineMedium = style(Fraunces, 44, 46, FontWeight.SemiBold, letterSpacingEm = -0.015f),
    headlineSmall = style(Inter, 24, 29, FontWeight.Bold, letterSpacingEm = -0.015f),
    titleLarge = style(Inter, 22, 28, FontWeight.SemiBold),
    titleMedium = style(Inter, 18, 24, FontWeight.SemiBold),
    titleSmall = style(Inter, 16, 22, FontWeight.Medium),
    bodyLarge = style(Inter, 18, 27, FontWeight.Normal),
    bodyMedium = style(Inter, 14, 22, FontWeight.Normal),
    bodySmall = style(Inter, 12, 19, FontWeight.Normal),
    labelLarge = style(Inter, 13, 14, FontWeight.Bold, letterSpacingEm = -0.015f),
    labelMedium = style(Inter, 14, 18, FontWeight.Medium),
    labelSmall = style(JetBrainsMono, 11, 11, FontWeight.Normal, letterSpacingEm = 0.18f),
    priceLarge = style(Inter, 24, 32, FontWeight.SemiBold, tabular = true),
    priceMedium = style(Inter, 18, 24, FontWeight.SemiBold, tabular = true),
    priceSmall = style(Inter, 14, 20, FontWeight.Medium, tabular = true),
    numericDisplay = style(Inter, 40, 48, FontWeight.SemiBold, tabular = true),
)

fun AppTypography.toMaterialTypography(): Typography = Typography(
    displayLarge = displayLarge,
    headlineLarge = headlineLarge,
    headlineMedium = headlineMedium,
    headlineSmall = headlineSmall,
    titleLarge = titleLarge,
    titleMedium = titleMedium,
    titleSmall = titleSmall,
    bodyLarge = bodyLarge,
    bodyMedium = bodyMedium,
    bodySmall = bodySmall,
    labelLarge = labelLarge,
    labelMedium = labelMedium,
    labelSmall = labelSmall,
)
