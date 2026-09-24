package com.noshitechinc.restaurant.core.network

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.network.error.ErrorMapper
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import timber.log.Timber

class SafeApiCall @Inject constructor(private val errorMapper: ErrorMapper) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun <T> invoke(block: suspend () -> T): ApiResult<T> = try {
        ApiResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Timber.w(e, "API call failed")
        ApiResult.Failure(errorMapper.map(e))
    }
}
