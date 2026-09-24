package com.noshitechinc.restaurant.core.logging

import android.util.Log
import timber.log.Timber

class CrashReportingTree(private val reporter: CrashReporter) : Timber.Tree() {
    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.INFO

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        reporter.log("${label(priority)}/${tag ?: DEFAULT_TAG}: $message")
        if (t != null && priority >= Log.WARN) reporter.recordException(t)
    }

    private fun label(priority: Int): String = when (priority) {
        Log.INFO -> "I"
        Log.WARN -> "W"
        Log.ERROR -> "E"
        Log.ASSERT -> "A"
        else -> "D"
    }

    private companion object {
        const val DEFAULT_TAG = "App"
    }
}
