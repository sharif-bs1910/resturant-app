package com.noshitechinc.restaurant.feature.selfservice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.noshitechinc.restaurant.core.designsystem.component.logo.LogoSurface
import com.noshitechinc.restaurant.core.designsystem.component.logo.NoshiLockup
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun SelfServiceTopBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.colors.card),
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.spacing.xxl, vertical = AppTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NoshiLockup(surface = LogoSurface.Light, markSize = AppTheme.sizes.logoMark)
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(AppTheme.border.thin)
                .background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
        )
    }
}

@ScreenPreviews
@Composable
private fun SelfServiceTopBarPreview() {
    PreviewSurface {
        SelfServiceTopBar()
    }
}
