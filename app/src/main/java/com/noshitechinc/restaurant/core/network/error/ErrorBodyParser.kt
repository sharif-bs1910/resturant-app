package com.noshitechinc.restaurant.core.network.error

import javax.inject.Inject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

data class ParsedError(val message: String?, val code: String?, val fieldErrors: Map<String, List<String>>)

fun interface ErrorBodyParser {
    fun parse(body: String): ParsedError?
}

class JsonErrorBodyParser @Inject constructor(private val json: Json) : ErrorBodyParser {
    override fun parse(body: String): ParsedError? = try {
        json.decodeFromString<ErrorBodyDto>(body).let {
            ParsedError(it.message, it.code, it.errors.orEmpty())
        }
    } catch (_: IllegalArgumentException) {
        null
    }
}

@Serializable
internal data class ErrorBodyDto(val message: String? = null, val code: String? = null, val errors: Map<String, List<String>>? = null)
