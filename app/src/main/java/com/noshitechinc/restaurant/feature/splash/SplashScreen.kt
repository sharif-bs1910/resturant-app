package com.noshitechinc.restaurant.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.logo.LogoSurface
import com.noshitechinc.restaurant.core.designsystem.component.logo.NoshiWordmark
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import kotlinx.coroutines.delay

private const val RevealMillis = 400
private const val HoldMillis = 1600L
private const val StartScale = 0.92f
private const val DotCycleMillis = 700
private const val MarkViewport = 160f
private const val DotRadius = 11.2f
private const val DotTravel = 28f

private data class SteamDot(val cx: Float, val cy: Float)

private val SteamDots = listOf(
    SteamDot(cx = 48f, cy = 32f),
    SteamDot(cx = 80f, cy = 16f),
    SteamDot(cx = 112f, cy = 32f),
)

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    SplashScreen(onFinished = onFinished)
}

@Composable
fun SplashScreen(onFinished: () -> Unit, modifier: Modifier = Modifier) {
    val inspection = LocalInspectionMode.current
    val reveal = remember { Animatable(if (inspection) 1f else 0f) }
    LaunchedEffect(inspection) {
        if (inspection) return@LaunchedEffect
        reveal.animateTo(1f, tween(durationMillis = RevealMillis, easing = FastOutSlowInEasing))
        delay(HoldMillis)
        onFinished()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            modifier = Modifier.graphicsLayer {
                alpha = reveal.value
                val scale = StartScale + (1f - StartScale) * reveal.value
                scaleX = scale
                scaleY = scale
            },
        ) {
            AnimatedSteamBowl(markSize = AppTheme.sizes.logoMark * 3)
            NoshiWordmark(surface = LogoSurface.Light)
        }
    }
}

@Composable
private fun AnimatedSteamBowl(markSize: Dp) {
    Box(Modifier.size(markSize)) {
        Image(
            painter = painterResource(R.drawable.ic_steam_bowl_base),
            contentDescription = stringResource(R.string.logo_content_description),
            modifier = Modifier.fillMaxSize(),
        )
        SteamDots.forEachIndexed { index, dot ->
            RisingDot(index = index, dot = dot, markSize = markSize)
        }
    }
}

@Composable
private fun BoxScope.RisingDot(index: Int, dot: SteamDot, markSize: Dp) {
    val shift by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = DotCycleMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(index * DotCycleMillis / SteamDots.size),
        ),
    )
    val radius = markSize * (DotRadius / MarkViewport)
    val rise = markSize * (DotTravel / MarkViewport) * shift
    Box(
        Modifier
            .align(Alignment.TopStart)
            .offset(
                x = markSize * (dot.cx / MarkViewport) - radius,
                y = markSize * (dot.cy / MarkViewport) - radius - rise,
            )
            .size(radius * 2)
            .background(AppTheme.colors.primary, CircleShape),
    )
}

@ScreenPreviews
@Composable
private fun SplashPreview() {
    PreviewSurface {
        SplashScreen(onFinished = {})
    }
}
