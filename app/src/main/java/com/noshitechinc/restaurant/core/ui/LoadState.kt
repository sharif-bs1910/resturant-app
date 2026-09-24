package com.noshitechinc.restaurant.core.ui

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.AppError

sealed interface LoadState<out T> {
    data object Idle : LoadState<Nothing>
    data object Loading : LoadState<Nothing>
    data class Content<out T>(val data: T) : LoadState<T>
    data object Empty : LoadState<Nothing>
    data class Error(val error: AppError) : LoadState<Nothing>
}

fun <T> ApiResult<T>.toLoadState(isEmpty: (T) -> Boolean = { (it as? Collection<*>)?.isEmpty() == true }): LoadState<T> = when (this) {
    is ApiResult.Success -> if (isEmpty(data)) LoadState.Empty else LoadState.Content(data)
    is ApiResult.Failure -> LoadState.Error(error)
}
