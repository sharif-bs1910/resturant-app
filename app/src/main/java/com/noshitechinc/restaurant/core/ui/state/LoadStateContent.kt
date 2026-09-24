package com.noshitechinc.restaurant.core.ui.state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.state.EmptyState
import com.noshitechinc.restaurant.core.designsystem.state.LoadingState
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.ui.LoadState

object StateTags {
    const val ERROR = "state_error"
    const val OFFLINE = "state_offline_blocking"
    const val LOADING = "state_loading"
}

@Composable
fun <T> LoadStateContent(
    state: LoadState<T>,
    onRetry: () -> Unit,
    empty: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    loading: @Composable () -> Unit = { LoadingState(modifier = Modifier.testTag(StateTags.LOADING)) },
    content: @Composable (T) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (state) {
            LoadState.Idle -> Unit

            LoadState.Loading -> loading()

            LoadState.Empty -> empty()

            is LoadState.Error -> when (state.error) {
                AppError.ServiceUnavailable -> ServiceUnavailableState(onRetry = onRetry)
                else -> ErrorState(error = state.error, onRetry = onRetry)
            }

            is LoadState.Content -> content(state.data)
        }
    }
}

@ComponentPreviews
@Composable
private fun LoadStateContentLoadingPreview() {
    PreviewSurface {
        LoadStateContent(
            state = LoadState.Loading,
            onRetry = {},
            empty = { EmptyState() },
        ) { _: String -> }
    }
}

@ComponentPreviews
@Composable
private fun LoadStateContentEmptyPreview() {
    PreviewSurface {
        LoadStateContent(
            state = LoadState.Empty,
            onRetry = {},
            empty = { EmptyState() },
        ) { _: String -> }
    }
}

@ComponentPreviews
@Composable
private fun LoadStateContentErrorPreview() {
    PreviewSurface {
        LoadStateContent(
            state = LoadState.Error(AppError.Timeout),
            onRetry = {},
            empty = { EmptyState() },
        ) { _: String -> }
    }
}

@ComponentPreviews
@Composable
private fun LoadStateContentContentPreview() {
    PreviewSurface {
        LoadStateContent(
            state = LoadState.Content("Loaded item"),
            onRetry = {},
            empty = { EmptyState() },
        ) { data ->
            Text(text = data, style = AppTheme.typography.bodyLarge, color = AppTheme.colors.textPrimary)
        }
    }
}
