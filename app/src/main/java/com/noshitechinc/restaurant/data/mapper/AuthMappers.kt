package com.noshitechinc.restaurant.data.mapper

import com.noshitechinc.restaurant.core.auth.TokenPair
import com.noshitechinc.restaurant.data.remote.dto.TokenResponseDto

fun TokenResponseDto.toTokenPair(): TokenPair = TokenPair(accessToken = accessToken, refreshToken = refreshToken)
