package com.noshitechinc.restaurant.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalAppColors = staticCompositionLocalOf { LightAppColors }
private val LocalAppTypography = staticCompositionLocalOf { DefaultAppTypography }
private val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }
private val LocalAppRadius = staticCompositionLocalOf { AppRadius() }
private val LocalAppBorder = staticCompositionLocalOf { AppBorder() }
private val LocalAppSizes = staticCompositionLocalOf { AppSizes() }

object AppTheme {
    val colors: AppColors
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current
    val typography: AppTypography
        @Composable @ReadOnlyComposable
        get() = LocalAppTypography.current
    val spacing: AppSpacing
        @Composable @ReadOnlyComposable
        get() = LocalAppSpacing.current
    val radius: AppRadius
        @Composable @ReadOnlyComposable
        get() = LocalAppRadius.current
    val border: AppBorder
        @Composable @ReadOnlyComposable
        get() = LocalAppBorder.current
    val sizes: AppSizes
        @Composable @ReadOnlyComposable
        get() = LocalAppSizes.current
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colors = LightAppColors
    val typography = DefaultAppTypography
    val radius = AppRadius()
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides typography,
        LocalAppSpacing provides AppSpacing(),
        LocalAppRadius provides radius,
        LocalAppBorder provides AppBorder(),
        LocalAppSizes provides AppSizes(),
    ) {
        MaterialTheme(
            colorScheme = colors.toColorScheme(),
            typography = typography.toMaterialTypography(),
            shapes = radius.toShapes(),
            content = content,
        )
    }
}

private fun AppColors.toColorScheme(): ColorScheme = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    background = background,
    onBackground = textPrimary,
    surface = surface,
    onSurface = textPrimary,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = textSecondary,
    outline = outline,
    outlineVariant = outlineVariant,
    error = destructive,
    onError = onDestructive,
    errorContainer = destructiveContainer,
    onErrorContainer = onDestructiveContainer,
    scrim = scrim,
)

private fun AppRadius.toShapes(): Shapes = Shapes(
    extraSmall = RoundedCornerShape(xs),
    small = RoundedCornerShape(sm),
    medium = RoundedCornerShape(md),
    large = RoundedCornerShape(lg),
    extraLarge = RoundedCornerShape(xl),
)
