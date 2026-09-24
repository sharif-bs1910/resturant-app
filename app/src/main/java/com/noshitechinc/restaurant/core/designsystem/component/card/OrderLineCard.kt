package com.noshitechinc.restaurant.core.designsystem.component.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.component.button.ButtonSize
import com.noshitechinc.restaurant.core.designsystem.component.button.ButtonVariant
import com.noshitechinc.restaurant.core.designsystem.component.quantity.QuantityStepper
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun OrderLineCard(
    name: String,
    quantity: Int,
    lineTotal: String,
    modifier: Modifier = Modifier,
    modifiers: List<String> = emptyList(),
    note: String? = null,
    onQuantityChange: ((Int) -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
) {
    AppCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Text(
                text = stringResource(R.string.order_line_quantity, quantity),
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                text = name,
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = lineTotal,
                style = AppTheme.typography.priceMedium,
                color = AppTheme.colors.textPrimary,
                softWrap = false,
                maxLines = 1,
            )
        }
        if (modifiers.isNotEmpty()) {
            Text(
                text = modifiers.joinToString(", "),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (note != null) {
            Text(
                text = note,
                style = AppTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                color = AppTheme.colors.textSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (onQuantityChange != null || onRemove != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onQuantityChange != null) {
                    QuantityStepper(
                        quantity = quantity,
                        onQuantityChange = onQuantityChange,
                        min = 1,
                    )
                }
                if (onRemove != null) {
                    AppButton(
                        text = stringResource(R.string.action_remove),
                        onClick = onRemove,
                        variant = ButtonVariant.Outline,
                        size = ButtonSize.Small,
                    )
                }
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun OrderLineCardSimplePreview() {
    PreviewSurface {
        OrderLineCard(
            name = PreviewData.SHORT_NAME,
            quantity = 2,
            lineTotal = PreviewData.PRICE,
        )
    }
}

@ComponentPreviews
@Composable
private fun OrderLineCardModifiersAndNotePreview() {
    PreviewSurface {
        OrderLineCard(
            name = PreviewData.SHORT_NAME,
            quantity = 1,
            lineTotal = "$6.50",
            modifiers = PreviewData.MODIFIERS,
            note = PreviewData.LONG_DESCRIPTION,
        )
    }
}

@ComponentPreviews
@Composable
private fun OrderLineCardLongContentPreview() {
    PreviewSurface {
        OrderLineCard(
            name = PreviewData.LONG_NAME,
            quantity = 3,
            lineTotal = PreviewData.LONG_PRICE,
            modifier = Modifier.width(360.dp),
        )
    }
}

@ComponentPreviews
@Composable
private fun OrderLineCardEditablePreview() {
    PreviewSurface {
        OrderLineCard(
            name = PreviewData.SHORT_NAME,
            quantity = 2,
            lineTotal = "$9.00",
            modifiers = listOf("Extra foam"),
            onQuantityChange = {},
            onRemove = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun OrderLineCardReadOnlyPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            OrderLineCard(
                name = PreviewData.SHORT_NAME,
                quantity = 1,
                lineTotal = PreviewData.PRICE,
            )
            OrderLineCard(
                name = PreviewData.LONG_NAME,
                quantity = 2,
                lineTotal = PreviewData.LONG_PRICE,
                modifiers = PreviewData.MODIFIERS,
                note = "No ice",
            )
        }
    }
}
