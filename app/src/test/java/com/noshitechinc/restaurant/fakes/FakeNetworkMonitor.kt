package com.noshitechinc.restaurant.fakes

import com.noshitechinc.restaurant.core.network.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeNetworkMonitor(online: Boolean = true) : NetworkMonitor {
    private val state = MutableStateFlow(online)
    override val isOnline: StateFlow<Boolean> = state

    fun setOnline(online: Boolean) {
        state.value = online
    }
}
