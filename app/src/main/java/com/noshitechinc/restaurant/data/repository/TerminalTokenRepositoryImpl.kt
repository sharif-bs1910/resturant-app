package com.noshitechinc.restaurant.data.repository

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.map
import com.noshitechinc.restaurant.core.network.SafeApiCall
import com.noshitechinc.restaurant.data.remote.api.TerminalStubApi
import com.noshitechinc.restaurant.domain.repository.TerminalTokenRepository
import javax.inject.Inject

class TerminalTokenRepositoryImpl @Inject constructor(private val api: TerminalStubApi, private val safeApiCall: SafeApiCall) :
    TerminalTokenRepository {
    override suspend fun fetchConnectionToken(): ApiResult<String> = safeApiCall { api.createConnectionToken() }.map { it.secret }
}
