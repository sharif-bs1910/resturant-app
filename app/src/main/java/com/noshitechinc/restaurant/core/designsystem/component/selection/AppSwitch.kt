package com.noshitechinc.restaurant.core.designsystem.component.selection

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.interaction.focusRing
import com.noshitechinc.restaurant.core.designsystem.interaction.rememberInteractionVisuals
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val visuals = rememberInteractionVisuals(source)
    val c = AppTheme.colors
    val shape = RoundedCornerShape(AppTheme.radius.pill)
    Box(
        modifier = modifier
            .sizeIn(
                minWidth = AppTheme.sizes.minTouchTarget,
                minHeight = AppTheme.sizes.minTouchTarget,
            )
            .focusRing(visuals.focused, c.focusRing, AppTheme.border.focus, shape),
        contentAlignment = Alignment.Center,
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            interactionSource = source,
            colors = SwitchDefaults.colors(
                checkedTrackColor = c.primary,
                checkedThumbColor = c.onPrimary,
                uncheckedTrackColor = c.surfaceVariant,
                uncheckedBorderColor = c.outline,
                uncheckedThumbColor = c.textSecondary,
                disabledCheckedTrackColor = c.disabledContainer,
                disabledCheckedThumbColor = c.textDisabled,
                disabledUncheckedTrackColor = c.disabledContainer,
                disabledUncheckedThumbColor = c.textDisabled,
                disabledCheckedBorderColor = c.disabledContainer,
                disabledUncheckedBorderColor = c.disabledContainer,
            ),
        )
    }
}

@ComponentPreviews
@Composable
private fun AppSwitchStatesPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            AppSwitch(checked = true, onCheckedChange = {})
            AppSwitch(checked = false, onCheckedChange = {})
            AppSwitch(checked = true, onCheckedChange = {}, enabled = false)
            AppSwitch(checked = false, onCheckedChange = {}, enabled = false)
        }
    }
}

@ComponentPreviews
@Composable
private fun AppSwitchFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        AppSwitch(checked = true, onCheckedChange = {})
    }
}
