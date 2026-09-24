package com.noshitechinc.restaurant.core.network

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.network.error.ErrorMapper
import com.noshitechinc.restaurant.core.network.error.JsonErrorBodyParser
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json

class SafeApiCallTest {
    private val safeApiCall = SafeApiCall(ErrorMapper(JsonErrorBodyParser(Json)))

    @Test
    fun `wraps success`() = runTest {
        assertEquals(ApiResult.Success(42), safeApiCall.invoke { 42 })
    }

    @Test
    fun `maps failures`() = runTest {
        val result: ApiResult<Int> = safeApiCall.invoke { throw UnknownHostException() }
        assertEquals(ApiResult.Failure(AppError.NoInternet), result)
    }

    @Test
    fun `rethrows cancellation`() = runTest {
        assertFailsWith<CancellationException> {
            safeApiCall.invoke<Int> { throw CancellationException("cancelled") }
        }
    }
}
