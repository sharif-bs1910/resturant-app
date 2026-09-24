package com.noshitechinc.restaurant.core.ui

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.AppError
import kotlin.test.Test
import kotlin.test.assertEquals

class LoadStateTest {
    @Test
    fun `empty list becomes Empty`() {
        assertEquals(LoadState.Empty, ApiResult.Success(emptyList<String>()).toLoadState())
    }

    @Test
    fun `non empty data becomes Content`() {
        assertEquals(LoadState.Content(listOf("a")), ApiResult.Success(listOf("a")).toLoadState())
    }

    @Test
    fun `failure becomes Error`() {
        assertEquals(LoadState.Error(AppError.Timeout), ApiResult.Failure(AppError.Timeout).toLoadState())
    }

    @Test
    fun `custom emptiness predicate is used`() {
        assertEquals(LoadState.Empty, ApiResult.Success("").toLoadState { it.isEmpty() })
    }
}
