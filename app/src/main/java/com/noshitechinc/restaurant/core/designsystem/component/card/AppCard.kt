package com.noshitechinc.restaurant.core.designsystem.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.interaction.focusRing
import com.noshitechinc.restaurant.core.designsystem.interaction.rememberInteractionVisuals
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val visuals = rememberInteractionVisuals(source)
    val shape = RoundedCornerShape(AppTheme.radius.lg)
    val c = AppTheme.colors
    val container = when {
        !enabled -> c.disabledContainer
        visuals.pressed && onClick != null -> c.surfacePressed
        else -> c.surface
    }
    val contentColor = if (enabled) c.textPrimary else c.textDisabled
    val border = BorderStroke(
        width = if (selected) AppTheme.border.thick else AppTheme.border.thin,
        color = when {
            selected -> c.primary
            !enabled -> c.outlineVariant
            else -> c.outline
        },
    )
    val cardModifier = modifier
        .fillMaxWidth()
        .focusRing(visuals.focused && onClick != null, c.focusRing, AppTheme.border.focus, shape)

    val body: @Composable () -> Unit = {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            content = content,
        )
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            enabled = enabled,
            shape = shape,
            color = container,
            contentColor = contentColor,
            border = border,
            interactionSource = source,
            modifier = cardModifier,
            content = body,
        )
    } else {
        Surface(
            shape = shape,
            color = container,
            contentColor = contentColor,
            border = border,
            modifier = cardModifier,
            content = body,
        )
    }
}

@ComponentPreviews
@Composable
private fun AppCardDefaultPreview() {
    PreviewSurface {
        AppCard {
            Text(text = "Card content", style = AppTheme.typography.bodyLarge)
        }
    }
}

@ComponentPreviews
@Composable
private fun AppCardSelectedPreview() {
    PreviewSurface {
        AppCard(selected = true, onClick = {}) {
            Text(text = "Selected card", style = AppTheme.typography.bodyLarge)
        }
    }
}

@ComponentPreviews
@Composable
private fun AppCardDisabledPreview() {
    PreviewSurface {
        AppCard(enabled = false, onClick = {}) {
            Text(text = "Disabled card", style = AppTheme.typography.bodyLarge)
        }
    }
}

@ComponentPreviews
@Composable
private fun AppCardPressedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Pressed) {
        AppCard(onClick = {}) {
            Text(text = "Pressed card", style = AppTheme.typography.bodyLarge)
        }
    }
}

@ComponentPreviews
@Composable
private fun AppCardFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        AppCard(onClick = {}) {
            Text(text = "Focused card", style = AppTheme.typography.bodyLarge)
        }
    }
}
