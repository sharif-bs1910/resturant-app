package com.noshitechinc.restaurant.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import com.noshitechinc.restaurant.core.common.coroutines.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

interface NetworkMonitor {
    val isOnline: StateFlow<Boolean>
}

@Singleton
class ConnectivityNetworkMonitor @Inject constructor(@ApplicationContext context: Context, @ApplicationScope scope: CoroutineScope) :
    NetworkMonitor {
    private val manager: ConnectivityManager = requireNotNull(context.getSystemService())

    override val isOnline: StateFlow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(manager.hasValidatedInternet())
            }

            override fun onLost(network: Network) {
                trySend(manager.hasValidatedInternet())
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                trySend(manager.hasValidatedInternet())
            }
        }
        manager.registerDefaultNetworkCallback(callback)
        trySend(manager.hasValidatedInternet())
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }
        .distinctUntilChanged()
        .stateIn(scope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), initialValue = true)

    private fun ConnectivityManager.hasValidatedInternet(): Boolean = getNetworkCapabilities(activeNetwork)?.let {
        it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } ?: false

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
