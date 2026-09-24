package com.noshitechinc.restaurant.core.common.format

import com.noshitechinc.restaurant.core.common.market.MarketConfig
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency

class CurrencyFormatter(market: MarketConfig = MarketConfig.UnitedStates) {
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(market.locale).apply {
        currency = Currency.getInstance(market.currencyCode)
    }
    private val amountFormat: NumberFormat = NumberFormat.getNumberInstance(market.locale).apply {
        minimumFractionDigits = FRACTION_DIGITS
        maximumFractionDigits = FRACTION_DIGITS
    }

    fun format(cents: Long): String = synchronized(currencyFormat) { currencyFormat.format(toDecimal(cents)) }

    fun formatAmount(cents: Long): String = synchronized(amountFormat) { amountFormat.format(toDecimal(cents)) }

    private fun toDecimal(cents: Long): BigDecimal = BigDecimal.valueOf(cents, FRACTION_DIGITS)

    companion object {
        const val MAX_DIGITS = 11
        private const val FRACTION_DIGITS = 2

        fun centsFromDigits(input: String): Long = input.filter(Char::isDigit).trimStart('0').take(MAX_DIGITS).ifEmpty { "0" }.toLong()
    }
}
