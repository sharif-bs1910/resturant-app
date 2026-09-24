package com.noshitechinc.restaurant.core.designsystem.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun LoadingState(modifier: Modifier = Modifier, message: String? = stringResource(R.string.state_loading)) {
    Column(
        modifier = modifier
            .widthIn(max = AppTheme.sizes.formMaxWidth)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        AppCircularProgress()
        if (message != null) {
            Text(
                text = message,
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}

@Composable
fun AppCircularProgress(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(AppTheme.sizes.iconXl),
        color = AppTheme.colors.primary,
        trackColor = AppTheme.colors.surfaceVariant,
        strokeWidth = AppTheme.border.thick,
    )
}

@Composable
fun AppLinearProgress(modifier: Modifier = Modifier, progress: Float? = null) {
    if (progress == null) {
        LinearProgressIndicator(
            modifier = modifier.fillMaxWidth(),
            color = AppTheme.colors.primary,
            trackColor = AppTheme.colors.surfaceVariant,
        )
    } else {
        LinearProgressIndicator(
            progress = { progress },
            modifier = modifier.fillMaxWidth(),
            color = AppTheme.colors.primary,
            trackColor = AppTheme.colors.surfaceVariant,
        )
    }
}

@ComponentPreviews
@Composable
private fun LoadingStatePreview() {
    PreviewSurface {
        LoadingState()
    }
}

@ComponentPreviews
@Composable
private fun AppCircularProgressPreview() {
    PreviewSurface {
        AppCircularProgress()
    }
}

@ComponentPreviews
@Composable
private fun AppLinearProgressIndeterminatePreview() {
    PreviewSurface {
        AppLinearProgress()
    }
}

@ComponentPreviews
@Composable
private fun AppLinearProgressDeterminatePreview() {
    PreviewSurface {
        AppLinearProgress(progress = 0.4f)
    }
}
