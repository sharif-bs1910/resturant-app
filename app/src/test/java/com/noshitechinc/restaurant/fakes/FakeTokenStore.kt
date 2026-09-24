package com.noshitechinc.restaurant.fakes

import com.noshitechinc.restaurant.core.auth.TokenPair
import com.noshitechinc.restaurant.core.auth.TokenStore

class FakeTokenStore(initial: TokenPair? = null) : TokenStore {
    @Volatile
    private var tokens: TokenPair? = initial

    override fun currentTokens(): TokenPair? = tokens

    override suspend fun hydrate() = Unit

    override suspend fun save(tokens: TokenPair) {
        this.tokens = tokens
    }

    override suspend fun clear() {
        tokens = null
    }
}
