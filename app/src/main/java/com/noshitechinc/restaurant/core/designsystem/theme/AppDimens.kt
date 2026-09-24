package com.noshitechinc.restaurant.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
)

@Immutable
data class AppRadius(
    val none: Dp = 0.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val pill: Dp = 999.dp,
)

@Immutable
data class AppBorder(val thin: Dp = 1.dp, val medium: Dp = 1.5.dp, val thick: Dp = 2.dp, val focus: Dp = 2.dp)

@Immutable
data class AppSizes(
    val minTouchTarget: Dp = 48.dp,
    val buttonSmall: Dp = 40.dp,
    val buttonMedium: Dp = 48.dp,
    val buttonLarge: Dp = 56.dp,
    val inputHeight: Dp = 56.dp,
    val iconSm: Dp = 16.dp,
    val iconMd: Dp = 24.dp,
    val iconLg: Dp = 32.dp,
    val iconXl: Dp = 48.dp,
    val illustration: Dp = 72.dp,
    val progressSmall: Dp = 20.dp,
    val stepperButton: Dp = 48.dp,
    val stepperValueMinWidth: Dp = 48.dp,
    val keypadKey: Dp = 72.dp,
    val keypadMaxWidth: Dp = 360.dp,
    val cardImage: Dp = 88.dp,
    val badgeMinHeight: Dp = 28.dp,
    val skeletonLine: Dp = 16.dp,
    val listRowMinHeight: Dp = 56.dp,
    val dialogMaxWidth: Dp = 560.dp,
    val formMaxWidth: Dp = 640.dp,
    val contentMaxWidth: Dp = 1200.dp,
)
