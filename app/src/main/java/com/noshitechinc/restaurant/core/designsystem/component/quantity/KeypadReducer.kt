package com.noshitechinc.restaurant.core.designsystem.component.quantity

enum class KeypadMode { Integer, Decimal, Currency, Pin }

sealed interface KeypadKey {
    data class Digit(val value: Int) : KeypadKey {
        init {
            require(value in 0..9) { "digit out of range: $value" }
        }
    }

    data object DoubleZero : KeypadKey
    data object Decimal : KeypadKey
    data object Backspace : KeypadKey
    data object Clear : KeypadKey
}

object KeypadReducer {
    const val DEFAULT_MAX_LENGTH = 9
    private const val DECIMAL_SEPARATOR = '.'
    private const val MAX_FRACTION_DIGITS = 2

    fun reduce(current: String, key: KeypadKey, mode: KeypadMode, maxLength: Int = DEFAULT_MAX_LENGTH): String = when (key) {
        KeypadKey.Clear -> ""

        KeypadKey.Backspace -> current.dropLast(1)

        is KeypadKey.Digit -> appendDigits(current, key.value.toString(), mode, maxLength)

        KeypadKey.DoubleZero -> if (mode == KeypadMode.Currency) appendDigits(current, "00", mode, maxLength) else current

        KeypadKey.Decimal -> when {
            mode != KeypadMode.Decimal -> current
            DECIMAL_SEPARATOR in current -> current
            current.isEmpty() -> "0$DECIMAL_SEPARATOR"
            else -> current + DECIMAL_SEPARATOR
        }
    }

    private fun appendDigits(current: String, digits: String, mode: KeypadMode, maxLength: Int): String {
        if (mode == KeypadMode.Decimal && DECIMAL_SEPARATOR in current) {
            val fraction = current.substringAfter(DECIMAL_SEPARATOR)
            val room = MAX_FRACTION_DIGITS - fraction.length
            return if (room <= 0) current else current + digits.take(room)
        }
        val combined = (current + digits).let { if (mode == KeypadMode.Pin) it else it.trimStart('0') }
        return combined.take(maxLength)
    }
}
