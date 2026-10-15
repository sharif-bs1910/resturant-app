package com.noshitechinc.restaurant.data.repository

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.network.SafeApiCall
import com.noshitechinc.restaurant.core.network.error.ErrorMapper
import com.noshitechinc.restaurant.core.network.error.JsonErrorBodyParser
import com.noshitechinc.restaurant.core.terminal.model.PaymentRequest
import com.noshitechinc.restaurant.data.remote.api.TerminalStubApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class TerminalRepositoriesTest {
    private lateinit var server: MockWebServer
    private lateinit var api: TerminalStubApi
    private lateinit var safeApiCall: SafeApiCall

    @BeforeTest
    fun setUp() {
        server = MockWebServer()
        server.start()
        val json = Json { ignoreUnknownKeys = true }
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TerminalStubApi::class.java)
        safeApiCall = SafeApiCall(ErrorMapper(JsonErrorBodyParser(json)))
    }

    @AfterTest
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `token repository returns connection secret`() = runTest {
        server.enqueue(
            MockResponse()
                .setBody(readFixture("terminal/connection_token.json"))
                .addHeader("Content-Type", "application/json"),
        )
        val repo = TerminalTokenRepositoryImpl(api, safeApiCall)
        val result = repo.fetchConnectionToken()
        assertIs<ApiResult.Success<String>>(result)
        assertEquals("pst_test_fake_connection_token", result.data)
        assertEquals("/connection_token", server.takeRequest().path)
    }

    @Test
    fun `payment repository returns payment intent ref`() = runTest {
        server.enqueue(
            MockResponse()
                .setBody(readFixture("terminal/payment_intent.json"))
                .addHeader("Content-Type", "application/json"),
        )
        val repo = TerminalPaymentRepositoryImpl(api, safeApiCall)
        val result = repo.createPaymentIntent(PaymentRequest(amountCents = 100, currency = "usd"))
        assertIs<ApiResult.Success<*>>(result)
        val data = (result as ApiResult.Success).data
        assertEquals("pi_test_123", data.id)
        assertEquals("pi_test_123_secret_abc", data.clientSecret)
        val request = server.takeRequest()
        assertEquals("/payment_intents", request.path)
    }

    private fun readFixture(path: String): String =
        requireNotNull(javaClass.classLoader?.getResourceAsStream(path)).bufferedReader().use { it.readText() }
}
