package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun CardChargeSection(state: CheckoutUiState, actions: PaymentActions) {
    val showCardSplit = state.payAmountMode == PayAmountMode.Full ||
        state.payAmountMode == PayAmountMode.ByItems ||
        state.payAmountMode == PayAmountMode.Custom
    ChargePanel {
        ReaderStatusRow()
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = AppTheme.border.thin,
            color = AppTheme.colors.textPrimary.copy(alpha = 0.14f),
        )
        if (showCardSplit) {
            Text(
                stringResource(R.string.checkout_how_much_card),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textPrimary,
            )
            FullOrCustomRow(state, actions)
            if (state.cardChargeCustom) {
                ChargeAmountField(
                    label = stringResource(R.string.checkout_enter_custom_tip),
                    digits = state.customChargeDigits,
                    onDigitsChange = actions.onCustomAmountDigits,
                )
            }
        }
        PillButton(
            stringResource(R.string.checkout_charge_amount, formatMoney(state.chargeCents)),
            actions.onCollectPayment,
            Modifier.fillMaxWidth(),
            enabled = state.chargeCents > 0,
        )
    }
}

@Composable
internal fun CashChargeSection(state: CheckoutUiState, actions: PaymentActions) {
    // Same Full/Custom + tender chrome for Split → Custom amount as for Split evenly
    val showCashSplit = state.payAmountMode == PayAmountMode.Full ||
        state.payAmountMode == PayAmountMode.ByItems ||
        state.payAmountMode == PayAmountMode.Custom
    val customCharge = state.cardChargeCustom
    val charge = state.chargeCents
    val noteSuggestions = usaCashNoteSuggestionsCents(charge)
    val exactSelected = charge > 0 && state.cashTenderedCents == charge
    ChargePanel {
        if (showCashSplit) {
            FullOrCustomRow(state, actions)
            if (customCharge) {
                ChargeAmountField(
                    label = stringResource(R.string.checkout_enter_custom_tip),
                    digits = state.customChargeDigits,
                    onDigitsChange = actions.onCustomAmountDigits,
                )
            }
        }
        // Figma: tender chips only after a charge exists — empty custom amount must not show Exact $0.00
        if (charge > 0) {
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            ) {
                Text(
                    stringResource(
                        if (showCashSplit && customCharge) {
                            R.string.checkout_cash_received
                        } else {
                            R.string.checkout_how_much_cash
                        },
                    ),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textPrimary,
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                ) {
                    EqualPillChip(
                        label = stringResource(R.string.checkout_exact_amount, formatMoney(charge)),
                        selected = exactSelected,
                        onClick = actions.onCashTenderExact,
                        modifier = Modifier.weight(1f),
                    )
                    for (noteCents in noteSuggestions) {
                        EqualPillChip(
                            label = formatMoney(noteCents),
                            selected = state.cashTenderedCents == noteCents,
                            onClick = { actions.onCashTenderNote(noteCents) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            Text(
                stringResource(R.string.checkout_change_due_value, formatMoney(state.changeDueCents)),
                style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = AppTheme.colors.textPrimary,
            )
        }
        PillButton(
            stringResource(R.string.checkout_charge_amount, formatMoney(charge)),
            actions.onCollectPayment,
            Modifier.fillMaxWidth(),
            enabled = charge > 0 &&
                (state.cashTenderedDigits.isEmpty() || state.cashTenderedCents >= charge),
        )
    }
}

@Composable
internal fun DeliveryChargeSection(state: CheckoutUiState, actions: PaymentActions) {
    // Figma (4050:16082): payment-type row already picks the app — only platform ID + CTA (no gray panel / re-picker)
    val appLabel = state.deliveryApp?.let { stringResource(it.labelRes) }
        ?: stringResource(state.settlement.labelRes)
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val hairline = AppTheme.colors.textPrimary.copy(alpha = 0.14f)
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Text(
                stringResource(R.string.checkout_platform_order_id),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.textPrimary,
            )
            BasicTextField(
                value = state.platformOrderId,
                onValueChange = actions.onPlatformOrderId,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(AppTheme.colors.card)
                    .border(AppTheme.border.thin, hairline, shape)
                    .padding(AppTheme.spacing.md),
                textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.textPrimary),
                singleLine = true,
                decorationBox = { inner ->
                    Box(Modifier.fillMaxWidth()) {
                        if (state.platformOrderId.isEmpty()) {
                            Text(
                                stringResource(R.string.checkout_platform_order_hint),
                                style = AppTheme.typography.bodyMedium,
                                color = AppTheme.colors.textMuted,
                            )
                        }
                        inner()
                    }
                },
            )
        }
        PillButton(
            stringResource(
                R.string.checkout_place_delivery_order,
                appLabel,
                formatMoney(state.chargeCents),
            ),
            actions.onCollectPayment,
            Modifier.fillMaxWidth(),
            enabled = state.chargeCents > 0,
        )
    }
}

@Composable
internal fun SplitByItemsSection(state: CheckoutUiState, actions: PaymentActions) {
    val allSelected = state.lines.isNotEmpty() && state.selectedPayLineIds.containsAll(state.lines.map { it.id })
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    val hairline = AppTheme.colors.textPrimary.copy(alpha = 0.14f)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(AppTheme.border.thin, hairline, shape),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                // Figma 4050:14670: oat-milk Background/Default
                .background(AppTheme.colors.background)
                .clickable(onClick = actions.onSelectAllPayLines)
                .padding(AppTheme.spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                PayCheckbox(checked = allSelected)
                Text(
                    stringResource(R.string.checkout_select_all),
                    // Figma 4050:14674: Inter 15 SemiBold → nearest titleSmall + SemiBold
                    style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colors.textPrimary,
                )
            }
            Text(
                stringResource(R.string.checkout_selected_count, state.selectedPayLineIds.size, state.lines.size),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
            )
        }
        state.lines.forEachIndexed { index, line ->
            if (index > 0) {
                HorizontalDivider(thickness = AppTheme.border.thin, color = hairline)
            }
            val selected = line.id in state.selectedPayLineIds
            val subtitle = cartLineModifierSummary(line)
            Row(
                Modifier
                    .fillMaxWidth()
                    // Figma selected row: oat-milk Background/Default
                    .background(if (selected) AppTheme.colors.background else AppTheme.colors.card)
                    .clickable { actions.onTogglePayLine(line.id) }
                    .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            ) {
                PayCheckbox(checked = selected, accent = true)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs)) {
                    Text(
                        if (line.quantity > 1) {
                            stringResource(R.string.checkout_line_qty, line.quantity, line.name)
                        } else {
                            line.name
                        },
                        // Figma 4050:14681: Inter 15 SemiBold → nearest titleSmall + SemiBold
                        style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = AppTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (subtitle.isNotEmpty()) {
                        Text(
                            subtitle,
                            // Figma 4050:14682: Inter 13 Regular muted → bodyMedium (14 Regular)
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Text(
                    formatMoney(line.unitCents * line.quantity),
                    // Figma 4050:14684: Inter 16 SemiBold
                    style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                // Figma 4050:14640 footer: oat-milk Background/Default
                .background(AppTheme.colors.background)
                .padding(AppTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            MoneyRow(stringResource(R.string.checkout_subtotal), formatMoney(state.selectedSubtotalCents))
            MoneyRow(stringResource(R.string.checkout_tax), formatMoney(state.selectedTaxCents))
            MoneyRow(
                stringResource(R.string.checkout_total),
                formatMoney(state.selectedTotalCents),
                emphasize = true,
            )
        }
    }
}

@Composable
private fun cartLineModifierSummary(line: CartLine): String {
    val modifiers = if (line.selectedModifierIds.isEmpty()) {
        emptyList()
    } else {
        modifierGroups()
            .flatMap { it.options }
            .filter { it.id in line.selectedModifierIds }
            .map { stringResource(it.labelRes) }
    }
    val parts = buildList {
        addAll(modifiers)
        if (line.note.isNotBlank()) add(line.note)
    }
    return parts.joinToString(" · ")
}

@Composable
private fun ChargePanel(content: @Composable ColumnScope.() -> Unit) {
    // Figma miscellaneous/_kit-section-fill #F5F5F5 → nearest token neutralContainer
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .background(AppTheme.colors.neutralContainer)
            .padding(AppTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        content = content,
    )
}

@Composable
private fun FullOrCustomRow(state: CheckoutUiState, actions: PaymentActions) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        EqualPillChip(
            label = stringResource(
                R.string.checkout_full_amount_value,
                // Must match chargeCents when Full is selected — never fall back to dueCents
                formatMoneyPlain(state.amountToPayCents),
            ),
            selected = !state.cardChargeCustom,
            onClick = { actions.onCardChargeCustom(false) },
            modifier = Modifier.weight(1f),
        )
        EqualPillChip(
            label = stringResource(R.string.checkout_custom_amount),
            selected = state.cardChargeCustom,
            onClick = { actions.onCardChargeCustom(true) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReaderStatusRow() {
    val iconShape = RoundedCornerShape(AppTheme.radius.sm)
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Box(
            Modifier
                .size(AppTheme.sizes.minTouchTarget)
                .clip(iconShape)
                .background(AppTheme.colors.card),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.CreditCard,
                contentDescription = null,
                tint = AppTheme.colors.textPrimary,
                modifier = Modifier.size(AppTheme.sizes.iconMd),
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
            Text(
                stringResource(R.string.checkout_reader_name),
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                stringResource(R.string.checkout_card_reader),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
            )
        }
        // Figma Connected chip: white card fill + status success text (not successContainer tint)
        Text(
            stringResource(R.string.checkout_reader_connected),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.success,
            modifier = Modifier
                .clip(RoundedCornerShape(AppTheme.radius.pill))
                .background(AppTheme.colors.card)
                .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.xs),
        )
    }
}

@Composable
private fun PayCheckbox(checked: Boolean, accent: Boolean = false) {
    val shape = RoundedCornerShape(AppTheme.radius.xs)
    val fill = when {
        checked && accent -> AppTheme.colors.primary
        checked -> AppTheme.colors.textPrimary
        else -> AppTheme.colors.card
    }
    val stroke = when {
        checked && accent -> AppTheme.colors.primary
        checked -> AppTheme.colors.textPrimary
        else -> AppTheme.colors.textPrimary.copy(alpha = 0.14f)
    }
    Box(
        Modifier
            .size(AppTheme.sizes.iconMd)
            .clip(shape)
            .border(AppTheme.border.thin, stroke, shape)
            .background(fill),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = AppTheme.colors.card,
                modifier = Modifier.size(AppTheme.sizes.iconSm),
            )
        }
    }
}
