package com.noshitechinc.restaurant.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.component.dialog.AppModal
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.ui.error.titleRes
import com.noshitechinc.restaurant.core.ui.error.toUiText
import com.noshitechinc.restaurant.core.ui.state.OfflineBlockingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

val LocalIsOnline: ProvidableCompositionLocal<Boolean> = compositionLocalOf { true }

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    effects: Flow<UiEffect> = emptyFlow(),
    onEffect: (UiEffect) -> Unit = {},
    requiresNetwork: Boolean = false,
    onRetryConnection: () -> Unit = {},
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var messageTone by remember { mutableStateOf(MessageTone.Info) }
    var dialogError by remember { mutableStateOf<AppError?>(null) }

    ObserveEffects(effects) { effect ->
        when (effect) {
            is ShowMessage -> {
                messageTone = effect.tone
                scope.launch { snackbarHostState.showSnackbar(effect.text.asString(context)) }
            }

            is ShowErrorDialog -> dialogError = effect.error

            else -> onEffect(effect)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = topBar,
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    AppSnackbar(message = data.visuals.message, tone = messageTone)
                }
            },
            containerColor = AppTheme.colors.background,
            content = content,
        )
        if (requiresNetwork && !LocalIsOnline.current) {
            OfflineBlockingState(onRetry = onRetryConnection)
        }
    }

    dialogError?.let { error ->
        AppModal(
            title = stringResource(error.titleRes()),
            onDismissRequest = { dialogError = null },
            actions = { AppButton(text = stringResource(R.string.action_ok), onClick = { dialogError = null }) },
        ) {
            Text(
                text = error.toUiText().asString(),
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}

@Composable
private fun AppSnackbar(message: String, tone: MessageTone) {
    val (container, content) = when (tone) {
        MessageTone.Info -> AppTheme.colors.textPrimary to AppTheme.colors.surface
        MessageTone.Success -> AppTheme.colors.success to AppTheme.colors.onPrimary
        MessageTone.Error -> AppTheme.colors.destructive to AppTheme.colors.onDestructive
    }
    Snackbar(
        modifier = Modifier.padding(AppTheme.spacing.lg),
        shape = RoundedCornerShape(AppTheme.radius.md),
        containerColor = container,
        contentColor = content,
    ) {
        Text(text = message, style = AppTheme.typography.bodyMedium, maxLines = 4)
    }
}

@ScreenPreviews
@Composable
private fun AppScaffoldOnlinePreview() {
    PreviewSurface {
        AppScaffold { Text(text = "Screen content", modifier = Modifier.padding(it)) }
    }
}

@ScreenPreviews
@Composable
private fun AppScaffoldOfflinePreview() {
    PreviewSurface {
        CompositionLocalProvider(LocalIsOnline provides false) {
            AppScaffold(requiresNetwork = true) {
                Text(text = "Screen content", modifier = Modifier.padding(it))
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun AppSnackbarPreview() {
    PreviewSurface {
        Box {
            AppSnackbar(message = "Order sent to the kitchen", tone = MessageTone.Success)
        }
    }
}
