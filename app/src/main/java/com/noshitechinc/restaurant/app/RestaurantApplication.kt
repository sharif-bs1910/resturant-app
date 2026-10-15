package com.noshitechinc.restaurant.app

import android.app.Application
import com.noshitechinc.restaurant.BuildConfig
import com.stripe.stripeterminal.TerminalApplicationDelegate
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RestaurantApplication : Application() {
    @Inject
    lateinit var appStartup: AppStartup

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.TERMINAL_ENABLED) {
            TerminalApplicationDelegate.onCreate(this)
        }
        appStartup.run()
    }
}
