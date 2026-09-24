package com.noshitechinc.restaurant.core.adaptive

import android.content.pm.ActivityInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdaptiveInfoTest {
    @Test
    fun `width classes follow material breakpoints`() {
        assertEquals(WidthClass.Compact, WidthClass.fromWidthDp(599))
        assertEquals(WidthClass.Medium, WidthClass.fromWidthDp(600))
        assertEquals(WidthClass.Medium, WidthClass.fromWidthDp(839))
        assertEquals(WidthClass.Expanded, WidthClass.fromWidthDp(840))
    }

    @Test
    fun `tablet landscape uses two panes and four card columns`() {
        val info = AdaptiveInfo.from(widthDp = 1280, heightDp = 800, smallestWidthDp = 800)
        assertTrue(info.usesTwoPane)
        assertTrue(info.isTabletDevice)
        assertTrue(info.isLandscape)
        assertEquals(4, info.cardColumns)
    }

    @Test
    fun `tablet portrait still two pane with three columns`() {
        val info = AdaptiveInfo.from(widthDp = 800, heightDp = 1280, smallestWidthDp = 800)
        assertTrue(info.usesTwoPane)
        assertFalse(info.isLandscape)
        assertEquals(3, info.cardColumns)
    }

    @Test
    fun `narrow multi-window is single pane with two columns`() {
        val info = AdaptiveInfo.from(widthDp = 411, heightDp = 891, smallestWidthDp = 411)
        assertFalse(info.usesTwoPane)
        assertFalse(info.isTabletDevice)
        assertEquals(2, info.cardColumns)
    }

    @Test
    fun `orientation is always sensor landscape for tablet-only product`() {
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE, OrientationPolicy.requestedOrientation(600))
        assertEquals(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE, OrientationPolicy.requestedOrientation(599))
    }
}
