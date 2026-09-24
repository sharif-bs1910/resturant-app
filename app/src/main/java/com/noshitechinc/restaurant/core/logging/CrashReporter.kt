package com.noshitechinc.restaurant.core.logging

interface CrashReporter {
    fun setUserId(id: String?)
    fun setKey(key: String, value: String)
    fun log(message: String)
    fun recordException(throwable: Throwable)
}

object NoOpCrashReporter : CrashReporter {
    override fun setUserId(id: String?) = Unit
    override fun setKey(key: String, value: String) = Unit
    override fun log(message: String) = Unit
    override fun recordException(throwable: Throwable) = Unit
}
