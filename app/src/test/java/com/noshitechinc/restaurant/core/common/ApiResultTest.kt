package com.noshitechinc.restaurant.core.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApiResultTest {
    @Test
    fun `map transforms success data`() {
        val result: ApiResult<Int> = ApiResult.Success(2)
        assertEquals(ApiResult.Success(4), result.map { it * 2 })
    }

    @Test
    fun `map keeps failure untouched`() {
        val result: ApiResult<Int> = ApiResult.Failure(AppError.NotFound)
        assertEquals(ApiResult.Failure(AppError.NotFound), result.map { it * 2 })
    }

    @Test
    fun `onFailure runs only for failures`() {
        var seen: AppError? = null
        ApiResult.Success(1).onFailure { seen = it }
        assertEquals(null, seen)
        ApiResult.Failure(AppError.Timeout).onFailure { seen = it }
        assertTrue(seen is AppError.Timeout)
    }
}
