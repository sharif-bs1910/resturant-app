package com.noshitechinc.restaurant.feature.terminalharness

import com.noshitechinc.restaurant.core.common.UiText
import com.noshitechinc.restaurant.core.terminal.model.ReaderInfo

enum class TerminalHarnessPhase {
    Idle,
    Discovering,
    ReadyToConnect,
    Connecting,
    Connected,
    CreatingIntent,
    ProcessingPayment,
    Succeeded,
    Declined,
    Error,
}

data class TerminalHarnessUiState(
    val enabled: Boolean = false,
    val simulated: Boolean = true,
    val locationId: String = "",
    val stubBaseUrl: String = "",
    val phase: TerminalHarnessPhase = TerminalHarnessPhase.Idle,
    val readers: List<ReaderInfo> = emptyList(),
    val selectedReaderId: String? = null,
    val connectedSerial: String? = null,
    val amountCents: Long = 100,
    val statusText: UiText? = null,
    val lastPaymentIntentId: String? = null,
)
