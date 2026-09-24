package com.noshitechinc.restaurant.feature.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.AppEnvironment
import com.noshitechinc.restaurant.core.designsystem.component.selection.StatusBadge
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.designsystem.theme.StatusTone
import com.noshitechinc.restaurant.core.ui.AppScaffold

@Composable
fun HomeRoute(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AppScaffold(effects = viewModel.effects) { padding ->
        HomeScreen(state = state, modifier = Modifier.padding(padding))
    }
}

@Composable
fun HomeScreen(state: HomeUiState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(AppTheme.spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = AppTheme.sizes.formMaxWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = AppTheme.typography.headlineLarge,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.home_subtitle),
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            state.environment?.let {
                StatusBadge(text = stringResource(it.labelRes()), tone = StatusTone.Warning)
            }
            Text(
                text = stringResource(R.string.home_version, state.versionName),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}

@StringRes
private fun AppEnvironment.labelRes(): Int = when (this) {
    AppEnvironment.Dev -> R.string.env_dev
    AppEnvironment.Staging -> R.string.env_staging
    AppEnvironment.Prod -> R.string.env_prod
}

@ScreenPreviews
@Composable
private fun HomeScreenDevPreview() {
    PreviewSurface {
        HomeScreen(state = HomeUiState(environment = AppEnvironment.Dev, versionName = "0.1.0"))
    }
}

@ScreenPreviews
@Composable
private fun HomeScreenProdPreview() {
    PreviewSurface {
        HomeScreen(state = HomeUiState(environment = null, versionName = "0.1.0"))
    }
}
