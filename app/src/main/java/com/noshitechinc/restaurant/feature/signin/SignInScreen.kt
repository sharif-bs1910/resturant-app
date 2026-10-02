package com.noshitechinc.restaurant.feature.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.ui.AppScaffold
import com.noshitechinc.restaurant.feature.auth.AuthFormColumn
import com.noshitechinc.restaurant.feature.auth.AuthHeading
import com.noshitechinc.restaurant.feature.auth.AuthHint
import com.noshitechinc.restaurant.feature.auth.AuthSplit
import com.noshitechinc.restaurant.feature.auth.PasscodeCells
import com.noshitechinc.restaurant.feature.auth.PasscodeKeypad
import com.noshitechinc.restaurant.feature.pairing.PasscodeError

@Composable
fun SignInRoute(onSignedIn: () -> Unit, viewModel: SignInViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AppScaffold(effects = viewModel.effects, onEffect = { effect -> if (effect is OpenSelectRole) onSignedIn() }) {
        SignInScreen(state = state, onDigit = viewModel::onDigit, onDelete = viewModel::onDelete)
    }
}

@Composable
fun SignInScreen(state: SignInUiState, onDigit: (Int) -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    AuthSplit(paired = true, body = stringResource(R.string.auth_sign_in_body), modifier = modifier) {
        AuthFormColumn {
            AuthHeading(
                title = stringResource(R.string.auth_sign_in_title),
                subtitle = stringResource(R.string.auth_sign_in_subtitle),
            )
            PasscodeCells(length = 4, value = state.pin, masked = true, hasError = state.hasError)
            if (state.hasError) PasscodeError(stringResource(R.string.auth_pin_error))
            PasscodeKeypad(onDigit = onDigit, onDelete = onDelete)
            AuthHint(
                label = stringResource(R.string.auth_pin_hint_label),
                body = stringResource(R.string.auth_pin_hint_body),
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun SignInPreview() {
    PreviewSurface {
        SignInScreen(state = SignInUiState(pin = "12"), onDigit = {}, onDelete = {})
    }
}

@ScreenPreviews
@Composable
private fun SignInErrorPreview() {
    PreviewSurface {
        SignInScreen(state = SignInUiState(pin = "1234", hasError = true), onDigit = {}, onDelete = {})
    }
}
