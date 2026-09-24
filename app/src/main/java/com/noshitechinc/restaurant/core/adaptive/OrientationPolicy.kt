package com.noshitechinc.restaurant.core.adaptive

import android.content.pm.ActivityInfo

object OrientationPolicy {
    const val TABLET_MIN_SMALLEST_WIDTH_DP = 600

    fun isTablet(smallestScreenWidthDp: Int): Boolean = smallestScreenWidthDp >= TABLET_MIN_SMALLEST_WIDTH_DP

    @Suppress("UNUSED_PARAMETER")
    fun requestedOrientation(smallestScreenWidthDp: Int): Int = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
}
