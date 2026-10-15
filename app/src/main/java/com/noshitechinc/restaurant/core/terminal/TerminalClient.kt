package com.noshitechinc.restaurant.core.terminal

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.terminal.model.PaymentOutcome
import com.noshitechinc.restaurant.core.terminal.model.ReaderInfo
import kotlinx.coroutines.flow.StateFlow

interface TerminalClient {
    val connectionState: StateFlow<TerminalConnectionState>

    suspend fun ensureInitialized(): ApiResult<Unit>

    suspend fun discoverReaders(simulated: Boolean): ApiResult<List<ReaderInfo>>

    fun cancelDiscover()

    suspend fun connect(reader: ReaderInfo, locationId: String): ApiResult<ReaderInfo>

    suspend fun disconnect(): ApiResult<Unit>

    suspend fun processPayment(clientSecret: String): ApiResult<PaymentOutcome>

    fun cancelPayment()
}

enum class TerminalConnectionState {
    Idle,
    Discovering,
    Connected,
    Busy,
}
