package com.noshitechinc.restaurant.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.noshitechinc.restaurant.BuildConfig
import com.noshitechinc.restaurant.core.logging.CrashReporter
import com.noshitechinc.restaurant.core.logging.CrashlyticsCrashReporter
import com.noshitechinc.restaurant.core.logging.NoOpCrashReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoggingModule {
    @Provides
    @Singleton
    fun crashReporter(@ApplicationContext context: Context): CrashReporter =
        if (BuildConfig.CRASH_REPORTING_ENABLED && FirebaseApp.getApps(context).isNotEmpty()) {
            CrashlyticsCrashReporter(FirebaseCrashlytics.getInstance())
        } else {
            NoOpCrashReporter
        }
}
