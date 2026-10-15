package com.noshitechinc.restaurant.core.terminal.model

data class ReaderInfo(
    val id: String,
    val serialNumber: String?,
    val label: String?,
    val locationId: String?,
    val sdkReader: Any? = null,
)

data class PaymentRequest(val amountCents: Long, val currency: String = "usd")

data class PaymentIntentRef(val id: String, val clientSecret: String)

sealed interface PaymentOutcome {
    data class Succeeded(val paymentIntentId: String) : PaymentOutcome
    data class Declined(val paymentIntentId: String?, val message: String) : PaymentOutcome
    data class Canceled(val message: String = "Canceled") : PaymentOutcome
}

sealed interface TerminalFailure {
    data class Message(val message: String) : TerminalFailure
    data object Disabled : TerminalFailure
    data object NotInitialized : TerminalFailure
    data object NoReaders : TerminalFailure
    data object NotConnected : TerminalFailure
}
