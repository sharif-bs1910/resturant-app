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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun CardChargeSection(state: CheckoutUiState, actions: PaymentActions) {
    val showCardSplit = state.payAmountMode == PayAmountMode.Full
    ChargePanel {
        ReaderStatusRow()
        if (showCardSplit) {
            Text(
                stringResource(R.string.checkout_how_much_card),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textPrimary,
            )
            FullOrCustomRow(state, actions)
            if (state.cardChargeCustom) {
                CustomAmountField(
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
    val showCashSplit = state.payAmountMode == PayAmountMode.Full
    val charge = state.chargeCents
    val roundUp = nextCashRoundUpCents(charge)
    val exactSelected = charge > 0 && state.cashTenderedCents == charge
    val roundSelected = roundUp > charge && state.cashTenderedCents == roundUp
    ChargePanel {
        if (showCashSplit) {
            FullOrCustomRow(state, actions)
            if (state.cardChargeCustom) {
                CustomAmountField(
                    label = stringResource(R.string.checkout_enter_custom_tip),
                    digits = state.customChargeDigits,
                    onDigitsChange = actions.onCustomAmountDigits,
                )
            }
        }
        Text(
            stringResource(R.string.checkout_how_much_cash),
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textPrimary,
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            CheckoutChip(
                stringResource(R.string.checkout_exact_amount, formatMoney(charge)),
                exactSelected,
                Modifier.weight(1f),
            ) { actions.onCashTenderExact() }
            if (roundUp > charge) {
                CheckoutChip(
                    formatMoney(roundUp),
                    roundSelected,
                    Modifier.weight(1f),
                ) { actions.onCashTenderRoundUp() }
            }
        }
        CustomAmountField(
            label = stringResource(R.string.checkout_enter_cash_amount),
            digits = state.cashTenderedDigits,
            onDigitsChange = actions.onCashAmountDigits,
        )
        Text(
            stringResource(R.string.checkout_change_due_value, formatMoney(state.changeDueCents)),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.textPrimary,
        )
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
    ChargePanel {
        Text(
            stringResource(R.string.checkout_paid_on_delivery_app, formatMoney(state.chargeCents)),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textMuted,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
            Text(
                stringResource(R.string.checkout_platform_order_id),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.textPrimary,
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppTheme.sizes.minTouchTarget)
                    .clip(RoundedCornerShape(AppTheme.radius.sm))
                    .border(
                        AppTheme.border.thin,
                        AppTheme.colors.textPrimary.copy(alpha = 0.14f),
                        RoundedCornerShape(AppTheme.radius.sm),
                    )
                    .background(AppTheme.colors.card)
                    .padding(AppTheme.spacing.md),
            ) {
                BasicTextField(
                    value = state.platformOrderId,
                    onValueChange = actions.onPlatformOrderId,
                    textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.textPrimary),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (state.platformOrderId.isEmpty()) {
                            Text(
                                stringResource(R.string.checkout_platform_order_hint),
                                style = AppTheme.typography.bodyMedium,
                                color = AppTheme.colors.textMuted,
                            )
                        }
                        inner()
                    },
                )
            }
        }
        PillButton(
            stringResource(R.string.checkout_mark_paid_amount, formatMoney(state.chargeCents)),
            actions.onCollectPayment,
            Modifier.fillMaxWidth(),
            enabled = state.chargeCents > 0,
        )
    }
}

@Composable
internal fun SplitByItemsSection(state: CheckoutUiState, actions: PaymentActions) {
    val allSelected = state.lines.isNotEmpty() && state.selectedPayLineIds.containsAll(state.lines.map { it.id })
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                modifier = Modifier.clickable(onClick = actions.onSelectAllPayLines),
            ) {
                PayCheckbox(checked = allSelected)
                Text(
                    stringResource(R.string.checkout_select_all),
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary,
                )
            }
            Text(
                stringResource(R.string.checkout_selected_count, state.selectedPayLineIds.size, state.lines.size),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
            )
        }
        state.lines.forEach { line ->
            val selected = line.id in state.selectedPayLineIds
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppTheme.radius.sm))
                    .border(
                        AppTheme.border.thin,
                        AppTheme.colors.textPrimary.copy(alpha = if (selected) 1f else 0.14f),
                        RoundedCornerShape(AppTheme.radius.sm),
                    )
                    .clickable { actions.onTogglePayLine(line.id) }
                    .padding(AppTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            ) {
                PayCheckbox(checked = selected)
                Text(
                    if (line.quantity > 1) {
                        stringResource(R.string.checkout_line_qty, line.quantity, line.name)
                    } else {
                        line.name
                    },
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    formatMoney(line.unitCents * line.quantity),
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun ChargePanel(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .background(AppTheme.colors.surface)
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
        CheckoutChip(
            stringResource(
                R.string.checkout_full_amount_value,
                formatMoney(state.amountToPayCents.coerceAtLeast(state.dueCents)),
            ),
            !state.cardChargeCustom,
            Modifier.weight(1f),
        ) { actions.onCardChargeCustom(false) }
        CheckoutChip(
            stringResource(R.string.checkout_custom_amount),
            state.cardChargeCustom,
            Modifier.weight(1f),
        ) { actions.onCardChargeCustom(true) }
    }
}

@Composable
private fun ReaderStatusRow() {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Icon(
            Icons.Outlined.CreditCard,
            contentDescription = null,
            tint = AppTheme.colors.textPrimary,
            modifier = Modifier.size(AppTheme.sizes.iconMd),
        )
        Column(Modifier.weight(1f)) {
            Text(
                stringResource(R.string.checkout_reader_name),
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                stringResource(R.string.checkout_card_reader),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
            )
        }
        Text(
            stringResource(R.string.checkout_reader_connected),
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.success,
        )
    }
}

@Composable
private fun PayCheckbox(checked: Boolean) {
    Box(
        Modifier
            .size(AppTheme.sizes.iconMd)
            .clip(RoundedCornerShape(AppTheme.radius.xs))
            .border(AppTheme.border.medium, AppTheme.colors.textPrimary, RoundedCornerShape(AppTheme.radius.xs))
            .background(if (checked) AppTheme.colors.textPrimary else AppTheme.colors.card),
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
