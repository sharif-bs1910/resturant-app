package com.noshitechinc.restaurant.core.designsystem.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.interaction.LocalForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun PreviewSurface(
    modifier: Modifier = Modifier,
    forcedInteraction: ForcedInteraction = ForcedInteraction.None,
    content: @Composable () -> Unit,
) {
    AppTheme {
        CompositionLocalProvider(LocalForcedInteraction provides forcedInteraction) {
            Surface(modifier = modifier, color = AppTheme.colors.background) {
                Box(modifier = Modifier.padding(AppTheme.spacing.lg)) { content() }
            }
        }
    }
}
