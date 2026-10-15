package com.noshitechinc.restaurant.core.terminal.fake

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.terminal.TerminalClient
import com.noshitechinc.restaurant.core.terminal.TerminalConnectionState
import com.noshitechinc.restaurant.core.terminal.model.PaymentOutcome
import com.noshitechinc.restaurant.core.terminal.model.ReaderInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Scriptable Terminal client for unit tests and when Stripe SDK is disabled.
 */
class FakeTerminalClient(
    private val readers: List<ReaderInfo> = listOf(
        ReaderInfo(
            id = "fake-sim-1",
            serialNumber = "SIMULATED-001",
            label = "Simulated M2",
            locationId = "tml_simulated",
        ),
    ),
    var discoverDelayMs: Long = 0,
    var connectDelayMs: Long = 0,
    var payDelayMs: Long = 0,
    var nextPaymentOutcome: PaymentOutcome = PaymentOutcome.Succeeded("pi_fake"),
    var failDiscoverWith: AppError? = null,
    var failConnectWith: AppError? = null,
    var failPayWith: AppError? = null,
    var initialized: Boolean = true,
) : TerminalClient {
    private val _connectionState = MutableStateFlow(TerminalConnectionState.Idle)
    override val connectionState: StateFlow<TerminalConnectionState> = _connectionState.asStateFlow()

    var connectedReader: ReaderInfo? = null
        private set

    var discoverCount: Int = 0
        private set

    var payCount: Int = 0
        private set

    private var discoverCanceled = false
    private var payCanceled = false

    override suspend fun ensureInitialized(): ApiResult<Unit> = if (initialized) {
        ApiResult.Success(Unit)
    } else {
        ApiResult.Failure(AppError.Unknown(IllegalStateException("Terminal not initialized")))
    }

    override suspend fun discoverReaders(simulated: Boolean): ApiResult<List<ReaderInfo>> {
        discoverCanceled = false
        discoverCount += 1
        _connectionState.value = TerminalConnectionState.Discovering
        delay(discoverDelayMs)
        if (discoverCanceled) {
            _connectionState.value = if (connectedReader != null) {
                TerminalConnectionState.Connected
            } else {
                TerminalConnectionState.Idle
            }
            return ApiResult.Failure(AppError.Unknown(IllegalStateException("Discover canceled")))
        }
        failDiscoverWith?.let {
            _connectionState.value = TerminalConnectionState.Idle
            return ApiResult.Failure(it)
        }
        _connectionState.value = if (connectedReader != null) {
            TerminalConnectionState.Connected
        } else {
            TerminalConnectionState.Idle
        }
        return ApiResult.Success(readers)
    }

    override fun cancelDiscover() {
        discoverCanceled = true
    }

    override suspend fun connect(reader: ReaderInfo, locationId: String): ApiResult<ReaderInfo> {
        delay(connectDelayMs)
        failConnectWith?.let { return ApiResult.Failure(it) }
        val connected = reader.copy(locationId = locationId.ifBlank { reader.locationId })
        connectedReader = connected
        _connectionState.value = TerminalConnectionState.Connected
        return ApiResult.Success(connected)
    }

    override suspend fun disconnect(): ApiResult<Unit> {
        connectedReader = null
        _connectionState.value = TerminalConnectionState.Idle
        return ApiResult.Success(Unit)
    }

    override suspend fun processPayment(clientSecret: String): ApiResult<PaymentOutcome> {
        payCanceled = false
        payCount += 1
        if (connectedReader == null) {
            return ApiResult.Failure(AppError.Unknown(IllegalStateException("Not connected")))
        }
        _connectionState.value = TerminalConnectionState.Busy
        delay(payDelayMs)
        if (payCanceled) {
            _connectionState.value = TerminalConnectionState.Connected
            return ApiResult.Success(PaymentOutcome.Canceled())
        }
        failPayWith?.let {
            _connectionState.value = TerminalConnectionState.Connected
            return ApiResult.Failure(it)
        }
        _connectionState.value = TerminalConnectionState.Connected
        return ApiResult.Success(nextPaymentOutcome)
    }

    override fun cancelPayment() {
        payCanceled = true
    }
}
