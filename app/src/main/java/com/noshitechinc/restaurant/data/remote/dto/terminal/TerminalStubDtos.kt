package com.noshitechinc.restaurant.data.remote.dto.terminal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConnectionTokenResponseDto(val secret: String)

@Serializable
data class CreatePaymentIntentRequestDto(val amountCents: Long, val currency: String)

@Serializable
data class CreatePaymentIntentResponseDto(val id: String, @SerialName("clientSecret") val clientSecret: String)
