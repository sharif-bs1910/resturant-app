package com.noshitechinc.restaurant.core.auth

fun interface AuthProvider {
    suspend fun refresh(refreshToken: String): TokenPair?
}
