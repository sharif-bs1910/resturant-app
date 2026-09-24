package com.noshitechinc.restaurant.core.logging

import com.noshitechinc.restaurant.core.common.AppEnvironment
import com.noshitechinc.restaurant.core.common.AppInfo
import timber.log.Timber

object AppLogging {
    fun install(appInfo: AppInfo, reporter: CrashReporter) {
        if (appInfo.environment == AppEnvironment.Dev) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree(reporter))
        }
        reporter.setKey("environment", appInfo.environment.name.lowercase())
        reporter.setKey("version", "${appInfo.versionName} (${appInfo.versionCode})")
        reporter.setKey("device_class", "tablet")
    }
}
