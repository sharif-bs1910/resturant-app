package com.noshitechinc.restaurant.core.logging

import com.noshitechinc.restaurant.core.common.AppEnvironment
import com.noshitechinc.restaurant.core.common.AppInfo
import com.noshitechinc.restaurant.fakes.FakeCrashReporter
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import timber.log.Timber

class CrashReportingTreeTest {
    private val reporter = FakeCrashReporter()

    @AfterTest
    fun tearDown() = Timber.uprootAll()

    @Test
    fun `drops debug logs and forwards info as breadcrumbs`() {
        Timber.plant(CrashReportingTree(reporter))
        Timber.d("debug detail")
        Timber.i("order screen opened")
        assertEquals(1, reporter.logs.size)
        assertTrue(reporter.logs.single().startsWith("I/"))
    }

    @Test
    fun `records exceptions logged at warn or above`() {
        Timber.plant(CrashReportingTree(reporter))
        val error = IllegalStateException("boom")
        Timber.w(error, "warned")
        Timber.i(RuntimeException("ignored"), "info with throwable")
        assertEquals(listOf<Throwable>(error), reporter.exceptions)
    }

    @Test
    fun `install sets environment keys and plants crash tree outside dev`() {
        AppLogging.install(AppInfo(AppEnvironment.Staging, "1.2.3", 7), reporter)
        Timber.e(IllegalArgumentException("bad"), "failed")
        assertEquals("staging", reporter.keys["environment"])
        assertEquals("1.2.3 (7)", reporter.keys["version"])
        assertEquals("tablet", reporter.keys["device_class"])
        assertEquals(1, reporter.exceptions.size)
    }
}
