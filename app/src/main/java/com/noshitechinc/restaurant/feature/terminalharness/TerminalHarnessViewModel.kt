package com.noshitechinc.restaurant.feature.terminalharness

import androidx.lifecycle.viewModelScope
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.UiText
import com.noshitechinc.restaurant.core.terminal.TerminalClient
import com.noshitechinc.restaurant.core.terminal.TerminalConfig
import com.noshitechinc.restaurant.core.terminal.model.PaymentOutcome
import com.noshitechinc.restaurant.core.terminal.model.PaymentRequest
import com.noshitechinc.restaurant.core.ui.BaseViewModel
import com.noshitechinc.restaurant.core.ui.MessageTone
import com.noshitechinc.restaurant.core.ui.ShowMessage
import com.noshitechinc.restaurant.core.ui.error.toUiText
import com.noshitechinc.restaurant.domain.repository.TerminalPaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TerminalHarnessViewModel @Inject constructor(
    private val terminalClient: TerminalClient,
    private val paymentRepository: TerminalPaymentRepository,
    config: TerminalConfig,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(
        TerminalHarnessUiState(
            enabled = config.enabled,
            simulated = config.useSimulatedReader,
            locationId = config.locationId,
            stubBaseUrl = config.stubBaseUrl,
        ),
    )
    val uiState: StateFlow<TerminalHarnessUiState> = _uiState.asStateFlow()

    init {
        if (config.enabled) {
            viewModelScope.launch {
                when (val result = terminalClient.ensureInitialized()) {
                    is ApiResult.Failure -> _uiState.update {
                        it.copy(phase = TerminalHarnessPhase.Error, statusText = result.error.toUiText())
                    }

                    is ApiResult.Success -> Unit
                }
            }
        }
    }

    fun onPermissionDenied() {
        _uiState.update {
            it.copy(
                phase = TerminalHarnessPhase.Error,
                statusText = UiText.Resource(R.string.terminal_harness_need_location),
            )
        }
    }

    fun onToggleSimulated(simulated: Boolean) {
        _uiState.update { it.copy(simulated = simulated) }
    }

    fun onSelectReader(readerId: String) {
        _uiState.update { it.copy(selectedReaderId = readerId) }
    }

    fun onTestStub() {
        launchSubmit(KEY_STUB) {
            _uiState.update { it.copy(statusText = null) }
            when (val result = paymentRepository.createPaymentIntent(PaymentRequest(amountCents = 100))) {
                is ApiResult.Success -> {
                    sendEffect(
                        ShowMessage(
                            UiText.Resource(R.string.terminal_harness_stub_ok),
                            MessageTone.Success,
                        ),
                    )
                    _uiState.update {
                        it.copy(
                            lastPaymentIntentId = result.data.id,
                            statusText = UiText.Resource(R.string.terminal_harness_stub_ok),
                        )
                    }
                }

                is ApiResult.Failure -> {
                    presentError(result.error)
                    _uiState.update {
                        it.copy(phase = TerminalHarnessPhase.Error, statusText = result.error.toUiText())
                    }
                }
            }
        }
    }

    fun onDiscover() {
        launchSubmit(KEY_DISCOVER) {
            _uiState.update {
                it.copy(phase = TerminalHarnessPhase.Discovering, statusText = null, readers = emptyList())
            }
            when (val result = terminalClient.discoverReaders(_uiState.value.simulated)) {
                is ApiResult.Success -> {
                    val readers = result.data
                    _uiState.update { state ->
                        state.copy(
                            phase = if (readers.isEmpty()) {
                                TerminalHarnessPhase.Error
                            } else {
                                TerminalHarnessPhase.ReadyToConnect
                            },
                            readers = readers,
                            selectedReaderId = readers.firstOrNull()?.id,
                            statusText = if (readers.isEmpty()) {
                                UiText.Resource(R.string.terminal_harness_no_readers)
                            } else {
                                null
                            },
                        )
                    }
                }

                is ApiResult.Failure -> {
                    presentError(result.error)
                    _uiState.update {
                        it.copy(phase = TerminalHarnessPhase.Error, statusText = result.error.toUiText())
                    }
                }
            }
        }
    }

    fun onConnect() {
        launchSubmit(KEY_CONNECT) {
            val state = _uiState.value
            val reader = state.readers.firstOrNull { it.id == state.selectedReaderId }
                ?: state.readers.firstOrNull()
            if (reader == null) {
                _uiState.update {
                    it.copy(
                        phase = TerminalHarnessPhase.Error,
                        statusText = UiText.Resource(R.string.terminal_harness_select_reader),
                    )
                }
                return@launchSubmit
            }
            _uiState.update { it.copy(phase = TerminalHarnessPhase.Connecting, statusText = null) }
            val locationId = if (state.simulated) {
                reader.locationId.orEmpty().ifBlank { state.locationId }
            } else {
                state.locationId
            }
            when (val result = terminalClient.connect(reader, locationId)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        phase = TerminalHarnessPhase.Connected,
                        connectedSerial = result.data.serialNumber ?: result.data.id,
                        statusText = null,
                    )
                }

                is ApiResult.Failure -> {
                    presentError(result.error)
                    _uiState.update {
                        it.copy(phase = TerminalHarnessPhase.Error, statusText = result.error.toUiText())
                    }
                }
            }
        }
    }

    fun onDisconnect() {
        launchSubmit(KEY_DISCONNECT) {
            when (val result = terminalClient.disconnect()) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        phase = TerminalHarnessPhase.Idle,
                        connectedSerial = null,
                        statusText = null,
                    )
                }

                is ApiResult.Failure -> presentError(result.error)
            }
        }
    }

    fun onPay() {
        launchSubmit(KEY_PAY) {
            val amount = _uiState.value.amountCents
            _uiState.update { it.copy(phase = TerminalHarnessPhase.CreatingIntent, statusText = null) }
            when (
                val created = paymentRepository.createPaymentIntent(
                    PaymentRequest(amountCents = amount, currency = "usd"),
                )
            ) {
                is ApiResult.Failure -> {
                    presentError(created.error)
                    _uiState.update {
                        it.copy(phase = TerminalHarnessPhase.Error, statusText = created.error.toUiText())
                    }
                    return@launchSubmit
                }

                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            phase = TerminalHarnessPhase.ProcessingPayment,
                            lastPaymentIntentId = created.data.id,
                        )
                    }
                    when (val paid = terminalClient.processPayment(created.data.clientSecret)) {
                        is ApiResult.Failure -> {
                            presentError(paid.error)
                            _uiState.update {
                                it.copy(
                                    phase = TerminalHarnessPhase.Error,
                                    statusText = paid.error.toUiText(),
                                )
                            }
                        }

                        is ApiResult.Success -> when (val outcome = paid.data) {
                            is PaymentOutcome.Succeeded -> {
                                sendEffect(
                                    ShowMessage(
                                        UiText.Resource(R.string.terminal_harness_pay_success),
                                        MessageTone.Success,
                                    ),
                                )
                                _uiState.update {
                                    it.copy(
                                        phase = TerminalHarnessPhase.Succeeded,
                                        lastPaymentIntentId = outcome.paymentIntentId,
                                        statusText = null,
                                    )
                                }
                            }

                            is PaymentOutcome.Declined -> _uiState.update {
                                it.copy(
                                    phase = TerminalHarnessPhase.Declined,
                                    lastPaymentIntentId = outcome.paymentIntentId,
                                    statusText = UiText.Dynamic(outcome.message),
                                )
                            }

                            is PaymentOutcome.Canceled -> _uiState.update {
                                it.copy(
                                    phase = TerminalHarnessPhase.Connected,
                                    statusText = UiText.Resource(R.string.terminal_harness_canceled),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onCancelPayment() {
        terminalClient.cancelPayment()
        _uiState.update {
            it.copy(
                phase = TerminalHarnessPhase.Connected,
                statusText = UiText.Resource(R.string.terminal_harness_canceled),
            )
        }
    }

    private companion object {
        const val KEY_DISCOVER = "terminal_discover"
        const val KEY_CONNECT = "terminal_connect"
        const val KEY_DISCONNECT = "terminal_disconnect"
        const val KEY_PAY = "terminal_pay"
        const val KEY_STUB = "terminal_stub"
    }
}
