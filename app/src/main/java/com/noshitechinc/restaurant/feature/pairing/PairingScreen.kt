package com.noshitechinc.restaurant.feature.pairing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.ui.AppScaffold
import com.noshitechinc.restaurant.feature.auth.AuthFormColumn
import com.noshitechinc.restaurant.feature.auth.AuthHeading
import com.noshitechinc.restaurant.feature.auth.AuthHint
import com.noshitechinc.restaurant.feature.auth.AuthSplit
import com.noshitechinc.restaurant.feature.auth.PasscodeCells
import com.noshitechinc.restaurant.feature.auth.PasscodeKeypad

@Composable
fun PairingRoute(onPaired: () -> Unit, viewModel: PairingViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AppScaffold(effects = viewModel.effects, onEffect = { effect -> if (effect is OpenSignIn) onPaired() }) {
        PairingScreen(state = state, onDigit = viewModel::onDigit, onDelete = viewModel::onDelete)
    }
}

@Composable
fun PairingScreen(state: PairingUiState, onDigit: (Int) -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    AuthSplit(paired = false, body = stringResource(R.string.auth_pairing_body), modifier = modifier) {
        AuthFormColumn {
            AuthHeading(
                title = stringResource(R.string.auth_pair_title),
                subtitle = stringResource(R.string.auth_pair_subtitle),
            )
            PasscodeCells(length = 6, value = state.code, masked = false, hasError = state.hasError)
            if (state.hasError) PasscodeError(stringResource(R.string.auth_pair_error))
            PasscodeKeypad(onDigit = onDigit, onDelete = onDelete)
            AuthHint(
                label = stringResource(R.string.auth_pair_hint_label),
                body = stringResource(R.string.auth_pair_hint_body),
            )
        }
    }
}

@Composable
internal fun PasscodeError(message: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = AppTheme.colors.destructive,
            modifier = Modifier.size(AppTheme.sizes.iconSm),
        )
        Text(
            text = message,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.destructive,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@ScreenPreviews
@Composable
private fun PairingPreview() {
    PreviewSurface {
        PairingScreen(state = PairingUiState(code = "482"), onDigit = {}, onDelete = {})
    }
}

@ScreenPreviews
@Composable
private fun PairingErrorPreview() {
    PreviewSurface {
        PairingScreen(state = PairingUiState(code = "482910", hasError = true), onDigit = {}, onDelete = {})
    }
}
