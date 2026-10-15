package com.noshitechinc.restaurant.core.terminal.stripe

import android.content.Context
import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.terminal.TerminalClient
import com.noshitechinc.restaurant.core.terminal.TerminalConfig
import com.noshitechinc.restaurant.core.terminal.TerminalConnectionState
import com.noshitechinc.restaurant.core.terminal.model.PaymentOutcome
import com.noshitechinc.restaurant.core.terminal.model.ReaderInfo
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.callable.DiscoveryListener
import com.stripe.stripeterminal.external.callable.MobileReaderListener
import com.stripe.stripeterminal.external.callable.OfflineListener
import com.stripe.stripeterminal.external.callable.TerminalListener
import com.stripe.stripeterminal.external.models.CollectPaymentIntentConfiguration
import com.stripe.stripeterminal.external.models.ConfirmPaymentIntentConfiguration
import com.stripe.stripeterminal.external.models.ConnectionConfiguration
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration
import com.stripe.stripeterminal.external.models.LocaleConfig
import com.stripe.stripeterminal.external.models.OfflineStatus
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.PaymentIntentStatus
import com.stripe.stripeterminal.external.models.PaymentStatus
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.TerminalException
import com.stripe.stripeterminal.ktx.connectReader
import com.stripe.stripeterminal.ktx.disconnectReader
import com.stripe.stripeterminal.ktx.processPaymentIntent
import com.stripe.stripeterminal.ktx.retrievePaymentIntent
import com.stripe.stripeterminal.log.LogLevel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

