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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.format.CurrencyFormatter
import com.noshitechinc.restaurant.core.designsystem.component.input.CurrencyVisualTransformation
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
                emphasize -> AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                accentValue -> AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
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
                accentValue -> AppTheme.typography.priceMedium.copy(fontWeight = FontWeight.Bold)
                emphasize -> AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
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
            .background(AppTheme.colors.surface),
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
    // Figma Input on payment uses Background/Card (white), not elevated cream.
    ChargeAmountField(label = label, digits = digits, onDigitsChange = onDigitsChange)
}

/**
 * Amount field on gray charge panels — Figma Input: label above, white card field, espresso-alpha border.
 */
@Composable
fun ChargeAmountField(label: String, digits: String, onDigitsChange: (String) -> Unit) {
    val formatter = remember { CurrencyFormatter() }
    val cents = digits.filter { it.isDigit() }.toLongOrNull() ?: 0L
    val raw = if (cents == 0L) "" else cents.toString()
    val transformation = remember(formatter) { CurrencyVisualTransformation(formatter) }
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val hairline = AppTheme.colors.textPrimary.copy(alpha = 0.14f)
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        Text(
            label,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        BasicTextField(
            value = raw,
            onValueChange = { next ->
                val nextCents = CurrencyFormatter.centsFromDigits(next)
                onDigitsChange(if (nextCents <= 0L) "" else nextCents.toString())
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(AppTheme.colors.card)
                .border(AppTheme.border.thin, hairline, shape)
                .padding(AppTheme.spacing.md),
            textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.textPrimary),
            visualTransformation = transformation,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            decorationBox = { inner ->
                Box(Modifier.fillMaxWidth()) {
                    if (raw.isEmpty()) {
                        Text(
                            stringResource(R.string.input_amount_placeholder),
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textMuted,
                            maxLines = 1,
                        )
                    }
                    inner()
                }
            },
        )
    }
}

@Composable
fun EqualPillChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedFillPrimary: Boolean = false,
    mediumHeight: Boolean = false,
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
            .height(if (mediumHeight) AppTheme.sizes.buttonMedium else AppTheme.sizes.buttonSmall)
            .clip(shape)
            .background(fill)
            .then(
                if (selected) {
                    Modifier
                } else {
                    Modifier.border(AppTheme.border.thin, AppTheme.colors.textPrimary, shape)
                },
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
fun SurfaceChoiceRow(
    modifier: Modifier = Modifier,
    scroll: Boolean = true,
    fillMaxWidth: Boolean = true,
    trackColor: Color = AppTheme.colors.surface,
    content: @Composable RowScope.() -> Unit,
) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Row(
        modifier
            .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
            .then(if (scroll) Modifier.horizontalScroll(rememberScrollState()) else Modifier)
            .clip(shape)
            .background(trackColor)
            .padding(AppTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
fun SurfaceChoiceChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, tall: Boolean = false) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Box(
        modifier
            .height(if (tall) AppTheme.sizes.buttonMedium else AppTheme.sizes.buttonSmall)
            .clip(shape)
            .then(if (selected) Modifier.background(AppTheme.colors.card) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = if (tall) {
                AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            } else {
                AppTheme.typography.labelLarge
            },
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
