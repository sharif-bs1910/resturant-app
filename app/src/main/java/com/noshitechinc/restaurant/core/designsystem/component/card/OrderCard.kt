package com.noshitechinc.restaurant.core.designsystem.component.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.selection.StatusBadge
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.designsystem.theme.StatusTone

@Composable
fun OrderCard(
    orderNumber: String,
    title: String,
    statusLabel: String,
    statusTone: StatusTone,
    total: String,
    itemCount: Int,
    elapsedMinutes: Int,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    AppCard(
        modifier = modifier,
        onClick = onClick,
        selected = selected,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Text(
                text = orderNumber,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            StatusBadge(text = statusLabel, tone = statusTone)
        }
        Text(
            text = title,
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = pluralStringResource(R.plurals.order_items_count, itemCount, itemCount),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.kitchen_elapsed_minutes,
                        elapsedMinutes,
                        elapsedMinutes,
                    ),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = total,
                style = AppTheme.typography.priceMedium,
                color = AppTheme.colors.textPrimary,
                softWrap = false,
                maxLines = 1,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun OrderCardTonesPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            StatusTone.entries.forEach { tone ->
                OrderCard(
                    orderNumber = "#1042",
                    title = PreviewData.TABLE,
                    statusLabel = tone.name,
                    statusTone = tone,
                    total = PreviewData.PRICE,
                    itemCount = 3,
                    elapsedMinutes = 8,
                    onClick = {},
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun OrderCardSelectedPreview() {
    PreviewSurface {
        OrderCard(
            orderNumber = "#1042",
            title = PreviewData.TABLE,
            statusLabel = "Open",
            statusTone = StatusTone.Info,
            total = PreviewData.PRICE,
            itemCount = 3,
            elapsedMinutes = 8,
            selected = true,
            onClick = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun OrderCardLongContentPreview() {
    PreviewSurface {
        OrderCard(
            orderNumber = "#1042",
            title = PreviewData.CUSTOMER_LONG,
            statusLabel = "Ready",
            statusTone = StatusTone.Success,
            total = PreviewData.LONG_PRICE,
            itemCount = 12,
            elapsedMinutes = 45,
            onClick = {},
            modifier = Modifier.width(360.dp),
        )
    }
}

@ComponentPreviews
@Composable
private fun OrderCardPressedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Pressed) {
        OrderCard(
            orderNumber = "#1042",
            title = PreviewData.TABLE,
            statusLabel = "Open",
            statusTone = StatusTone.Info,
            total = PreviewData.PRICE,
            itemCount = 3,
            elapsedMinutes = 8,
            onClick = {},
        )
    }
}
