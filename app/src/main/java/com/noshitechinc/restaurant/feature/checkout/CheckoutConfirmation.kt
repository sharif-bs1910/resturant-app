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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun ConfirmationContent(state: CheckoutUiState, onNewOrder: () -> Unit, onPrintTicket: () -> Unit = {}, onPrintReceipt: () -> Unit = {}) {
    Box(Modifier.fillMaxSize().padding(AppTheme.spacing.xl), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .width(AppTheme.sizes.dialogMaxWidth)
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
                stringResource(R.string.checkout_confirmed_heading, state.orderNumber),
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.checkout_confirmed_body),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
                textAlign = TextAlign.Center,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppTheme.radius.sm))
                    .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.sm))
                    .padding(AppTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                RecapRow(stringResource(R.string.checkout_recap_channel), stringResource(state.channel.labelRes))
                RecapRow(
                    stringResource(R.string.checkout_recap_fulfillment),
                    stringResource(
                        if (state.fulfillment == Fulfillment.Pickup) {
                            R.string.checkout_ready_pickup
                        } else {
                            R.string.checkout_ready_delivery
                        },
                    ),
                )
                RecapRow(stringResource(R.string.checkout_recap_items), state.itemCount.toString())
                RecapRow(stringResource(R.string.checkout_total), formatMoney(state.totalCents))
                RecapRow(stringResource(R.string.checkout_recap_payment), stringResource(R.string.checkout_payment_sent))
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
