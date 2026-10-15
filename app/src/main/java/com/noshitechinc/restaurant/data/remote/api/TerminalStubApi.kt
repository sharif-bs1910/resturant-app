package com.noshitechinc.restaurant.data.remote.api

import com.noshitechinc.restaurant.core.network.NO_AUTH_HEADER
import com.noshitechinc.restaurant.data.remote.dto.terminal.ConnectionTokenResponseDto
import com.noshitechinc.restaurant.data.remote.dto.terminal.CreatePaymentIntentRequestDto
import com.noshitechinc.restaurant.data.remote.dto.terminal.CreatePaymentIntentResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TerminalStubApi {
    @Headers("$NO_AUTH_HEADER: true")
    @POST("connection_token")
    suspend fun createConnectionToken(): ConnectionTokenResponseDto

    @Headers("$NO_AUTH_HEADER: true")
    @POST("payment_intents")
    suspend fun createPaymentIntent(@Body body: CreatePaymentIntentRequestDto): CreatePaymentIntentResponseDto
}
