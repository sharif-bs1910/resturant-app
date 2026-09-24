package com.noshitechinc.restaurant.core.network

import com.noshitechinc.restaurant.core.auth.SessionManager
import com.noshitechinc.restaurant.core.auth.TokenPair
import com.noshitechinc.restaurant.core.network.interceptor.AuthInterceptor
import com.noshitechinc.restaurant.fakes.FakeAuthProvider
import com.noshitechinc.restaurant.fakes.FakeTokenStore
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class TokenAuthenticatorTest {
    private val server = MockWebServer()

    @AfterTest
    fun tearDown() = server.shutdown()

    private fun serveOkOnlyFor(token: String) {
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                if (request.getHeader(AUTHORIZATION_HEADER) == "$BEARER_PREFIX$token") {
                    MockResponse().setResponseCode(200).setBody("ok")
                } else {
                    MockResponse().setResponseCode(401)
                }
        }
        server.start()
    }

    private fun client(store: FakeTokenStore, provider: FakeAuthProvider) = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(store))
        .authenticator(TokenAuthenticator(store, provider, SessionManager(store)))
        .build()

    private fun OkHttpClient.get(): Int = newCall(Request.Builder().url(server.url("/orders")).build()).execute().use { it.code }

    @Test
    fun `parallel 401s trigger a single refresh`() {
        serveOkOnlyFor("new")
        val store = FakeTokenStore(TokenPair("old", "refresh"))
        val provider = FakeAuthProvider(TokenPair("new", "refresh-2"), delayMillis = 200)
        val client = client(store, provider)
        val pool = Executors.newFixedThreadPool(3)
        val codes = List(3) { pool.submit(Callable { client.get() }) }.map { it.get(10, TimeUnit.SECONDS) }
        pool.shutdown()
        assertEquals(listOf(200, 200, 200), codes)
        assertEquals(1, provider.calls.get())
        assertEquals("new", store.currentTokens()?.accessToken)
    }

    @Test
    fun `failed refresh ends the session`() {
        serveOkOnlyFor("never")
        val store = FakeTokenStore(TokenPair("old", "refresh"))
        val provider = FakeAuthProvider(result = null)
        assertEquals(401, client(store, provider).get())
        assertNull(store.currentTokens())
    }

    @Test
    fun `retries at most once per request`() {
        serveOkOnlyFor("never")
        val store = FakeTokenStore(TokenPair("old", "refresh"))
        val provider = FakeAuthProvider(TokenPair("new", "refresh-2"))
        assertEquals(401, client(store, provider).get())
        assertEquals(2, server.requestCount)
    }

    @Test
    fun `requests without a token are not retried`() {
        serveOkOnlyFor("never")
        val store = FakeTokenStore(initial = null)
        val provider = FakeAuthProvider(TokenPair("new", null))
        assertEquals(401, client(store, provider).get())
        assertEquals(0, provider.calls.get())
        assertEquals(1, server.requestCount)
    }
}
