package com.noshitechinc.restaurant.core.designsystem.interaction

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

enum class ForcedInteraction { None, Pressed, Focused }

val LocalForcedInteraction = staticCompositionLocalOf { ForcedInteraction.None }

@Immutable
data class InteractionVisuals(val pressed: Boolean, val focused: Boolean)

@Composable
fun rememberInteractionVisuals(interactionSource: InteractionSource): InteractionVisuals {
    val pressed by interactionSource.collectIsPressedAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val forced = LocalForcedInteraction.current
    return InteractionVisuals(
        pressed = pressed || forced == ForcedInteraction.Pressed,
        focused = focused || forced == ForcedInteraction.Focused,
    )
}

fun Modifier.focusRing(visible: Boolean, color: Color, width: Dp, shape: Shape): Modifier =
    if (visible) border(width, color, shape) else this
