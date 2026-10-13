package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun ConfirmationContent(state: CheckoutUiState, onNewOrder: () -> Unit, onPrintTicket: () -> Unit = {}, onPrintReceipt: () -> Unit = {}) {
    Box(Modifier.fillMaxSize().padding(AppTheme.spacing.xl), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .widthIn(max = AppTheme.sizes.dialogMaxWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .clip(RoundedCornerShape(AppTheme.radius.lg))
                .background(AppTheme.colors.card)
                .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.lg))
                .padding(horizontal = AppTheme.spacing.xxxl, vertical = AppTheme.spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        ) {
            Box(
                Modifier
                    .size(AppTheme.sizes.logoMark)
                    .clip(CircleShape)
                    .background(AppTheme.colors.success),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = AppTheme.colors.card)
            }
            Text(
                stringResource(R.string.checkout_placed_heading),
                style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.checkout_placed_body),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
                textAlign = TextAlign.Center,
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                TokenBlock(
                    label = stringResource(R.string.checkout_token_number),
                    value = stringResource(R.string.checkout_token_value, state.orderNumber),
                    accent = true,
                    modifier = Modifier.weight(1f),
                )
                TokenBlock(
                    label = stringResource(R.string.checkout_table_number),
                    value = stringResource(R.string.checkout_token_value, state.tableNumber),
                    accent = false,
                    modifier = Modifier.weight(1f),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppTheme.radius.sm))
                    .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.sm))
                    .padding(AppTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                RecapRow(stringResource(R.string.checkout_recap_name), state.customerName)
                RecapRow(stringResource(R.string.checkout_recap_phone), state.customerPhone)
                RecapRow(stringResource(R.string.checkout_recap_items), state.itemCount.toString())
                RecapRow(stringResource(R.string.checkout_total), formatMoney(state.totalCents))
                RecapRow(stringResource(R.string.checkout_paid_with), paidWithLabel(state))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                PillButton(stringResource(R.string.checkout_print_ticket), onPrintTicket, Modifier.weight(1f), filled = false)
                PillButton(stringResource(R.string.checkout_print_receipt), onPrintReceipt, Modifier.weight(1f), filled = false)
            }
            PillButton(stringResource(R.string.checkout_new_order), onNewOrder, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun paidWithLabel(state: CheckoutUiState): String = when (state.settlement) {
    Settlement.Cash -> stringResource(R.string.checkout_paid_cash)

    Settlement.Card -> stringResource(R.string.checkout_paid_card_ending, state.cardLastFour)

    Settlement.UberEats,
    Settlement.DoorDash,
    Settlement.Grubhub,
    -> stringResource(state.settlement.labelRes)
}

@Composable
private fun TokenBlock(label: String, value: String, accent: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(AppTheme.radius.sm))
            .background(if (accent) AppTheme.colors.surface else AppTheme.colors.surfaceVariant)
            .padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Text(
            label,
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            value,
            style = AppTheme.typography.headlineMedium,
            color = if (accent) AppTheme.colors.primary else AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
internal fun RecapRow(label: String, value: String) {
    MoneyRow(label, value)
}

@ScreenPreviews
@Composable
private fun ConfirmationContentPreview() {
    PreviewSurface {
        ConfirmationContent(
            CheckoutUiState(step = CheckoutStep.Confirmed, orderNumber = "1042", lines = PhoneCart),
            onNewOrder = {},
        )
    }
}
