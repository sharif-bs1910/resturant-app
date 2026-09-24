package com.noshitechinc.restaurant.core.common.validation

import com.noshitechinc.restaurant.core.common.format.UsPhoneFormatter

object UsPhoneValidator {
    private const val EXCHANGE_START = 3

    fun isValid(digits: String): Boolean = digits.length == UsPhoneFormatter.NATIONAL_DIGITS &&
        digits.all(Char::isDigit) &&
        digits[0] in '2'..'9' &&
        digits[EXCHANGE_START] in '2'..'9'
}
