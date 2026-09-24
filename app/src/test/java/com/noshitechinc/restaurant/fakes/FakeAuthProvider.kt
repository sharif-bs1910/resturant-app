package com.noshitechinc.restaurant.fakes

import com.noshitechinc.restaurant.core.auth.AuthProvider
import com.noshitechinc.restaurant.core.auth.TokenPair
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.delay

class FakeAuthProvider(private val result: TokenPair?, private val delayMillis: Long = 0) : AuthProvider {
    val calls = AtomicInteger(0)

    override suspend fun refresh(refreshToken: String): TokenPair? {
        calls.incrementAndGet()
        if (delayMillis > 0) delay(delayMillis)
        return result
    }
}
