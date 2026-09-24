package com.noshitechinc.restaurant.core.network.error

object MessageSanitizer {
    const val MAX_LENGTH = 280
    private val technical = Regex(
        "(?i)(exception|stack ?trace|sqlstate|syntax error|null ?pointer|<[a-z!/]|\\bat [\\w.$]+\\()",
    )

    fun sanitize(raw: String?): String? {
        val text = raw?.trim().orEmpty()
        return text.takeIf { it.isNotEmpty() && it.length <= MAX_LENGTH && !technical.containsMatchIn(it) }
    }
}
