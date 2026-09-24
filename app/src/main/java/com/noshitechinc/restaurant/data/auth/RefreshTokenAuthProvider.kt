package com.noshitechinc.restaurant.data.auth

import com.noshitechinc.restaurant.core.auth.AuthProvider
import com.noshitechinc.restaurant.core.auth.TokenPair
import com.noshitechinc.restaurant.data.mapper.toTokenPair
import com.noshitechinc.restaurant.data.remote.api.AuthApi
import com.noshitechinc.restaurant.data.remote.dto.RefreshTokenRequestDto
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import timber.log.Timber

class RefreshTokenAuthProvider @Inject constructor(private val authApi: AuthApi) : AuthProvider {
    @Suppress("TooGenericExceptionCaught")
    override suspend fun refresh(refreshToken: String): TokenPair? = try {
        authApi.refresh(RefreshTokenRequestDto(refreshToken)).toTokenPair()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Timber.w(e, "Token refresh failed")
        null
    }
}
