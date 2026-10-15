package com.noshitechinc.restaurant.domain.repository

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.terminal.model.PaymentIntentRef
import com.noshitechinc.restaurant.core.terminal.model.PaymentRequest

interface TerminalPaymentRepository {
    suspend fun createPaymentIntent(request: PaymentRequest): ApiResult<PaymentIntentRef>
}
