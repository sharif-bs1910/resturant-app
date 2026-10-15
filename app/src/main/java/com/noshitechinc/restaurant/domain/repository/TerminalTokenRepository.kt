package com.noshitechinc.restaurant.domain.repository

import com.noshitechinc.restaurant.core.common.ApiResult

interface TerminalTokenRepository {
    suspend fun fetchConnectionToken(): ApiResult<String>
}
