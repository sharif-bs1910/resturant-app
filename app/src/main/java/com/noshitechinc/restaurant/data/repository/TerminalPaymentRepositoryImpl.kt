package com.noshitechinc.restaurant.data.repository

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.map
import com.noshitechinc.restaurant.core.network.SafeApiCall
import com.noshitechinc.restaurant.core.terminal.model.PaymentIntentRef
import com.noshitechinc.restaurant.core.terminal.model.PaymentRequest
import com.noshitechinc.restaurant.data.mapper.toDomain
import com.noshitechinc.restaurant.data.remote.api.TerminalStubApi
import com.noshitechinc.restaurant.data.remote.dto.terminal.CreatePaymentIntentRequestDto
import com.noshitechinc.restaurant.domain.repository.TerminalPaymentRepository
import javax.inject.Inject

class TerminalPaymentRepositoryImpl @Inject constructor(private val api: TerminalStubApi, private val safeApiCall: SafeApiCall) :
    TerminalPaymentRepository {
    override suspend fun createPaymentIntent(request: PaymentRequest): ApiResult<PaymentIntentRef> = safeApiCall {
        api.createPaymentIntent(
            CreatePaymentIntentRequestDto(
                amountCents = request.amountCents,
                currency = request.currency,
            ),
        )
    }.map { it.toDomain() }
}
