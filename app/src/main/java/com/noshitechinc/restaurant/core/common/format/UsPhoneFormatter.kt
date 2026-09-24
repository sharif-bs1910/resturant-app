package com.noshitechinc.restaurant.core.common.format

object UsPhoneFormatter {
    const val NATIONAL_DIGITS = 10
    private const val AREA_END = 3
    private const val EXCHANGE_END = 6

    fun digitsOnly(input: String): String = input.filter(Char::isDigit).take(NATIONAL_DIGITS)

    fun format(digits: String): String {
        val d = digitsOnly(digits)
        return when {
            d.isEmpty() -> ""
            d.length <= AREA_END -> "($d"
            d.length <= EXCHANGE_END -> "(${d.take(AREA_END)}) ${d.drop(AREA_END)}"
            else -> "(${d.take(AREA_END)}) ${d.substring(AREA_END, EXCHANGE_END)}-${d.drop(EXCHANGE_END)}"
        }
    }

    fun originalToTransformed(offset: Int, digitCount: Int): Int {
        if (digitCount == 0) return 0
        val o = offset.coerceIn(0, digitCount)
        val transformed = when {
            o <= AREA_END -> o + 1
            o <= EXCHANGE_END -> o + 3
            else -> o + 4
        }
        return transformed.coerceAtMost(format("0".repeat(digitCount)).length)
    }

    fun transformedToOriginal(offset: Int, digitCount: Int): Int {
        val original = when {
            offset <= 1 -> 0
            offset <= 4 -> offset - 1
            offset <= 6 -> AREA_END
            offset <= 9 -> offset - 3
            offset == 10 -> EXCHANGE_END
            else -> offset - 4
        }
        return original.coerceIn(0, digitCount)
    }
}
