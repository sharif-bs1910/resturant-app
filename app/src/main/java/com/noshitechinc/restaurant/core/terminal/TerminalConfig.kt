package com.noshitechinc.restaurant.core.terminal

data class TerminalConfig(
    val enabled: Boolean,
    val locationId: String,
    val stubBaseUrl: String,
    val useSimulatedReader: Boolean) {
    val stubConfigured: Boolean
        get() = enabled && stubBaseUrl.isNotBlank()
}
