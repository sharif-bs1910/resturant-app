package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.adaptive.rememberAdaptiveInfo
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

data class PaymentActions(
    val onSettlement: (Settlement) -> Unit,
    val onPayAmountMode: (PayAmountMode) -> Unit,
    val onAmountDigit: (String) -> Unit,
    val onAmountDelete: () -> Unit,
    val onCashDigit: (String) -> Unit,
    val onCashDelete: () -> Unit,
    val onRedeemGiftCard: () -> Unit,
    val onCollectPayment: () -> Unit,
    val onBack: () -> Unit,
    val onCashAmountDigits: (String) -> Unit = {},
    val onCashTenderExact: () -> Unit = {},
    val onCashTenderRoundUp: () -> Unit = {},
    val onCustomAmountDigits: (String) -> Unit = {},
    val onSendPaymentLink: () -> Unit = {},
    val onTogglePayLine: (String) -> Unit = {},
    val onSelectAllPayLines: () -> Unit = {},
    val onClearItemCustomAmount: () -> Unit = {},
    val onCardChargeCustom: (Boolean) -> Unit = {},
    val onTipOption: (TipOption) -> Unit = {},
    val onTipDigit: (String) -> Unit = {},
    val onTipDelete: () -> Unit = {},
    val onTipAmountDigits: (String) -> Unit = {},
    val onDeliveryApp: (DeliveryApp) -> Unit = {},
    val onPlatformOrderId: (String) -> Unit = {},
)

