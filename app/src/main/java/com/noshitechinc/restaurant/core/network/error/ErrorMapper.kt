package com.noshitechinc.restaurant.core.network.error

import com.noshitechinc.restaurant.core.common.AppError
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import retrofit2.HttpException

class ErrorMapper @Inject constructor(private val parser: ErrorBodyParser) {
    fun map(throwable: Throwable): AppError = when (throwable) {
        is HttpException -> fromHttp(throwable.code(), throwable.response()?.errorBody()?.string())
        is SocketTimeoutException -> AppError.Timeout
        is IOException -> AppError.NoInternet
        else -> AppError.Unknown(throwable)
    }

    fun fromHttp(code: Int, body: String?): AppError {
        val parsed = body?.takeIf { it.isNotBlank() }?.let(parser::parse)
        val message = MessageSanitizer.sanitize(parsed?.message)
        return when (code) {
            HTTP_UNAUTHORIZED -> AppError.SessionExpired
            HTTP_FORBIDDEN -> AppError.Forbidden
            HTTP_NOT_FOUND -> AppError.NotFound
            in SERVICE_UNAVAILABLE -> AppError.ServiceUnavailable
            in CLIENT_ERRORS -> AppError.Validation(message, parsed?.fieldErrors.orEmpty())
            else -> AppError.Server(code, message)
        }
    }

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
        const val HTTP_FORBIDDEN = 403
        const val HTTP_NOT_FOUND = 404
        val SERVICE_UNAVAILABLE = setOf(502, 503, 504)
        val CLIENT_ERRORS = 400..499
    }
}
