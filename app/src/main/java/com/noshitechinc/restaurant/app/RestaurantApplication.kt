package com.noshitechinc.restaurant.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RestaurantApplication : Application() {
    @Inject
    lateinit var appStartup: AppStartup

    override fun onCreate() {
        super.onCreate()
        appStartup.run()
    }
}
