package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun TimeLogContent(state: CheckoutUiState, onSettings: (SettingsState) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Text(
            stringResource(R.string.checkout_nav_time_log),
            style = AppTheme.typography.headlineMedium.copy(
                fontSize = AppTheme.typography.titleLarge.fontSize,
                lineHeight = AppTheme.typography.titleLarge.lineHeight,
                fontWeight = FontWeight.SemiBold,
            ),
            color = AppTheme.colors.textPrimary,
        )
        ClockPanel(state.settings, onSettings)
    }
}

@ScreenPreviews
@Composable
private fun TimeLogPreview() {
    PreviewSurface {
        TimeLogContent(CheckoutUiState(section = KitchenSection.TimeLog), {})
    }
}