@Singleton
class StripeTerminalClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val config: TerminalConfig,
    private val tokenProvider: AppConnectionTokenProvider,
) : TerminalClient {
    private val initMutex = Mutex()
    private val _connectionState = MutableStateFlow(TerminalConnectionState.Idle)
    override val connectionState: StateFlow<TerminalConnectionState> = _connectionState.asStateFlow()

    private var discoverCancelable: Cancelable? = null
    private var paymentCancelable: Cancelable? = null

    private val terminalListener = object : TerminalListener {
        override fun onConnectionStatusChange(status: ConnectionStatus) {
            Timber.d("Terminal connection status: %s", status)
        }

        override fun onPaymentStatusChange(status: PaymentStatus) {
            Timber.d("Terminal payment status: %s", status)
        }
    }

    private val mobileReaderListener = object : MobileReaderListener {}

    private val offlineListener = object : OfflineListener {
        override fun onOfflineStatusChange(offlineStatus: OfflineStatus) = Unit
        override fun onPaymentIntentForwarded(paymentIntent: PaymentIntent, e: TerminalException?) = Unit
        override fun onForwardingFailure(e: TerminalException) = Unit
    }

    override suspend fun ensureInitialized(): ApiResult<Unit> {
        if (!config.enabled) {
            return ApiResult.Failure(AppError.Unknown(IllegalStateException("Terminal disabled")))
        }
        return initMutex.withLock {
            try {
                if (!Terminal.isInitialized()) {
                    Terminal.init(
                        context.applicationContext,
                        LogLevel.VERBOSE,
                        tokenProvider,
                        terminalListener,
                        offlineListener,
                        LocaleConfig.CardLanguagePreferenceIfAvailable,
                    )
                }
                ApiResult.Success(Unit)
            } catch (e: TerminalException) {
                ApiResult.Failure(e.toAppError())
            } catch (e: Exception) {
                ApiResult.Failure(AppError.Unknown(e))
            }
        }
    }

    override suspend fun discoverReaders(simulated: Boolean): ApiResult<List<ReaderInfo>> {
        ensureInitialized().onFailure { return ApiResult.Failure(it) }
        _connectionState.value = TerminalConnectionState.Discovering
        return try {
            val readers = discoverBluetooth(simulated)
            _connectionState.value = if (Terminal.getInstance().connectedReader != null) {
                TerminalConnectionState.Connected
            } else {
                TerminalConnectionState.Idle
            }
            ApiResult.Success(readers.map { it.toReaderInfo() })
        } catch (e: TerminalException) {
            _connectionState.value = TerminalConnectionState.Idle
            ApiResult.Failure(e.toAppError())
        } catch (e: Exception) {
            _connectionState.value = TerminalConnectionState.Idle
            ApiResult.Failure(AppError.Unknown(e))
        }
    }

    private suspend fun discoverBluetooth(simulated: Boolean): List<Reader> = suspendCancellableCoroutine { cont ->
        val discoveryConfig = DiscoveryConfiguration.BluetoothDiscoveryConfiguration(
            timeout = 0,
            isSimulated = simulated,
        )
        var latest: List<Reader> = emptyList()
        var completed = false
        fun complete(result: Result<List<Reader>>) {
            if (completed || !cont.isActive) return
            completed = true
            cont.resumeWith(result)
        }
        discoverCancelable = Terminal.getInstance().discoverReaders(
            discoveryConfig,
            object : DiscoveryListener {
                override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
                    latest = readers
                    if (readers.isNotEmpty()) {
                        discoverCancelable?.cancel(NoOpCallback)
                        complete(Result.success(readers))
                    }
                }
            },
            object : Callback {
                override fun onSuccess() {
                    complete(Result.success(latest))
                }

                override fun onFailure(e: TerminalException) {
                    complete(Result.failure(e))
                }
            },
        )
        cont.invokeOnCancellation {
            discoverCancelable?.cancel(NoOpCallback)
        }
    }

    override fun cancelDiscover() {
        discoverCancelable?.cancel(object : Callback {
            override fun onSuccess() = Unit
            override fun onFailure(e: TerminalException) = Unit
        })
        discoverCancelable = null
    }

    override suspend fun connect(reader: ReaderInfo, locationId: String): ApiResult<ReaderInfo> {
        ensureInitialized().onFailure { return ApiResult.Failure(it) }
        val sdkReader = reader.sdkReader as? Reader
            ?: return ApiResult.Failure(AppError.Unknown(IllegalArgumentException("Invalid reader")))
        val resolvedLocation = when {
            locationId.isNotBlank() -> locationId
            !reader.locationId.isNullOrBlank() -> reader.locationId
            else -> config.locationId
        }
        if (resolvedLocation.isBlank()) {
            return ApiResult.Failure(AppError.Unknown(IllegalStateException("Missing location id")))
        }
        return try {
            val connectionConfig = ConnectionConfiguration.BluetoothConnectionConfiguration(
                resolvedLocation,
                true,
                mobileReaderListener,
            )
            val connected = Terminal.getInstance().connectReader(sdkReader, connectionConfig)
            _connectionState.value = TerminalConnectionState.Connected
            ApiResult.Success(connected.toReaderInfo())
        } catch (e: TerminalException) {
            ApiResult.Failure(e.toAppError())
        } catch (e: Exception) {
            ApiResult.Failure(AppError.Unknown(e))
        }
    }

    override suspend fun disconnect(): ApiResult<Unit> {
        if (!Terminal.isInitialized()) return ApiResult.Success(Unit)
        return try {
            Terminal.getInstance().disconnectReader()
            _connectionState.value = TerminalConnectionState.Idle
            ApiResult.Success(Unit)
        } catch (e: TerminalException) {
            ApiResult.Failure(e.toAppError())
        } catch (e: Exception) {
            ApiResult.Failure(AppError.Unknown(e))
        }
    }

    override suspend fun processPayment(clientSecret: String): ApiResult<PaymentOutcome> {
        ensureInitialized().onFailure { return ApiResult.Failure(it) }
        if (Terminal.getInstance().connectedReader == null) {
            return ApiResult.Failure(AppError.Unknown(IllegalStateException("Not connected")))
        }
        _connectionState.value = TerminalConnectionState.Busy
        return try {
            val terminal = Terminal.getInstance()
            val intent = terminal.retrievePaymentIntent(clientSecret)
            val processed = terminal.processPaymentIntent(
                intent,
                CollectPaymentIntentConfiguration.Builder().build(),
                ConfirmPaymentIntentConfiguration.Builder().build(),
            )
            _connectionState.value = TerminalConnectionState.Connected
            ApiResult.Success(processed.toOutcome())
        } catch (e: TerminalException) {
            _connectionState.value = TerminalConnectionState.Connected
            val declined = e.paymentIntent
            if (declined != null && declined.status == PaymentIntentStatus.REQUIRES_PAYMENT_METHOD) {
                ApiResult.Success(
                    PaymentOutcome.Declined(
                        paymentIntentId = declined.id,
                        message = e.errorMessage,
                    ),
                )
            } else {
                ApiResult.Failure(e.toAppError())
            }
        } catch (e: Exception) {
            _connectionState.value = TerminalConnectionState.Connected
            ApiResult.Failure(AppError.Unknown(e))
        }
    }

    override fun cancelPayment() {
        paymentCancelable?.cancel(object : Callback {
            override fun onSuccess() = Unit
            override fun onFailure(e: TerminalException) = Unit
        })
        paymentCancelable = null
    }

    private fun Reader.toReaderInfo(): ReaderInfo = ReaderInfo(
        id = id.orEmpty().ifBlank { serialNumber.orEmpty() },
        serialNumber = serialNumber,
        label = label,
        locationId = location?.id,
        sdkReader = this,
    )

    private fun PaymentIntent.toOutcome(): PaymentOutcome = when (status) {
        PaymentIntentStatus.SUCCEEDED,
        PaymentIntentStatus.REQUIRES_CAPTURE,
        -> PaymentOutcome.Succeeded(id.orEmpty())

        PaymentIntentStatus.CANCELED -> PaymentOutcome.Canceled()

        else -> PaymentOutcome.Declined(
            paymentIntentId = id,
            message = lastPaymentError?.message ?: "Payment not completed ($status)",
        )
    }

    private fun TerminalException.toAppError(): AppError {
        val detail = errorMessage.ifBlank { message }.orEmpty()
        return if (detail.isNotBlank()) {
            AppError.Validation(detail)
        } else {
            AppError.Unknown(this)
        }
    }

    private object NoOpCallback : Callback {
        override fun onSuccess() = Unit
        override fun onFailure(e: TerminalException) = Unit
    }

    private inline fun ApiResult<Unit>.onFailure(block: (AppError) -> Unit): ApiResult<Unit> {
        if (this is ApiResult.Failure) block(error)
        return this
    }
}
