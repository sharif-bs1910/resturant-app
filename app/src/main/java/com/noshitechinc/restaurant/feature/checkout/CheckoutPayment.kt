package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    val onCashTenderNote: (Int) -> Unit = {},
    val onCustomAmountDigits: (String) -> Unit = {},
    val onSplitAmountDigits: (String) -> Unit = {},
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
    val onSplitCustomKind: (SplitCustomKind) -> Unit = {},
    val onSplitGuestCount: (Int) -> Unit = {},
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
                stringResource(
                    if (state.payAmountMode == PayAmountMode.Full) {
                        R.string.checkout_payment_details
                    } else {
                        R.string.checkout_payment_title
                    },
                ),
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
                verticalAlignment = Alignment.Top,
            ) {
                OrderSummaryPane(state, actions, Modifier.width(AppTheme.sizes.dialogMaxWidth / 2))
                // Wrap height to content (Figma delivery 4050:16102) — fillMaxHeight left empty space under CTA
                PaymentPane(
                    state,
                    actions,
                    Modifier
                        .weight(1f)
                        .wrapContentHeight(align = Alignment.Top),
                )
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
    val deliverySummary = state.settlement.isDeliveryApp
    Column(
        modifier
            .wrapContentHeight()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(AppTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Text(
            stringResource(R.string.checkout_order_summary),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.textPrimary,
        )
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            MoneyRow(stringResource(R.string.checkout_subtotal), formatMoney(state.subtotalCents))
            MoneyRow(stringResource(R.string.checkout_tax), formatMoney(state.taxCents))
            if (!deliverySummary && state.tipCents > 0) {
                MoneyRow(stringResource(R.string.checkout_tip), formatMoney(state.tipCents))
            }
            MoneyRow(
                stringResource(R.string.checkout_total),
                formatMoney(state.totalCents + if (deliverySummary) 0 else state.tipCents),
                emphasize = true,
            )
        }
        // Figma Pay with Others (4050:16102): compact card — totals only, no remaining / redeem / link
        if (!deliverySummary) {
            if (state.giftAppliedCents > 0) {
                MoneyRow(stringResource(R.string.checkout_gift_card), formatMoney(-state.giftAppliedCents))
            }
            if (state.paidCents > 0) {
                MoneyRow(stringResource(R.string.checkout_paid_so_far), formatMoney(-state.paidCents))
            }
            HorizontalDivider(
                thickness = AppTheme.border.thin,
                color = AppTheme.colors.textPrimary.copy(alpha = 0.14f),
            )
            MoneyRow(
                stringResource(R.string.checkout_amount_remaining),
                formatMoney(state.dueCents),
                emphasize = true,
                accentValue = true,
            )
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
}

@Composable
private fun PaymentPane(state: CheckoutUiState, actions: PaymentActions, modifier: Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Column(
        modifier
            .wrapContentHeight()
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppTheme.spacing.xl, vertical = AppTheme.spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PayAmountModeRow(state.payAmountMode, actions.onPayAmountMode)
        // Figma split-by-items (4050:14640): no Amount to pay header — item list totals replace it
        if (state.payAmountMode != PayAmountMode.ByItems) {
            AmountToPayHeader(state)
        }
        when (state.payAmountMode) {
            PayAmountMode.Full -> Unit
            PayAmountMode.Custom -> SplitCustomSection(state, actions)
            PayAmountMode.ByItems -> SplitByItemsSection(state, actions)
        }
        PaymentPaneDivider()
        if (!state.settlement.isDeliveryApp) {
            TipSection(state, actions)
            PaymentPaneDivider()
        }
        Text(
            stringResource(R.string.checkout_choose_payment_type),
            // Figma JetBrains Mono 14 — keep mono family (labelSmall), size from labelMedium
            style = AppTheme.typography.labelSmall.copy(
                fontSize = AppTheme.typography.labelMedium.fontSize,
                lineHeight = AppTheme.typography.labelMedium.lineHeight,
            ),
            // Figma: Full (15975) + delivery (16082) → Text/Default; split modes (14471/14640) → Muted
            color = if (
                state.settlement.isDeliveryApp ||
                state.payAmountMode == PayAmountMode.Full
            ) {
                AppTheme.colors.textPrimary
            } else {
                AppTheme.colors.textMuted
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        PaymentTypeRow(
            settlement = state.settlement,
            options = if (state.payAmountMode == PayAmountMode.ByItems) {
                listOf(Settlement.Card, Settlement.Cash)
            } else {
                Settlement.entries.toList()
            },
            onSettlement = actions.onSettlement,
        )
        when {
            state.settlement == Settlement.Card -> CardChargeSection(state, actions)
            state.settlement == Settlement.Cash -> CashChargeSection(state, actions)
            state.settlement.isDeliveryApp -> DeliveryChargeSection(state, actions)
        }
    }
}

@Composable
private fun PaymentPaneDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = AppTheme.border.thin,
        color = AppTheme.colors.textPrimary.copy(alpha = 0.14f),
    )
}

@Composable
private fun AmountToPayHeader(state: CheckoutUiState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            stringResource(R.string.checkout_amount_to_pay),
            style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
            color = AppTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
        Text(
            formatMoney(state.amountToPayCents),
            style = AppTheme.typography.numericDisplay.copy(
                fontSize = AppTheme.typography.headlineMedium.fontSize,
                lineHeight = AppTheme.typography.headlineMedium.lineHeight,
            ),
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
        )
        if (state.settlement.isDeliveryApp) {
            Text(
                stringResource(
                    R.string.checkout_paid_on_delivery_app,
                    formatMoney(state.remainingBeforeTipCents),
                ),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SplitCustomSection(state: CheckoutUiState, actions: PaymentActions) {
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            SurfaceChoiceRow(
                scroll = false,
                fillMaxWidth = false,
                trackColor = AppTheme.colors.background,
                modifier = Modifier.width(AppTheme.sizes.checkoutDetailWidth),
            ) {
                SurfaceChoiceChip(
                    label = stringResource(R.string.checkout_split_evenly),
                    selected = state.splitCustomKind == SplitCustomKind.Evenly,
                    onClick = { actions.onSplitCustomKind(SplitCustomKind.Evenly) },
                    modifier = Modifier.weight(1f),
                    tall = true,
                )
                SurfaceChoiceChip(
                    label = stringResource(R.string.checkout_custom_amount),
                    selected = state.splitCustomKind == SplitCustomKind.CustomAmount,
                    onClick = { actions.onSplitCustomKind(SplitCustomKind.CustomAmount) },
                    modifier = Modifier.weight(1f),
                    tall = true,
                )
            }
        }
        when (state.splitCustomKind) {
            SplitCustomKind.Evenly -> SplitGuestsPanel(state, actions)

            SplitCustomKind.CustomAmount ->
                CustomAmountField(
                    label = stringResource(R.string.checkout_enter_custom_tip),
                    digits = state.splitAmountDigits,
                    onDigitsChange = actions.onSplitAmountDigits,
                )
        }
    }
}

@Composable
private fun SplitGuestsPanel(state: CheckoutUiState, actions: PaymentActions) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val hairline = AppTheme.colors.textPrimary.copy(alpha = 0.14f)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppTheme.colors.background)
            .padding(AppTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
            ) {
                Text(
                    stringResource(R.string.checkout_number_of_guests),
                    style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.textPrimary,
                )
                Text(
                    stringResource(
                        R.string.checkout_guests_split_hint,
                        formatMoney(state.remainingBeforeTipCents),
                    ),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Stepper(state.splitGuestCount) { actions.onSplitGuestCount(it) }
        }
        HorizontalDivider(thickness = AppTheme.border.thin, color = hairline)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (count in 2..8) {
                EqualPillChip(
                    label = count.toString(),
                    selected = state.splitGuestCount == count,
                    onClick = { actions.onSplitGuestCount(count) },
                    modifier = Modifier.weight(1f),
                    mediumHeight = true,
                )
            }
        }
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
            // Figma 4050:16231: Inter 14 Medium
            style = AppTheme.typography.labelMedium,
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
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EqualPillChip(
            stringResource(R.string.checkout_pay_full_amount),
            selected == PayAmountMode.Full,
            onClick = { onSelect(PayAmountMode.Full) },
        )
        EqualPillChip(
            stringResource(R.string.checkout_split_custom),
            selected == PayAmountMode.Custom,
            onClick = { onSelect(PayAmountMode.Custom) },
        )
        EqualPillChip(
            stringResource(R.string.checkout_split_by_items),
            selected == PayAmountMode.ByItems,
            onClick = { onSelect(PayAmountMode.ByItems) },
        )
    }
}

