package com.noshitechinc.restaurant.core.auth

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class SessionManager @Inject constructor(private val tokenStore: TokenStore) {
    private val _sessionEnded = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionEnded: SharedFlow<Unit> = _sessionEnded.asSharedFlow()

    suspend fun startSession(tokens: TokenPair) = tokenStore.save(tokens)

    suspend fun endSession() {
        tokenStore.clear()
        _sessionEnded.emit(Unit)
    }
}
