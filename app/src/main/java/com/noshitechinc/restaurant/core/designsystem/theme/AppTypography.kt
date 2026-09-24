package com.noshitechinc.restaurant.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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

private fun style(size: Int, lineHeight: Int, weight: FontWeight, tabular: Boolean = false) = TextStyle(
    fontFamily = FontFamily.Default,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    fontWeight = weight,
    fontFeatureSettings = if (tabular) "tnum" else null,
)

val DefaultAppTypography = AppTypography(
    displayLarge = style(45, 52, FontWeight.Normal),
    headlineLarge = style(32, 40, FontWeight.SemiBold),
    headlineMedium = style(28, 36, FontWeight.SemiBold),
    headlineSmall = style(24, 32, FontWeight.SemiBold),
    titleLarge = style(22, 28, FontWeight.SemiBold),
    titleMedium = style(18, 24, FontWeight.SemiBold),
    titleSmall = style(16, 22, FontWeight.Medium),
    bodyLarge = style(16, 24, FontWeight.Normal),
    bodyMedium = style(14, 20, FontWeight.Normal),
    bodySmall = style(12, 16, FontWeight.Normal),
    labelLarge = style(16, 20, FontWeight.SemiBold),
    labelMedium = style(14, 18, FontWeight.Medium),
    labelSmall = style(12, 16, FontWeight.Medium),
    priceLarge = style(24, 32, FontWeight.SemiBold, tabular = true),
    priceMedium = style(18, 24, FontWeight.SemiBold, tabular = true),
    priceSmall = style(14, 20, FontWeight.Medium, tabular = true),
    numericDisplay = style(40, 48, FontWeight.SemiBold, tabular = true),
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
