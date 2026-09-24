package com.noshitechinc.restaurant.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequestDto(val refreshToken: String)

@Serializable
data class TokenResponseDto(val accessToken: String, val refreshToken: String? = null)
