package com.noshitechinc.restaurant.feature.selfservice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.feature.checkout.PillButton
import com.noshitechinc.restaurant.feature.checkout.formatMoney

@Composable
fun SelfServicePaymentContent(state: SelfServiceUiState, onCancel: () -> Unit, onTryAgain: () -> Unit, onBackToOrder: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(AppTheme.spacing.xl), contentAlignment = Alignment.Center) {
        when (state.step) {
            SelfServiceStep.PaymentWaiting -> WaitingCard(state, onCancel)
            SelfServiceStep.PaymentProcessing -> ProcessingCard(state)
            SelfServiceStep.PaymentDeclined -> DeclinedCard(state, onTryAgain, onBackToOrder)
            else -> Unit
        }
    }
}

@Composable
private fun WaitingCard(state: SelfServiceUiState, onCancel: () -> Unit) {
    PaymentCard {
        PaymentBadge { Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = AppTheme.colors.card) }
        Text(
            stringResource(R.string.self_pay_wait_title),
            style = AppTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.self_pay_due, formatMoney(state.dueCents)),
            style = AppTheme.typography.priceLarge,
            color = AppTheme.colors.primary,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.self_pay_meta, state.orderNumber, state.tableNumber),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
        PillButton(
            stringResource(R.string.self_cancel),
            onCancel,
            Modifier
                .fillMaxWidth(0.45f)
                .height(AppTheme.sizes.buttonMedium),
            filled = false,
        )
    }
}

@Composable
private fun ProcessingCard(state: SelfServiceUiState) {
    PaymentCard {
        CircularProgressIndicator(
            modifier = Modifier.size(AppTheme.sizes.logoMark),
            color = AppTheme.colors.primary,
            strokeWidth = AppTheme.border.thick,
        )
        Text(
            stringResource(R.string.self_pay_processing_title),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.self_pay_processing_body),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.self_pay_due, formatMoney(state.dueCents)),
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.primary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DeclinedCard(state: SelfServiceUiState, onTryAgain: () -> Unit, onBackToOrder: () -> Unit) {
    PaymentCard {
        Text(
            stringResource(R.string.self_pay_declined_title),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.self_pay_declined_body),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.self_pay_due, formatMoney(state.dueCents)),
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.primary,
            textAlign = TextAlign.Center,
        )
        PillButton(stringResource(R.string.self_try_again), onTryAgain, Modifier.fillMaxWidth())
        PillButton(
            stringResource(R.string.self_back_to_order),
            onBackToOrder,
            Modifier.fillMaxWidth(),
            filled = false,
        )
    }
}

@Composable
private fun PaymentCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .widthIn(max = AppTheme.sizes.dialogMaxWidth)
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.lg))
            .background(AppTheme.colors.card)
            .border(
                AppTheme.border.thin,
                AppTheme.colors.textPrimary.copy(alpha = 0.14f),
                RoundedCornerShape(AppTheme.radius.lg),
            )
            .padding(AppTheme.spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        content()
    }
}

@Composable
private fun PaymentBadge(content: @Composable () -> Unit) {
    Box(
        Modifier
            .size(AppTheme.sizes.logoMark)
            .clip(CircleShape)
            .background(AppTheme.colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@ScreenPreviews
@Composable
private fun SelfServicePaymentWaitingPreview() {
    PreviewSurface {
        SelfServicePaymentContent(
            state = SelfServiceUiState(
                step = SelfServiceStep.PaymentWaiting,
                lines = SelfServiceDemoCart,
                tableNumber = "12",
                orderNumber = "1047",
            ),
            onCancel = {},
            onTryAgain = {},
            onBackToOrder = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun SelfServicePaymentDeclinedPreview() {
    PreviewSurface {
        SelfServicePaymentContent(
            state = SelfServiceUiState(
                step = SelfServiceStep.PaymentDeclined,
                lines = SelfServiceDemoCart,
                tableNumber = "12",
            ),
            onCancel = {},
            onTryAgain = {},
            onBackToOrder = {},
        )
    }
}
