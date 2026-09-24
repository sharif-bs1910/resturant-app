package com.noshitechinc.restaurant.core.auth

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.noshitechinc.restaurant.fakes.FakeTokenCipher
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class KeystoreTokenStoreTest {
    private val dir: File = Files.createTempDirectory("tokens").toFile()
    private val dataStore =
        PreferenceDataStoreFactory.create(produceFile = { File(dir, "session.preferences_pb") })
    private val cipher = FakeTokenCipher()

    @Test
    fun `save encrypts values and caches tokens`() = runTest {
        val store = KeystoreTokenStore(dataStore, cipher)
        store.save(TokenPair("access-1", "refresh-1"))
        val raw = dataStore.data.first()[stringPreferencesKey("access_token")].orEmpty()
        assertFalse(raw.contains("access-1"))
        assertEquals(TokenPair("access-1", "refresh-1"), store.currentTokens())
    }

    @Test
    fun `hydrate restores tokens written earlier`() = runTest {
        dataStore.edit {
            it[stringPreferencesKey("access_token")] = cipher.encrypt("access-2")
            it[stringPreferencesKey("refresh_token")] = cipher.encrypt("refresh-2")
        }
        val store = KeystoreTokenStore(dataStore, cipher)
        store.hydrate()
        assertEquals(TokenPair("access-2", "refresh-2"), store.currentTokens())
    }

    @Test
    fun `clear removes tokens`() = runTest {
        val store = KeystoreTokenStore(dataStore, cipher)
        store.save(TokenPair("access-3", null))
        store.clear()
        assertNull(store.currentTokens())
        assertNull(dataStore.data.first()[stringPreferencesKey("access_token")])
    }
}
