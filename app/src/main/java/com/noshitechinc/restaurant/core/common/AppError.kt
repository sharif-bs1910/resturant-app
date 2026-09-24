package com.noshitechinc.restaurant.core.common

sealed interface AppError {
    data object NoInternet : AppError
    data object Timeout : AppError
    data object ServiceUnavailable : AppError
    data object SessionExpired : AppError
    data object Forbidden : AppError
    data object NotFound : AppError
    data class Validation(val message: String?, val fieldErrors: Map<String, List<String>> = emptyMap()) : AppError
    data class Server(val code: Int, val message: String?) : AppError
    data class Unknown(val cause: Throwable? = null) : AppError
}
