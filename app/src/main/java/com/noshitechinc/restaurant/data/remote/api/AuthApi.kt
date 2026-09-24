package com.noshitechinc.restaurant.data.remote.api

import com.noshitechinc.restaurant.core.network.NO_AUTH_HEADER
import com.noshitechinc.restaurant.data.remote.dto.RefreshTokenRequestDto
import com.noshitechinc.restaurant.data.remote.dto.TokenResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("$NO_AUTH_HEADER: true")
    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenRequestDto): TokenResponseDto
}
