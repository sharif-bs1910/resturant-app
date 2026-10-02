package com.noshitechinc.restaurant.core.designsystem.component.logo

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.em
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

enum class LogoSurface { Light, Dark, Brand }

@Immutable
private data class WordmarkColors(val word: Color, val accent: Color)

@Composable
private fun LogoSurface.wordmarkColors(): WordmarkColors {
    val c = AppTheme.colors
    return when (this) {
        LogoSurface.Light -> WordmarkColors(c.textPrimary, c.primary)
        LogoSurface.Dark -> WordmarkColors(c.background, c.primary)
        LogoSurface.Brand -> WordmarkColors(c.textPrimary, c.background)
    }
}

private val WordmarkFamily = FontFamily(
    Font(R.font.fraunces_semibold, FontWeight.SemiBold),
    Font(R.font.fraunces_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
)

@Composable
fun SteamBowlMark(surface: LogoSurface, modifier: Modifier = Modifier, markSize: Dp = AppTheme.sizes.logoMark) {
    Image(
        painter = painterResource(surface.markRes()),
        contentDescription = stringResource(R.string.logo_content_description),
        modifier = modifier.size(markSize),
    )
}

@DrawableRes
private fun LogoSurface.markRes(): Int = when (this) {
    LogoSurface.Light -> R.drawable.ic_steam_bowl_light
    LogoSurface.Dark -> R.drawable.ic_steam_bowl_dark
    LogoSurface.Brand -> R.drawable.ic_steam_bowl_brand
}

@Composable
fun NoshiWordmark(surface: LogoSurface, modifier: Modifier = Modifier) {
    val colors = surface.wordmarkColors()
    val description = stringResource(R.string.logo_content_description)
    val tracking = (-0.035).em
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = colors.word,
                    fontFamily = WordmarkFamily,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = tracking,
                ),
            ) { append("nosh") }
            withStyle(
                SpanStyle(
                    color = colors.accent,
                    fontFamily = WordmarkFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = tracking,
                ),
            ) { append("i") }
        },
        style = AppTheme.typography.headlineMedium,
        modifier = modifier.semantics { contentDescription = description },
    )
}

@Composable
fun NoshiLockup(surface: LogoSurface, modifier: Modifier = Modifier, markSize: Dp = AppTheme.sizes.logoMark, stacked: Boolean = false) {
    val showMark = markSize >= AppTheme.sizes.logoMinMark
    if (stacked) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            if (showMark) SteamBowlMark(surface = surface, markSize = markSize)
            NoshiWordmark(surface = surface)
        }
    } else {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            if (showMark) SteamBowlMark(surface = surface, markSize = markSize)
            NoshiWordmark(surface = surface)
        }
    }
}

@ComponentPreviews
@Composable
private fun NoshiLockupSurfacesPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
            LogoSurface.entries.forEach { surface ->
                NoshiLockup(surface = surface)
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun NoshiLockupStackedPreview() {
    PreviewSurface {
        NoshiLockup(surface = LogoSurface.Light, stacked = true)
    }
}
