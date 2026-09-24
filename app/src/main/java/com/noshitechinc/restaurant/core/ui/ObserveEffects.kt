package com.noshitechinc.restaurant.core.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ObserveEffects(effects: Flow<UiEffect>, onEffect: suspend (UiEffect) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnEffect by rememberUpdatedState(onEffect)
    LaunchedEffect(effects, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            effects.collect { currentOnEffect(it) }
        }
    }
}

@ComponentPreviews
@Composable
private fun ObserveEffectsPreview() {
    PreviewSurface {
        ObserveEffects(effects = emptyFlow(), onEffect = {})
        Text(
            text = "Effects host",
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.textPrimary,
        )
    }
}
