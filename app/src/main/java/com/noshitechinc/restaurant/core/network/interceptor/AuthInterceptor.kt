package com.noshitechinc.restaurant.core.network.interceptor

import com.noshitechinc.restaurant.core.auth.TokenStore
import com.noshitechinc.restaurant.core.network.AUTHORIZATION_HEADER
import com.noshitechinc.restaurant.core.network.BEARER_PREFIX
import com.noshitechinc.restaurant.core.network.NO_AUTH_HEADER
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor @Inject constructor(private val tokenStore: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.header(NO_AUTH_HEADER) != null) {
            return chain.proceed(request.newBuilder().removeHeader(NO_AUTH_HEADER).build())
        }
        val token = tokenStore.currentTokens()?.accessToken ?: return chain.proceed(request)
        return chain.proceed(request.withBearer(token))
    }
}

fun Request.withBearer(token: String): Request = newBuilder().header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token").build()
