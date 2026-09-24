package com.noshitechinc.restaurant.core.network.interceptor

import com.noshitechinc.restaurant.core.auth.TokenPair
import com.noshitechinc.restaurant.core.common.AppEnvironment
import com.noshitechinc.restaurant.core.common.AppInfo
import com.noshitechinc.restaurant.core.network.AUTHORIZATION_HEADER
import com.noshitechinc.restaurant.core.network.NO_AUTH_HEADER
import com.noshitechinc.restaurant.fakes.FakeTokenStore
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

class InterceptorsTest {
    private val server = MockWebServer().apply { start() }

    @AfterTest
    fun tearDown() = server.shutdown()

    private fun client(store: FakeTokenStore) = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor(AppInfo(AppEnvironment.Dev, "0.1.0", 1)))
        .addInterceptor(AuthInterceptor(store))
        .build()

    @Test
    fun `adds common headers and bearer token`() {
        server.enqueue(MockResponse())
        client(FakeTokenStore(TokenPair("abc", null)))
            .newCall(Request.Builder().url(server.url("/")).build())
            .execute()
            .close()
        val recorded = server.takeRequest()
        assertEquals("Bearer abc", recorded.getHeader(AUTHORIZATION_HEADER))
        assertEquals("application/json", recorded.getHeader("Accept"))
        assertEquals("en", recorded.getHeader("Accept-Language"))
        assertEquals("0.1.0", recorded.getHeader("X-App-Version"))
        assertEquals("android", recorded.getHeader("X-Platform"))
    }

    @Test
    fun `no-auth requests skip the token and strip the marker header`() {
        server.enqueue(MockResponse())
        val request = Request.Builder().url(server.url("/")).header(NO_AUTH_HEADER, "true").build()
        client(FakeTokenStore(TokenPair("abc", null))).newCall(request).execute().close()
        val recorded = server.takeRequest()
        assertNull(recorded.getHeader(AUTHORIZATION_HEADER))
        assertNull(recorded.getHeader(NO_AUTH_HEADER))
    }
}
