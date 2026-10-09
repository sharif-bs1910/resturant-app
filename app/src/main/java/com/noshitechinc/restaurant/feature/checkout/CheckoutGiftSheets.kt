package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.input.CurrencyField
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun GiftCardSheet(state: CheckoutUiState, actions: OverlayActions) {
    val preview = minOf(GiftCardBalanceCents, state.dueCents.coerceAtLeast(state.totalCents))
    val remaining = (state.totalCents - preview).coerceAtLeast(0)
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.checkoutSheetWidth)) {
        SheetHeader(
            stringResource(R.string.checkout_redeem),
            stringResource(R.string.checkout_gift_context, state.orderNumber, formatMoney(state.dueCents)),
            actions.onDismiss,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl), modifier = Modifier.fillMaxWidth()) {
            val keyGap = AppTheme.spacing.md
            Column(
                Modifier.width(AppTheme.sizes.passcodeKeyWidth * 3 + keyGap * 2),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GiftField(stringResource(R.string.checkout_gift_number), state.giftNumber, state.giftField == GiftField.Number) {
                    actions.onGiftField(GiftField.Number)
                }
                GiftField(stringResource(R.string.checkout_gift_pin), state.giftPin, state.giftField == GiftField.Pin) {
                    actions.onGiftField(GiftField.Pin)
                }
                GiftKeypad(actions.onGiftDigit, actions.onGiftDelete)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AppTheme.radius.sm))
                        .border(
                            AppTheme.border.thin,
                            AppTheme.colors.textPrimary.copy(alpha = 0.14f),
                            RoundedCornerShape(AppTheme.radius.sm),
                        )
                        .padding(AppTheme.spacing.lg),
                ) {
                    Text(
                        stringResource(R.string.checkout_gift_balance),
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted,
                    )
                    Text(formatMoney(GiftCardBalanceCents), style = AppTheme.typography.headlineSmall, color = AppTheme.colors.textPrimary)
                    Text(
                        stringResource(R.string.checkout_available),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textMuted,
                    )
                }
                MoneyRow(stringResource(R.string.checkout_total), formatMoney(state.totalCents))
                MoneyRow(stringResource(R.string.checkout_gift_card), formatMoney(-preview))
                MoneyRow(stringResource(R.string.checkout_remaining), formatMoney(remaining), accentValue = true)
                Text(
                    stringResource(R.string.checkout_gift_helper),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(
                stringResource(R.string.checkout_back),
                actions.onBackToTender,
                Modifier.width(AppTheme.sizes.heroHeight / 2),
                filled = false,
            )
            PillButton(stringResource(R.string.checkout_customer_continue), actions.onContinueGiftCard, Modifier.weight(1f))
        }
    }
}

@Composable
internal fun GiftAmountSheet(state: CheckoutUiState, actions: OverlayActions) {
    val maxApply = minOf(GiftCardBalanceCents, state.remainingBeforeTipCents.coerceAtLeast(1))
    val applyCents = state.giftApplyCents.coerceIn(0, maxApply)
    val remaining = (state.totalCents - state.paidCents - applyCents).coerceAtLeast(0)
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.checkoutSheetWidth)) {
        SheetHeader(
            stringResource(R.string.checkout_enter_gift_amount),
            stringResource(R.string.checkout_gift_context, state.orderNumber, formatMoney(state.dueCents)),
            actions.onDismiss,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg), modifier = Modifier.fillMaxWidth()) {
            CurrencyField(
                cents = applyCents.toLong(),
                onCentsChange = { cents ->
                    actions.onGiftAmountDigits(
                        if (cents <= 0L) "" else cents.coerceAtMost(maxApply.toLong()).toString(),
                    )
                },
                label = stringResource(R.string.checkout_enter_custom_tip),
                modifier = Modifier.fillMaxWidth(),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppTheme.radius.sm))
                    .border(
                        AppTheme.border.thin,
                        AppTheme.colors.textPrimary.copy(alpha = 0.14f),
                        RoundedCornerShape(AppTheme.radius.sm),
                    )
                    .padding(AppTheme.spacing.lg),
            ) {
                Text(
                    stringResource(R.string.checkout_gift_balance),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textMuted,
                )
                Text(formatMoney(GiftCardBalanceCents), style = AppTheme.typography.headlineSmall, color = AppTheme.colors.textPrimary)
                Text(
                    stringResource(R.string.checkout_available),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textMuted,
                )
            }
            MoneyRow(stringResource(R.string.checkout_total), formatMoney(state.totalCents))
            MoneyRow(stringResource(R.string.checkout_gift_card), formatMoney(-applyCents))
            MoneyRow(stringResource(R.string.checkout_remaining), formatMoney(remaining), accentValue = true)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(
                stringResource(R.string.checkout_back),
                actions.onBackToTender,
                Modifier.width(AppTheme.sizes.heroHeight / 2),
                filled = false,
            )
            PillButton(
                stringResource(R.string.checkout_apply_amount, formatMoney(applyCents)),
                actions.onApplyGiftCard,
                Modifier.weight(1f),
                enabled = applyCents > 0,
            )
        }
    }
}
