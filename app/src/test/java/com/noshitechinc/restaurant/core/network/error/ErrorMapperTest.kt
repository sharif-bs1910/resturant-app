package com.noshitechinc.restaurant.core.network.error

import com.noshitechinc.restaurant.core.common.AppError
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class ErrorMapperTest {
    private val mapper = ErrorMapper(JsonErrorBodyParser(Json { ignoreUnknownKeys = true }))

    private fun http(code: Int, body: String = "") = HttpException(Response.error<Any>(code, body.toResponseBody()))

    @Test
    fun `maps connectivity exceptions`() {
        assertEquals(AppError.Timeout, mapper.map(SocketTimeoutException()))
        assertEquals(AppError.NoInternet, mapper.map(UnknownHostException()))
        assertEquals(AppError.NoInternet, mapper.map(IOException("reset")))
    }

    @Test
    fun `maps http status codes`() {
        assertEquals(AppError.SessionExpired, mapper.map(http(401)))
        assertEquals(AppError.Forbidden, mapper.map(http(403)))
        assertEquals(AppError.NotFound, mapper.map(http(404)))
        assertEquals(AppError.ServiceUnavailable, mapper.map(http(502)))
        assertEquals(AppError.ServiceUnavailable, mapper.map(http(503)))
        assertEquals(AppError.ServiceUnavailable, mapper.map(http(504)))
        assertEquals(AppError.Server(500, null), mapper.map(http(500)))
    }

    @Test
    fun `validation errors carry sanitized message and field errors`() {
        val body =
            """{"message":"Quantity must be at least 1","code":"VALIDATION","errors":{"quantity":["Must be at least 1"]}}"""
        val error = mapper.map(http(422, body))
        assertEquals(
            AppError.Validation("Quantity must be at least 1", mapOf("quantity" to listOf("Must be at least 1"))),
            error,
        )
    }

    @Test
    fun `technical server messages are dropped`() {
        val error = mapper.map(http(500, """{"message":"java.lang.IllegalStateException: boom"}"""))
        assertEquals(AppError.Server(500, null), error)
    }

    @Test
    fun `unparseable bodies do not crash`() {
        assertEquals(AppError.Validation(null, emptyMap()), mapper.map(http(400, "not json")))
    }

    @Test
    fun `unexpected exceptions map to unknown`() {
        assertIs<AppError.Unknown>(mapper.map(IllegalStateException("bug")))
    }
}
