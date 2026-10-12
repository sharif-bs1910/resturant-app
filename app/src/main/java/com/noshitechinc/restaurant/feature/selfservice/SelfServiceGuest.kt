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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.format.UsPhoneFormatter
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.feature.checkout.AddressInput
import com.noshitechinc.restaurant.feature.checkout.PillButton

@Composable
fun SelfServiceGuestContent(
    state: SelfServiceUiState,
    onPhone: (String) -> Unit,
    onName: (String) -> Unit,
    onTable: (String) -> Unit,
    onContinue: () -> Unit,
) {
    // Figma card width 480 → 6 × passcode key width.
    val cardWidth = AppTheme.sizes.passcodeKeyWidth * 6
    Box(
        Modifier.fillMaxSize().padding(AppTheme.spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = cardWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .clip(RoundedCornerShape(AppTheme.radius.md))
                .background(AppTheme.colors.card)
                .border(
                    AppTheme.border.thin,
                    AppTheme.colors.textPrimary.copy(alpha = 0.14f),
                    RoundedCornerShape(AppTheme.radius.md),
                )
                .padding(AppTheme.spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxl),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            ) {
                Text(
                    stringResource(R.string.self_guest_title),
                    style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    stringResource(R.string.self_guest_body),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textMuted,
                    textAlign = TextAlign.Center,
                )
            }
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                AddressInput(
                    label = stringResource(R.string.checkout_customer_phone),
                    value = UsPhoneFormatter.format(state.guestPhoneDigits),
                    focused = false,
                    onValue = { onPhone(UsPhoneFormatter.digitsOnly(it)) },
                    placeholder = stringResource(R.string.checkout_customer_phone_hint),
                )
                AddressInput(
                    label = stringResource(R.string.checkout_customer_name),
                    value = state.guestName,
                    focused = false,
                    onValue = onName,
                    placeholder = stringResource(R.string.checkout_customer_name_hint),
                )
                AddressInput(
                    label = stringResource(R.string.checkout_table_number),
                    value = state.tableNumber,
                    focused = false,
                    onValue = onTable,
                    placeholder = stringResource(R.string.self_table_hint),
                )
            }
            PillButton(
                stringResource(R.string.checkout_customer_continue),
                onContinue,
                Modifier
                    .fillMaxWidth()
                    .height(AppTheme.sizes.buttonMedium),
                enabled = state.guestReady,
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun SelfServiceGuestEmptyPreview() {
    PreviewSurface {
        SelfServiceGuestContent(
            state = SelfServiceUiState(step = SelfServiceStep.Guest),
            onPhone = {},
            onName = {},
            onTable = {},
            onContinue = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun SelfServiceGuestFilledPreview() {
    PreviewSurface {
        SelfServiceGuestContent(
            state = SelfServiceUiState(
                step = SelfServiceStep.Guest,
                guestPhoneDigits = "4155550192",
                guestName = "Maya Rodriguez",
                tableNumber = "12",
            ),
            onPhone = {},
            onName = {},
            onTable = {},
            onContinue = {},
        )
    }
}