@Composable
fun PaymentContent(state: CheckoutUiState, actions: PaymentActions) {
    val adaptive = rememberAdaptiveInfo()
    Column(
        Modifier
            .fillMaxSize()
            .padding(AppTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
            Text(
                stringResource(R.string.checkout_payment_details),
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                stringResource(
                    R.string.checkout_bill_context,
                    state.orderNumber,
                    state.billGuestLabel,
                    state.tableNumber,
                ),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (adaptive.usesTwoPane) {
            Row(
                Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                OrderSummaryPane(state, actions, Modifier.width(AppTheme.sizes.dialogMaxWidth / 2).fillMaxHeight())
                PaymentPane(state, actions, Modifier.weight(1f).fillMaxHeight())
            }
        } else {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                OrderSummaryPane(state, actions, Modifier.fillMaxWidth())
                PaymentPane(state, actions, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun OrderSummaryPane(state: CheckoutUiState, actions: PaymentActions, modifier: Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Column(
        modifier
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(AppTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Text(
            stringResource(R.string.checkout_order_summary),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.textPrimary,
        )
        MoneyRow(stringResource(R.string.checkout_subtotal), formatMoney(state.subtotalCents))
        MoneyRow(stringResource(R.string.checkout_tax), formatMoney(state.taxCents))
        MoneyRow(stringResource(R.string.checkout_total), formatMoney(state.totalCents + state.tipCents), emphasize = true)
        if (state.giftAppliedCents > 0) {
            MoneyRow(stringResource(R.string.checkout_gift_card), formatMoney(-state.giftAppliedCents))
        }
        if (state.paidCents > 0) {
            MoneyRow(stringResource(R.string.checkout_paid_so_far), formatMoney(-state.paidCents))
        }
        Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                stringResource(R.string.checkout_amount_remaining),
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                formatMoney(state.dueCents),
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.primary,
            )
        }
        PaidProgressBar(state.paidProgress)
        Text(
            if (state.coveredCents > 0) {
                stringResource(
                    R.string.checkout_paid_of,
                    formatMoney(state.coveredCents),
                    formatMoney(state.billCents),
                )
            } else {
                stringResource(R.string.checkout_no_payments_yet)
            },
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textMuted,
        )
        PillButton(
            stringResource(R.string.checkout_redeem),
            actions.onRedeemGiftCard,
            Modifier.fillMaxWidth(),
            filled = false,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AppTheme.sizes.minTouchTarget)
                .clip(RoundedCornerShape(AppTheme.radius.pill))
                .background(AppTheme.colors.surface)
                .clickable(onClick = actions.onSendPaymentLink),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                stringResource(R.string.checkout_payment_link),
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.textPrimary,
            )
        }
    }
}

@Composable
private fun PaymentPane(state: CheckoutUiState, actions: PaymentActions, modifier: Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Column(
        modifier
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppTheme.spacing.xl, vertical = AppTheme.spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PayAmountModeRow(state.payAmountMode, actions.onPayAmountMode)
        AmountToPayHeader(state.amountToPayCents)
        when (state.payAmountMode) {
            PayAmountMode.Full -> Unit

            PayAmountMode.Custom -> {
                CustomAmountField(
                    label = stringResource(R.string.checkout_enter_custom_tip),
                    digits = state.customChargeDigits,
                    onDigitsChange = actions.onCustomAmountDigits,
                )
            }

            PayAmountMode.ByItems -> SplitByItemsSection(state, actions)
        }
        TipSection(state, actions)
        Text(
            stringResource(R.string.checkout_choose_payment_type),
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.textPrimary,
        )
        PaymentTypeRow(state.settlement, actions.onSettlement)
        when {
            state.settlement == Settlement.Card -> CardChargeSection(state, actions)
            state.settlement == Settlement.Cash -> CashChargeSection(state, actions)
            state.settlement.isDeliveryApp -> DeliveryChargeSection(state, actions)
        }
    }
}

@Composable
private fun AmountToPayHeader(amountCents: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        Text(
            stringResource(R.string.checkout_amount_to_pay),
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.textMuted,
        )
        Text(
            formatMoney(amountCents),
            style = AppTheme.typography.numericDisplay,
            color = AppTheme.colors.textPrimary,
        )
    }
}

@Composable
private fun TipSection(state: CheckoutUiState, actions: PaymentActions) {
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Text(
            stringResource(R.string.checkout_add_a_tip),
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.textPrimary,
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            TipChip(
                stringResource(R.string.checkout_tip_none),
                formatMoney(0),
                state.tipOption == TipOption.None,
                Modifier.weight(1f),
            ) { actions.onTipOption(TipOption.None) }
            TipChip(
                stringResource(R.string.checkout_tip_percent, 5),
                formatMoney(state.tipBaseCents * 5 / 100),
                state.tipOption == TipOption.Percent5,
                Modifier.weight(1f),
            ) { actions.onTipOption(TipOption.Percent5) }
            TipChip(
                stringResource(R.string.checkout_tip_percent, 10),
                formatMoney(state.tipBaseCents * 10 / 100),
                state.tipOption == TipOption.Percent10,
                Modifier.weight(1f),
            ) { actions.onTipOption(TipOption.Percent10) }
            TipChip(
                stringResource(R.string.checkout_tip_percent, 20),
                formatMoney(state.tipBaseCents * 20 / 100),
                state.tipOption == TipOption.Percent20,
                Modifier.weight(1f),
            ) { actions.onTipOption(TipOption.Percent20) }
            TipChip(
                stringResource(R.string.checkout_tip_custom),
                stringResource(R.string.checkout_enter_amount_short),
                state.tipOption == TipOption.Custom,
                Modifier.weight(1f),
            ) { actions.onTipOption(TipOption.Custom) }
        }
        if (state.tipOption == TipOption.Custom) {
            CustomAmountField(
                label = stringResource(R.string.checkout_enter_custom_tip),
                digits = state.tipCustomDigits,
                onDigitsChange = actions.onTipAmountDigits,
            )
        }
    }
}

@Composable
private fun TipChip(title: String, subtitle: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Column(
        modifier
            .heightIn(min = AppTheme.sizes.minTouchTarget + AppTheme.spacing.md)
            .clip(shape)
            .border(
                AppTheme.border.thin,
                if (selected) AppTheme.colors.primary else AppTheme.colors.textPrimary.copy(alpha = 0.14f),
                shape,
            )
            .clickable(onClick = onClick)
            .padding(AppTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
    ) {
        Text(
            title,
            style = AppTheme.typography.labelMedium,
            color = if (selected) AppTheme.colors.primary else AppTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(subtitle, style = AppTheme.typography.bodySmall, color = AppTheme.colors.textMuted, maxLines = 1)
    }
}

@Composable
private fun PayAmountModeRow(selected: PayAmountMode, onSelect: (PayAmountMode) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EqualPillChip(
            stringResource(R.string.checkout_pay_full_amount),
            selected == PayAmountMode.Full,
            onClick = { onSelect(PayAmountMode.Full) },
            modifier = Modifier.weight(1f),
        )
        EqualPillChip(
            stringResource(R.string.checkout_split_custom),
            selected == PayAmountMode.Custom,
            onClick = { onSelect(PayAmountMode.Custom) },
            modifier = Modifier.weight(1f),
        )
        EqualPillChip(
            stringResource(R.string.checkout_split_by_items),
            selected == PayAmountMode.ByItems,
            onClick = { onSelect(PayAmountMode.ByItems) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PaymentTypeRow(selected: Settlement, onSelect: (Settlement) -> Unit) {
    SurfaceChoiceRow {
        Settlement.entries.forEach { option ->
            SurfaceChoiceChip(
                label = stringResource(option.labelRes),
                selected = option == selected,
                onClick = { onSelect(option) },
            )
        }
    }
}

@Composable
fun ProcessingOverlay() {
    Column(
        modifier = Modifier
            .widthIn(max = AppTheme.sizes.dialogMaxWidth)
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.lg))
            .background(AppTheme.colors.card)
            .padding(horizontal = AppTheme.spacing.xxxl, vertical = AppTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(AppTheme.sizes.logoMark),
            color = AppTheme.colors.primary,
            trackColor = AppTheme.colors.surface,
        )
        Text(
            stringResource(R.string.checkout_processing_title),
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.checkout_processing_body),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@ScreenPreviews
@Composable
private fun PaymentCardPreview() {
    PreviewSurface {
        PaymentContent(
            CheckoutUiState(step = CheckoutStep.Payment, lines = SampleCart, settlement = Settlement.Card),
            emptyPayment(),
        )
    }
}

private fun emptyPayment() = PaymentActions({}, {}, {}, {}, {}, {}, {}, {}, {})
