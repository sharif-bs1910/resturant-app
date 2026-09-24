package com.noshitechinc.restaurant.app

import com.noshitechinc.restaurant.core.auth.TokenStore
import com.noshitechinc.restaurant.core.common.AppInfo
import com.noshitechinc.restaurant.core.common.coroutines.ApplicationScope
import com.noshitechinc.restaurant.core.common.coroutines.IoDispatcher
import com.noshitechinc.restaurant.core.logging.AppLogging
import com.noshitechinc.restaurant.core.logging.CrashReporter
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppStartup @Inject constructor(
    private val appInfo: AppInfo,
    private val crashReporter: CrashReporter,
    private val tokenStore: TokenStore,
    @ApplicationScope private val applicationScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    fun run() {
        AppLogging.install(appInfo, crashReporter)
        applicationScope.launch(ioDispatcher) { tokenStore.hydrate() }
    }
}
