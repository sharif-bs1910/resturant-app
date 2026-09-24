package com.noshitechinc.restaurant.core.common.format

import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyFormatterTest {
    private val formatter = CurrencyFormatter()

    @Test
    fun `formats cents as US dollars`() {
        assertEquals("$1,234.56", formatter.format(123_456))
        assertEquals("$0.00", formatter.format(0))
        assertEquals("$0.05", formatter.format(5))
    }

    @Test
    fun `formats amount without currency symbol`() {
        assertEquals("1,234.56", formatter.formatAmount(123_456))
    }

    @Test
    fun `parses typed digits into cents`() {
        assertEquals(123L, CurrencyFormatter.centsFromDigits("1a2b3"))
        assertEquals(0L, CurrencyFormatter.centsFromDigits("000"))
        assertEquals(0L, CurrencyFormatter.centsFromDigits(""))
    }

    @Test
    fun `caps digits to avoid overflow`() {
        assertEquals(99_999_999_999L, CurrencyFormatter.centsFromDigits("9".repeat(30)))
    }
}
