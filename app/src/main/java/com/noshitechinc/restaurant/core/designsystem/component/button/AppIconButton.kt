package com.noshitechinc.restaurant.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.interaction.focusRing
import com.noshitechinc.restaurant.core.designsystem.interaction.rememberInteractionVisuals
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Outline,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val visuals = rememberInteractionVisuals(source)
    val colors = buttonColors(variant, enabled, visuals.pressed)
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = colors.container,
        contentColor = colors.content,
        border = colors.border?.let { BorderStroke(AppTheme.border.thin, it) },
        interactionSource = source,
        modifier = modifier
            .size(AppTheme.sizes.minTouchTarget)
            .focusRing(visuals.focused, AppTheme.colors.focusRing, AppTheme.border.focus, shape),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(AppTheme.sizes.iconMd),
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun AppIconButtonVariantsPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            ButtonVariant.entries.forEach { variant ->
                AppIconButton(
                    icon = Icons.Filled.Add,
                    contentDescription = variant.name,
                    onClick = {},
                    variant = variant,
                )
                AppIconButton(
                    icon = Icons.Filled.Add,
                    contentDescription = "${variant.name} disabled",
                    onClick = {},
                    variant = variant,
                    enabled = false,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun AppIconButtonPressedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Pressed) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            ButtonVariant.entries.forEach {
                AppIconButton(
                    icon = Icons.Filled.Add,
                    contentDescription = "${it.name} pressed",
                    onClick = {},
                    variant = it,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun AppIconButtonFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            ButtonVariant.entries.forEach {
                AppIconButton(
                    icon = Icons.Filled.Add,
                    contentDescription = "${it.name} focused",
                    onClick = {},
                    variant = it,
                )
            }
        }
    }
}
