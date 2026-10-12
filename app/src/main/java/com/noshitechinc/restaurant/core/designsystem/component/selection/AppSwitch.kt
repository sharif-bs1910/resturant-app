package com.noshitechinc.restaurant.core.designsystem.component.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
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
    val track = when {
        !enabled -> c.disabledContainer
        checked -> c.primary
        else -> c.textDisabled
    }
    val thumb = if (enabled) c.card else c.textDisabled
    Box(
        modifier = modifier
            .sizeIn(
                minWidth = AppTheme.sizes.minTouchTarget,
                minHeight = AppTheme.sizes.minTouchTarget,
            )
            .focusRing(visuals.focused, c.focusRing, AppTheme.border.focus, shape)
            .toggleable(
                value = checked,
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .width(AppTheme.sizes.passcodeCellWidth)
                .height(AppTheme.sizes.iconLg)
                .clip(shape)
                .background(track)
                .padding(AppTheme.spacing.xs),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                Modifier
                    .size(AppTheme.sizes.iconMd)
                    .clip(CircleShape)
                    .background(thumb),
            )
        }
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
