package com.noshitechinc.restaurant.fakes

import com.noshitechinc.restaurant.core.logging.CrashReporter

class FakeCrashReporter : CrashReporter {
    val logs = mutableListOf<String>()
    val exceptions = mutableListOf<Throwable>()
    val keys = mutableMapOf<String, String>()
    var capturedUserId: String? = null

    override fun setUserId(id: String?) {
        capturedUserId = id
    }

    override fun setKey(key: String, value: String) {
        keys[key] = value
    }

    override fun log(message: String) {
        logs += message
    }

    override fun recordException(throwable: Throwable) {
        exceptions += throwable
    }
}
