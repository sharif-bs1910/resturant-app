package com.noshitechinc.restaurant.data.mapper

import com.noshitechinc.restaurant.core.terminal.model.PaymentIntentRef
import com.noshitechinc.restaurant.data.remote.dto.terminal.CreatePaymentIntentResponseDto

fun CreatePaymentIntentResponseDto.toDomain(): PaymentIntentRef = PaymentIntentRef(
    id = id,
    clientSecret = clientSecret,
)
