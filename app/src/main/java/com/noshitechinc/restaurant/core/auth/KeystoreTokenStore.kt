package com.noshitechinc.restaurant.core.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class KeystoreTokenStore @Inject constructor(private val dataStore: DataStore<Preferences>, private val cipher: TokenCipher) : TokenStore {
    @Volatile
    private var cached: TokenPair? = null

    @Volatile
    private var hydrated = false
    private val mutex = Mutex()

    override fun currentTokens(): TokenPair? {
        if (!hydrated) runBlocking { hydrate() }
        return cached
    }

    override suspend fun hydrate() {
        mutex.withLock {
            if (hydrated) return
            val prefs = dataStore.data.first()
            val access = prefs[ACCESS_KEY]?.let(cipher::decrypt)
            cached = access?.let { TokenPair(it, prefs[REFRESH_KEY]?.let(cipher::decrypt)) }
            hydrated = true
        }
    }

    override suspend fun save(tokens: TokenPair) {
        mutex.withLock {
            dataStore.edit { prefs ->
                prefs[ACCESS_KEY] = cipher.encrypt(tokens.accessToken)
                val refresh = tokens.refreshToken
                if (refresh != null) {
                    prefs[REFRESH_KEY] = cipher.encrypt(refresh)
                } else {
                    prefs.remove(REFRESH_KEY)
                }
            }
            cached = tokens
            hydrated = true
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            dataStore.edit { prefs ->
                prefs.remove(ACCESS_KEY)
                prefs.remove(REFRESH_KEY)
            }
            cached = null
            hydrated = true
        }
    }

    private companion object {
        val ACCESS_KEY = stringPreferencesKey("access_token")
        val REFRESH_KEY = stringPreferencesKey("refresh_token")
    }
}
