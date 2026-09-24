package com.noshitechinc.restaurant.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

enum class StatusTone { Neutral, Info, Success, Warning, Danger }

fun AppColors.toneColors(tone: StatusTone): Pair<Color, Color> = when (tone) {
    StatusTone.Neutral -> neutralContainer to onNeutralContainer
    StatusTone.Info -> infoContainer to onInfoContainer
    StatusTone.Success -> successContainer to onSuccessContainer
    StatusTone.Warning -> warningContainer to onWarningContainer
    StatusTone.Danger -> destructiveContainer to onDestructiveContainer
}

@Composable
@ReadOnlyComposable
fun StatusTone.containerColor(): Color = AppTheme.colors.toneColors(this).first

@Composable
@ReadOnlyComposable
fun StatusTone.contentColor(): Color = AppTheme.colors.toneColors(this).second
