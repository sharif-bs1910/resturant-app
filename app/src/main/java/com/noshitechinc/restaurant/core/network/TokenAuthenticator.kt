package com.noshitechinc.restaurant.core.network

import com.noshitechinc.restaurant.core.auth.AuthProvider
import com.noshitechinc.restaurant.core.auth.SessionManager
import com.noshitechinc.restaurant.core.auth.TokenStore
import com.noshitechinc.restaurant.core.network.interceptor.withBearer
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    private val authProvider: AuthProvider,
    private val sessionManager: SessionManager,
) : Authenticator {
    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        val failedToken = response.request.header(AUTHORIZATION_HEADER)?.removePrefix(BEARER_PREFIX) ?: return null
        if (response.priorResponse != null) return null
        return runBlocking { mutex.withLock { renew(response.request, failedToken) } }
    }

    private suspend fun renew(request: Request, failedToken: String): Request? {
        val current = tokenStore.currentTokens() ?: return null
        if (current.accessToken != failedToken) return request.withBearer(current.accessToken)
        val refreshed = current.refreshToken?.let { authProvider.refresh(it) }
        if (refreshed == null) {
            sessionManager.endSession()
            return null
        }
        tokenStore.save(refreshed)
        return request.withBearer(refreshed.accessToken)
    }
}