@Composable
private fun PaymentTypeRow(settlement: Settlement, options: List<Settlement>, onSettlement: (Settlement) -> Unit) {
    // Figma split-by-items (4050:14748): fixed 400px oat-milk track, equal flex Card/Cash
    val splitByItems = options.size == 2
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        SurfaceChoiceRow(
            scroll = !splitByItems,
            // Fixed 400dp width for Card/Cash — do not fillMaxWidth or it overrides width()
            fillMaxWidth = false,
            trackColor = if (splitByItems) {
                AppTheme.colors.background
            } else {
                AppTheme.colors.surface
            },
            modifier = if (splitByItems) {
                Modifier.width(AppTheme.sizes.checkoutDetailWidth)
            } else {
                Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)
            },
        ) {
            options.forEach { option ->
                SurfaceChoiceChip(
                    label = stringResource(option.labelRes),
                    selected = settlement == option,
                    onClick = { onSettlement(option) },
                    modifier = if (splitByItems) Modifier.weight(1f) else Modifier,
                    tall = splitByItems,
                )
            }
        }
    }
}

@Composable
fun ProcessingOverlay() {
    Column(
        modifier = Modifier
            .widthIn(max = AppTheme.sizes.formMaxWidth / 2)
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.lg))
            .background(AppTheme.colors.card)
            .padding(horizontal = AppTheme.spacing.xxxl, vertical = AppTheme.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Box(
            Modifier
                .size(AppTheme.sizes.logoMark)
                .clip(RoundedCornerShape(AppTheme.radius.pill))
                .background(AppTheme.colors.primary),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(AppTheme.sizes.iconMd),
                color = AppTheme.colors.card,
                trackColor = AppTheme.colors.primary.copy(alpha = 0.35f),
                strokeWidth = AppTheme.border.thick,
            )
        }
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

