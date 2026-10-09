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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun TenderSheet(state: CheckoutUiState, actions: OverlayActions) {
    PaymentLinkSheet(state, actions)
}

@Composable
internal fun PaymentLinkSheet(state: CheckoutUiState, actions: OverlayActions) {
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)) {
        SheetHeader(
            stringResource(R.string.checkout_payment_link),
            stringResource(R.string.checkout_payment_link_subtitle),
            actions.onDismiss,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs)) {
            Text(
                stringResource(R.string.checkout_amount_due),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
            )
            Text(
                formatMoney(state.dueCents),
                style = AppTheme.typography.numericDisplay,
                color = AppTheme.colors.primary,
            )
            PaidProgressBar(state.paidProgress)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.radius.sm))
                .border(AppTheme.border.thin, AppTheme.colors.textPrimary, RoundedCornerShape(AppTheme.radius.sm))
                .clickable(onClick = actions.onEditPaymentLink)
                .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    state.customerName.ifBlank { stringResource(R.string.checkout_customer_name_hint) },
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    state.customerPhone.ifBlank { stringResource(R.string.checkout_customer_phone_hint) },
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted,
                    maxLines = 1,
                    softWrap = false,
                )
            }
            Text(
                stringResource(R.string.checkout_edit),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.textMuted,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(stringResource(R.string.checkout_close_action), actions.onDismiss, Modifier.weight(1f), filled = false)
            PillButton(
                stringResource(R.string.checkout_send_link_to, phoneTail(state.customerPhone)),
                actions.onSendPaymentLink,
                Modifier.weight(1f),
                enabled = state.customerPhone.isNotBlank(),
            )
        }
    }
}

@Composable
internal fun PaymentLinkEditSheet(state: CheckoutUiState, actions: OverlayActions) {
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)) {
        SheetHeader(
            stringResource(R.string.checkout_edit_link_title),
            stringResource(R.string.checkout_edit_link_subtitle),
            actions.onBackToTender,
        )
        AddressInput(
            stringResource(R.string.checkout_customer_phone),
            state.customerDraftPhone,
            focused = true,
            onValue = actions.onCustomerPhone,
            placeholder = stringResource(R.string.checkout_customer_phone_hint),
        )
        AddressInput(
            stringResource(R.string.checkout_customer_name),
            state.customerDraftName,
            focused = false,
            onValue = actions.onCustomerName,
            placeholder = stringResource(R.string.checkout_customer_name_hint),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(stringResource(R.string.checkout_close_action), actions.onBackToTender, Modifier.weight(1f), filled = false)
            PillButton(
                stringResource(R.string.checkout_customer_save),
                actions.onSavePaymentLinkDetails,
                Modifier.weight(1f),
                enabled = state.customerDraftPhone.isNotBlank() || state.customerDraftName.isNotBlank(),
            )
        }
    }
}

@Composable
internal fun SavedSheet(state: CheckoutUiState, onDone: () -> Unit) {
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                Modifier.size(AppTheme.sizes.logoMark).clip(CircleShape).background(AppTheme.colors.success),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = AppTheme.colors.card)
            }
        }
        Text(
            stringResource(R.string.checkout_payment_sent_title),
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.radius.sm))
                .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.sm))
                .padding(AppTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            MoneyRow(stringResource(R.string.checkout_recap_channel), stringResource(state.channel.labelRes))
            MoneyRow(
                stringResource(R.string.checkout_recap_fulfillment),
                stringResource(
                    if (state.fulfillment == Fulfillment.Pickup) R.string.checkout_ready_pickup else R.string.checkout_ready_delivery,
                ),
            )
            MoneyRow(stringResource(R.string.checkout_recap_items), state.itemCount.toString())
            MoneyRow(stringResource(R.string.checkout_total), formatMoney(state.totalCents))
            MoneyRow(stringResource(R.string.checkout_recap_payment), stringResource(R.string.checkout_link_sent))
        }
        PillButton(stringResource(R.string.checkout_close_action), onDone, Modifier.fillMaxWidth())
    }
}
