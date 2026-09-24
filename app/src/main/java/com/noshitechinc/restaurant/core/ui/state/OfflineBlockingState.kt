package com.noshitechinc.restaurant.core.ui.state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.component.dialog.AppModalLayout
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.state.StatusMessage
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun OfflineBlockingState(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.scrim)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            }
            .testTag(StateTags.OFFLINE),
        contentAlignment = Alignment.Center,
    ) {
        AppModalLayout(
            title = stringResource(R.string.offline_blocking_title),
            modifier = Modifier.padding(AppTheme.spacing.xl),
            actions = {
                AppButton(
                    text = stringResource(R.string.action_retry),
                    onClick = onRetry,
                )
            },
        ) {
            StatusMessage(
                icon = Icons.Outlined.WifiOff,
                title = "",
                message = stringResource(R.string.offline_blocking_message),
                scrollable = false,
            )
        }
    }
}

@ScreenPreviews
@Composable
private fun OfflineBlockingStatePreview() {
    PreviewSurface {
        OfflineBlockingState(onRetry = {})
    }
}
