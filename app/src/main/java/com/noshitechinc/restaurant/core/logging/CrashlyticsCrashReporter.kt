package com.noshitechinc.restaurant.core.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsCrashReporter(private val crashlytics: FirebaseCrashlytics) : CrashReporter {
    override fun setUserId(id: String?) = crashlytics.setUserId(id.orEmpty())
    override fun setKey(key: String, value: String) = crashlytics.setCustomKey(key, value)
    override fun log(message: String) = crashlytics.log(message)
    override fun recordException(throwable: Throwable) = crashlytics.recordException(throwable)
}
