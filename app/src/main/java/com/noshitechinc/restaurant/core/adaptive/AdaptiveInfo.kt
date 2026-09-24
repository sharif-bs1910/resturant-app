package com.noshitechinc.restaurant.core.adaptive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

enum class WidthClass {
    Compact,
    Medium,
    Expanded,
    ;

    companion object {
        const val MEDIUM_MIN_DP = 600
        const val EXPANDED_MIN_DP = 840

        fun fromWidthDp(widthDp: Int): WidthClass = when {
            widthDp >= EXPANDED_MIN_DP -> Expanded
            widthDp >= MEDIUM_MIN_DP -> Medium
            else -> Compact
        }
    }
}

data class AdaptiveInfo(val widthClass: WidthClass, val isTabletDevice: Boolean, val isLandscape: Boolean) {
    val usesTwoPane: Boolean get() = widthClass != WidthClass.Compact

    val cardColumns: Int
        get() = when (widthClass) {
            WidthClass.Compact -> 2
            WidthClass.Medium -> 3
            WidthClass.Expanded -> 4
        }

    companion object {
        fun from(widthDp: Int, heightDp: Int, smallestWidthDp: Int): AdaptiveInfo = AdaptiveInfo(
            widthClass = WidthClass.fromWidthDp(widthDp),
            isTabletDevice = OrientationPolicy.isTablet(smallestWidthDp),
            isLandscape = widthDp > heightDp,
        )
    }
}

@Composable
fun rememberAdaptiveInfo(): AdaptiveInfo {
    val configuration = LocalConfiguration.current
    return remember(
        configuration.screenWidthDp,
        configuration.screenHeightDp,
        configuration.smallestScreenWidthDp,
    ) {
        AdaptiveInfo.from(
            configuration.screenWidthDp,
            configuration.screenHeightDp,
            configuration.smallestScreenWidthDp,
        )
    }
}
