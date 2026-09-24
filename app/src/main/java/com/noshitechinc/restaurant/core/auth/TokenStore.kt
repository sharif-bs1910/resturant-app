package com.noshitechinc.restaurant.core.auth

interface TokenStore {
    fun currentTokens(): TokenPair?
    suspend fun hydrate()
    suspend fun save(tokens: TokenPair)
    suspend fun clear()
}