@ScreenPreviews
@Composable
private fun PaymentSplitByItemsPreview() {
    PreviewSurface {
        PaymentContent(
            CheckoutUiState(
                step = CheckoutStep.Payment,
                lines = SampleCart,
                payAmountMode = PayAmountMode.ByItems,
                selectedPayLineIds = SampleCart.take(2).map { it.id }.toSet(),
                settlement = Settlement.Card,
            ),
            emptyPayment(),
        )
    }
}

@ScreenPreviews
@Composable
private fun PaymentSplitCustomPreview() {
    PreviewSurface {
        PaymentContent(
            CheckoutUiState(
                step = CheckoutStep.Payment,
                lines = SampleCart,
                payAmountMode = PayAmountMode.Custom,
                splitCustomKind = SplitCustomKind.Evenly,
                splitGuestCount = 4,
                settlement = Settlement.Card,
            ),
            emptyPayment(),
        )
    }
}

@ScreenPreviews
@Composable
private fun PaymentSplitCustomAmountCashPreview() {
    PreviewSurface {
        PaymentContent(
            CheckoutUiState(
                step = CheckoutStep.Payment,
                lines = SampleCart,
                payAmountMode = PayAmountMode.Custom,
                splitCustomKind = SplitCustomKind.CustomAmount,
                splitAmountDigits = "2428",
                settlement = Settlement.Cash,
                cardChargeCustom = true,
                customChargeDigits = "2000",
                cashTenderedDigits = "5000",
            ),
            emptyPayment(),
        )
    }
}

@ScreenPreviews
@Composable
private fun PaymentSplitCustomAmountCardPreview() {
    PreviewSurface {
        PaymentContent(
            CheckoutUiState(
                step = CheckoutStep.Payment,
                lines = SampleCart,
                payAmountMode = PayAmountMode.Custom,
                splitCustomKind = SplitCustomKind.CustomAmount,
                splitAmountDigits = "2428",
                settlement = Settlement.Card,
                cardChargeCustom = true,
                customChargeDigits = "2500",
            ),
            emptyPayment(),
        )
    }
}

@ScreenPreviews
@Composable
private fun PaymentCashPreview() {
    PreviewSurface {
        PaymentContent(
            CheckoutUiState(
                step = CheckoutStep.Payment,
                lines = SampleCart,
                settlement = Settlement.Cash,
            ),
            emptyPayment(),
        )
    }
}

@ScreenPreviews
@Composable
private fun PaymentDeliveryPreview() {
    PreviewSurface {
        PaymentContent(
            CheckoutUiState(
                step = CheckoutStep.Payment,
                lines = SampleCart,
                settlement = Settlement.UberEats,
                deliveryApp = DeliveryApp.UberEats,
            ),
            emptyPayment(),
        )
    }
}

private fun emptyPayment() = PaymentActions({}, {}, {}, {}, {}, {}, {}, {}, {})
