package com.noshitechinc.restaurant.feature.selfservice

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
import com.noshitechinc.restaurant.core.common.format.UsPhoneFormatter
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.feature.checkout.PillButton
import com.noshitechinc.restaurant.feature.checkout.RecapRow
import com.noshitechinc.restaurant.feature.checkout.formatMoney

@Composable
fun SelfServiceSuccessContent(state: SelfServiceUiState, onNewOrder: () -> Unit) {
    Box(
        Modifier.fillMaxSize().padding(AppTheme.spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = AppTheme.sizes.dialogMaxWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .clip(RoundedCornerShape(AppTheme.radius.lg))
                .background(AppTheme.colors.card)
                .border(
                    AppTheme.border.thin,
                    AppTheme.colors.textPrimary.copy(alpha = 0.14f),
                    RoundedCornerShape(AppTheme.radius.lg),
                )
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
                stringResource(R.string.self_success_title),
                style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.self_success_body),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
                textAlign = TextAlign.Center,
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                TokenBlock(
                    label = stringResource(R.string.self_token_label),
                    value = stringResource(R.string.self_hash_value, state.tokenNumber),
                    accent = true,
                    modifier = Modifier.weight(1f),
                )
                TokenBlock(
                    label = stringResource(R.string.self_table_label),
                    value = stringResource(R.string.self_hash_value, state.tableNumber),
                    accent = false,
                    modifier = Modifier.weight(1f),
                )
            }
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
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                RecapRow(stringResource(R.string.checkout_customer_name), state.guestName)
                RecapRow(
                    stringResource(R.string.checkout_customer_phone),
                    UsPhoneFormatter.format(state.guestPhoneDigits),
                )
                RecapRow(stringResource(R.string.checkout_recap_items), state.itemCount.toString())
                RecapRow(stringResource(R.string.checkout_total), formatMoney(state.totalCents))
                RecapRow(
                    stringResource(R.string.self_paid_with),
                    stringResource(R.string.self_paid_card, state.cardLastFour),
                )
            }
            PillButton(stringResource(R.string.checkout_new_order), onNewOrder, Modifier.fillMaxWidth())
        }
    }
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

@ScreenPreviews
@Composable
private fun SelfServiceSuccessPreview() {
    PreviewSurface {
        SelfServiceSuccessContent(
            state = SelfServiceUiState(
                step = SelfServiceStep.Success,
                lines = SelfServiceDemoCart,
                guestName = "Maya Rodriguez",
                guestPhoneDigits = "4155550192",
                tableNumber = "12",
                tokenNumber = "1042",
            ),
            onNewOrder = {},
        )
    }
}
