package com.noshitechinc.restaurant.core.designsystem.component.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.component.button.ButtonSize
import com.noshitechinc.restaurant.core.designsystem.component.selection.StatusBadge
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.designsystem.theme.StatusTone
import com.noshitechinc.restaurant.core.designsystem.theme.containerColor
import com.noshitechinc.restaurant.core.designsystem.theme.contentColor

data class KitchenTicketItem(val quantity: Int, val name: String, val modifiers: List<String> = emptyList(), val isDone: Boolean = false)

@Composable
fun KitchenTicketCard(
    ticketNumber: String,
    title: String,
    statusLabel: String,
    statusTone: StatusTone,
    elapsedMinutes: Int,
    items: List<KitchenTicketItem>,
    modifier: Modifier = Modifier,
    warningAfterMinutes: Int = 10,
    overdueAfterMinutes: Int = 20,
    onItemToggle: ((Int) -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    actionLoading: Boolean = false,
) {
    val elapsedTone = ElapsedTone.of(
        elapsedMinutes = elapsedMinutes,
        warningAfterMinutes = warningAfterMinutes,
        overdueAfterMinutes = overdueAfterMinutes,
    )
    AppCard(modifier = modifier) {
        Surface(
            color = elapsedTone.statusTone.containerColor(),
            contentColor = elapsedTone.statusTone.contentColor(),
            shape = RoundedCornerShape(AppTheme.radius.md),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(AppTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ticketNumber,
                        style = AppTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = title,
                        style = AppTheme.typography.bodyLarge,
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
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                StatusBadge(text = statusLabel, tone = statusTone)
            }
        }
        items.forEachIndexed { index, item ->
            KitchenTicketItemRow(
                item = item,
                onToggle = onItemToggle?.let { toggle -> { toggle(index) } },
            )
        }
        if (actionLabel != null && onAction != null) {
            AppButton(
                text = actionLabel,
                onClick = onAction,
                size = ButtonSize.Large,
                loading = actionLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun KitchenTicketItemRow(item: KitchenTicketItem, onToggle: (() -> Unit)?) {
    val nameColor = if (item.isDone) AppTheme.colors.textDisabled else AppTheme.colors.textPrimary
    val nameDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clickable(enabled = onToggle != null) { onToggle?.invoke() }
            .padding(vertical = AppTheme.spacing.xs),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Text(
                text = item.quantity.toString(),
                style = AppTheme.typography.labelLarge,
                color = nameColor,
            )
            Text(
                text = item.name,
                style = AppTheme.typography.bodyLarge,
                color = nameColor,
                textDecoration = nameDecoration,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
        if (item.modifiers.isNotEmpty()) {
            Text(
                text = item.modifiers.joinToString(", "),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = AppTheme.spacing.lg),
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun KitchenTicketCardOnTimePreview() {
    PreviewSurface {
        KitchenTicketCard(
            ticketNumber = "K-12",
            title = PreviewData.TABLE,
            statusLabel = "Cooking",
            statusTone = StatusTone.Info,
            elapsedMinutes = 5,
            items = previewItems(),
            onItemToggle = {},
            actionLabel = "Bump",
            onAction = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun KitchenTicketCardWarningPreview() {
    PreviewSurface {
        KitchenTicketCard(
            ticketNumber = "K-13",
            title = PreviewData.TABLE,
            statusLabel = "Cooking",
            statusTone = StatusTone.Warning,
            elapsedMinutes = 12,
            items = previewItems(),
            onItemToggle = {},
            actionLabel = "Bump",
            onAction = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun KitchenTicketCardOverduePreview() {
    PreviewSurface {
        KitchenTicketCard(
            ticketNumber = "K-14",
            title = PreviewData.TABLE,
            statusLabel = "Late",
            statusTone = StatusTone.Danger,
            elapsedMinutes = 25,
            items = previewItems(someDone = true),
            onItemToggle = {},
            actionLabel = "Bump",
            onAction = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun KitchenTicketCardManyItemsPreview() {
    PreviewSurface {
        KitchenTicketCard(
            ticketNumber = "K-15",
            title = PreviewData.CUSTOMER_LONG,
            statusLabel = "Cooking",
            statusTone = StatusTone.Info,
            elapsedMinutes = 7,
            items = listOf(
                KitchenTicketItem(
                    quantity = 2,
                    name = PreviewData.LONG_NAME,
                    modifiers = PreviewData.MODIFIERS,
                ),
                KitchenTicketItem(
                    quantity = 1,
                    name = PreviewData.LONG_NAME,
                    modifiers = PreviewData.MODIFIERS,
                    isDone = true,
                ),
                KitchenTicketItem(quantity = 3, name = PreviewData.SHORT_NAME, isDone = true),
                KitchenTicketItem(
                    quantity = 1,
                    name = PreviewData.LONG_NAME,
                    modifiers = listOf("Extra spicy"),
                ),
            ),
            onItemToggle = {},
            actionLabel = "Mark ready",
            onAction = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun KitchenTicketCardActionLoadingPreview() {
    PreviewSurface {
        KitchenTicketCard(
            ticketNumber = "K-16",
            title = PreviewData.TABLE,
            statusLabel = "Cooking",
            statusTone = StatusTone.Info,
            elapsedMinutes = 4,
            items = previewItems(),
            actionLabel = "Bump",
            onAction = {},
            actionLoading = true,
        )
    }
}

private fun previewItems(someDone: Boolean = false): List<KitchenTicketItem> = listOf(
    KitchenTicketItem(quantity = 1, name = PreviewData.SHORT_NAME, modifiers = listOf("Oat milk")),
    KitchenTicketItem(
        quantity = 2,
        name = "Avocado toast",
        modifiers = listOf("No chili"),
        isDone = someDone,
    ),
    KitchenTicketItem(quantity = 1, name = "Eggs Benedict", isDone = someDone),
)
