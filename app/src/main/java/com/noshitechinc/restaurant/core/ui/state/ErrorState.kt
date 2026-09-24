package com.noshitechinc.restaurant.core.ui.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.state.StatusMessage
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.ui.asString
import com.noshitechinc.restaurant.core.ui.error.titleRes
import com.noshitechinc.restaurant.core.ui.error.toUiText

@Composable
fun ErrorState(error: AppError, onRetry: () -> Unit, modifier: Modifier = Modifier, retrying: Boolean = false) {
    val icon = when (error) {
        AppError.NoInternet -> Icons.Outlined.WifiOff
        AppError.ServiceUnavailable -> Icons.Outlined.CloudOff
        else -> Icons.Outlined.ErrorOutline
    }
    StatusMessage(
        icon = icon,
        title = stringResource(error.titleRes()),
        modifier = modifier.testTag(StateTags.ERROR),
        message = error.toUiText().asString(),
        iconTint = AppTheme.colors.destructive,
        action = {
            AppButton(
                text = stringResource(R.string.action_retry),
                onClick = onRetry,
                loading = retrying,
            )
        },
    )
}

@Composable
fun ServiceUnavailableState(onRetry: () -> Unit, modifier: Modifier = Modifier, retrying: Boolean = false) {
    StatusMessage(
        icon = Icons.Outlined.CloudOff,
        title = stringResource(R.string.error_title_unavailable),
        modifier = modifier.testTag(StateTags.ERROR),
        message = stringResource(R.string.service_unavailable_message),
        iconTint = AppTheme.colors.destructive,
        action = {
            AppButton(
                text = stringResource(R.string.action_retry),
                onClick = onRetry,
                loading = retrying,
            )
        },
    )
}

@ComponentPreviews
@Composable
private fun ErrorStateNoInternetPreview() {
    PreviewSurface {
        ErrorState(error = AppError.NoInternet, onRetry = {})
    }
}

@ComponentPreviews
@Composable
private fun ErrorStateTimeoutPreview() {
    PreviewSurface {
        ErrorState(error = AppError.Timeout, onRetry = {})
    }
}

@ComponentPreviews
@Composable
private fun ErrorStateServiceUnavailablePreview() {
    PreviewSurface {
        ErrorState(error = AppError.ServiceUnavailable, onRetry = {})
    }
}

@ComponentPreviews
@Composable
private fun ErrorStateSessionExpiredPreview() {
    PreviewSurface {
        ErrorState(error = AppError.SessionExpired, onRetry = {})
    }
}

@ComponentPreviews
@Composable
private fun ErrorStateForbiddenPreview() {
    PreviewSurface {
        ErrorState(error = AppError.Forbidden, onRetry = {})
    }
}

@ComponentPreviews
@Composable
private fun ErrorStateNotFoundPreview() {
    PreviewSurface {
        ErrorState(error = AppError.NotFound, onRetry = {})
    }
}

@ComponentPreviews
@Composable
private fun ErrorStateValidationPreview() {
    PreviewSurface {
        ErrorState(error = AppError.Validation(message = "Table number is required"), onRetry = {})
    }
}

@ComponentPreviews
@Composable
private fun ErrorStateServerPreview() {
    PreviewSurface {
        ErrorState(error = AppError.Server(code = 500, message = "Internal server error"), onRetry = {})
    }
}

@ComponentPreviews
@Composable
private fun ErrorStateUnknownPreview() {
    PreviewSurface {
        ErrorState(error = AppError.Unknown(), onRetry = {})
    }
}

@ComponentPreviews
@Composable
private fun ErrorStateRetryingPreview() {
    PreviewSurface {
        ErrorState(error = AppError.Timeout, onRetry = {}, retrying = true)
    }
}

@ComponentPreviews
@Composable
private fun ServiceUnavailableStatePreview() {
    PreviewSurface {
        ServiceUnavailableState(onRetry = {})
    }
}
