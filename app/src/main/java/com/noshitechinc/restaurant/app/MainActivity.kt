package com.noshitechinc.restaurant.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noshitechinc.restaurant.core.adaptive.OrientationPolicy
import com.noshitechinc.restaurant.core.auth.SessionManager
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.network.NetworkMonitor
import com.noshitechinc.restaurant.core.ui.LocalIsOnline
import com.noshitechinc.restaurant.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        requestedOrientation = OrientationPolicy.requestedOrientation(resources.configuration.smallestScreenWidthDp)
        enableEdgeToEdge()
        setContent {
            val isOnline by networkMonitor.isOnline.collectAsStateWithLifecycle()
            AppTheme {
                CompositionLocalProvider(LocalIsOnline provides isOnline) {
                    AppNavHost(sessionEnded = sessionManager.sessionEnded)
                }
            }
        }
    }
}
