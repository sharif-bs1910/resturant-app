package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.core.designsystem.component.input.CurrencyField
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun MoneyRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasize: Boolean = false,
    accentValue: Boolean = false,
    destructiveValue: Boolean = false,
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = when {
                emphasize -> AppTheme.typography.titleSmall
                accentValue -> AppTheme.typography.titleMedium
                else -> AppTheme.typography.bodyMedium
            },
            color = if (emphasize || accentValue) {
                AppTheme.colors.textPrimary
            } else {
                AppTheme.colors.textMuted
            },
        )
        Text(
            value,
            style = when {
                accentValue -> AppTheme.typography.priceMedium
                emphasize -> AppTheme.typography.titleSmall
                else -> AppTheme.typography.bodyMedium
            },
            color = when {
                destructiveValue -> AppTheme.colors.destructive
                accentValue -> AppTheme.colors.primary
                else -> AppTheme.colors.textPrimary
            },
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
fun PaidProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.pill)
    val fill = progress.coerceIn(0f, 1f)
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(AppTheme.spacing.md)
            .clip(shape)
            .background(AppTheme.colors.surfaceVariant),
    ) {
        if (fill > 0f) {
            Box(
                Modifier
                    .width(maxWidth * fill)
                    .fillMaxHeight()
                    .clip(shape)
                    .background(AppTheme.colors.success),
            )
        }
    }
}

@Composable
fun CustomAmountField(label: String, digits: String, onDigitsChange: (String) -> Unit) {
    CurrencyField(
        cents = digits.filter { it.isDigit() }.toLongOrNull() ?: 0L,
        onCentsChange = { cents -> onDigitsChange(if (cents <= 0L) "" else cents.toString()) },
        label = label,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun EqualPillChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedFillPrimary: Boolean = false,
) {
    val shape = RoundedCornerShape(AppTheme.radius.pill)
    val fill = when {
        selected && selectedFillPrimary -> AppTheme.colors.primary
        selected -> AppTheme.colors.textPrimary
        else -> AppTheme.colors.card
    }
    val labelColor = when {
        selected && selectedFillPrimary -> AppTheme.colors.card
        selected -> AppTheme.colors.background
        else -> AppTheme.colors.textPrimary
    }
    Box(
        modifier
            .height(AppTheme.sizes.buttonSmall)
            .clip(shape)
            .background(fill)
            .border(
                AppTheme.border.thin,
                if (selected && selectedFillPrimary) AppTheme.colors.primary else AppTheme.colors.textPrimary,
                shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = AppTheme.typography.labelMedium,
            color = labelColor,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun SurfaceChoiceRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Row(
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .clip(shape)
            .background(AppTheme.colors.surface)
            .padding(AppTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
fun SurfaceChoiceChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Box(
        modifier
            .height(AppTheme.sizes.buttonSmall)
            .clip(shape)
            .background(if (selected) AppTheme.colors.card else AppTheme.colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = AppTheme.typography.labelMedium,
            color = if (selected) AppTheme.colors.primary else AppTheme.colors.textPrimary,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@ComponentPreviews
@Composable
private fun CheckoutComponentsPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            MoneyRow("Subtotal", "$92.50")
            MoneyRow("Total", "$97.12", emphasize = true)
            MoneyRow("Change due", "$2.88", accentValue = true)
            PaidProgressBar(0.35f)
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                EqualPillChip("Pay full", selected = true, onClick = {}, modifier = Modifier.weight(1f))
                EqualPillChip("Split", selected = false, onClick = {}, modifier = Modifier.weight(1f))
            }
            SurfaceChoiceRow {
                SurfaceChoiceChip("Cash", selected = true, onClick = {})
                SurfaceChoiceChip("Card", selected = false, onClick = {})
            }
            CustomAmountField("Enter amount", "2500", onDigitsChange = {})
        }
    }
}
