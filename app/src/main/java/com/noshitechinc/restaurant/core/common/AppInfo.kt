package com.noshitechinc.restaurant.core.common

enum class AppEnvironment {
    Dev,
    Staging,
    Prod,
    ;

    companion object {
        fun from(value: String): AppEnvironment = entries.first { it.name.equals(value, ignoreCase = true) }
    }
}

data class AppInfo(val environment: AppEnvironment, val versionName: String, val versionCode: Int)
