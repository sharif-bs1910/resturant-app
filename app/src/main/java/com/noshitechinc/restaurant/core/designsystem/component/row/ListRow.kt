package com.noshitechinc.restaurant.core.designsystem.component.row

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.interaction.focusRing
import com.noshitechinc.restaurant.core.designsystem.interaction.rememberInteractionVisuals
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun ListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingText: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    showChevron: Boolean = false,
    enabled: Boolean = true,
    destructive: Boolean = false,
    onClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val visuals = rememberInteractionVisuals(source)
    val c = AppTheme.colors
    val titleColor = when {
        !enabled -> c.textDisabled
        destructive -> c.destructive
        else -> c.textPrimary
    }
    val subtitleColor = if (enabled) c.textSecondary else c.textDisabled
    val iconTint = when {
        !enabled -> c.textDisabled
        destructive -> c.destructive
        else -> c.textSecondary
    }
    Row(
        modifier = modifier
            .heightIn(min = AppTheme.sizes.listRowMinHeight)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        enabled = enabled,
                        interactionSource = source,
                        indication = ripple(),
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .background(
                if (visuals.pressed && onClick != null) c.surfacePressed else Color.Transparent,
            )
            .focusRing(
                visible = visuals.focused && onClick != null,
                color = c.focusRing,
                width = AppTheme.border.focus,
                shape = RectangleShape,
            )
            .padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(AppTheme.sizes.iconMd),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = AppTheme.typography.bodyLarge,
                color = titleColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = AppTheme.typography.bodySmall,
                    color = subtitleColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (trailingText != null) {
            Text(
                text = trailingText,
                style = AppTheme.typography.bodyLarge,
                color = if (enabled) c.textPrimary else c.textDisabled,
                softWrap = false,
                maxLines = 1,
            )
        }
        trailingContent?.invoke()
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = if (enabled) c.textSecondary else c.textDisabled,
                modifier = Modifier.size(AppTheme.sizes.iconMd),
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun ListRowVariantsPreview() {
    PreviewSurface {
        Column {
            ListRow(title = "Title only")
            ListRow(title = "With subtitle", subtitle = "Supporting detail")
            ListRow(
                title = "Leading icon",
                subtitle = "Guest profile",
                leadingIcon = Icons.Filled.Person,
                onClick = {},
            )
            ListRow(
                title = "Trailing price",
                trailingText = PreviewData.LONG_PRICE,
                onClick = {},
            )
            ListRow(title = "With chevron", showChevron = true, onClick = {})
            ListRow(title = "Disabled", enabled = false, onClick = {})
            ListRow(title = "Destructive", destructive = true, onClick = {})
        }
    }
}

@ComponentPreviews
@Composable
private fun ListRowPressedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Pressed) {
        ListRow(title = "Pressed", onClick = {})
    }
}

@ComponentPreviews
@Composable
private fun ListRowFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        ListRow(title = "Focused", onClick = {})
    }
}

@ComponentPreviews
@Composable
private fun ListRowLongTitlePreview() {
    PreviewSurface {
        ListRow(
            title = PreviewData.LONG_NAME,
            subtitle = PreviewData.LONG_DESCRIPTION,
            trailingText = PreviewData.LONG_PRICE,
            showChevron = true,
            onClick = {},
            modifier = Modifier.width(360.dp),
        )
    }
}
