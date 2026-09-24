package com.noshitechinc.restaurant.core.common.format

import kotlin.test.Test
import kotlin.test.assertEquals

class UsPhoneFormatterTest {
    @Test
    fun `keeps at most ten digits`() {
        assertEquals("5551234567", UsPhoneFormatter.digitsOnly("(555) 123-4567 ext 89"))
    }

    @Test
    fun `formats progressively while typing`() {
        assertEquals("", UsPhoneFormatter.format(""))
        assertEquals("(55", UsPhoneFormatter.format("55"))
        assertEquals("(555) 12", UsPhoneFormatter.format("55512"))
        assertEquals("(555) 123-4567", UsPhoneFormatter.format("5551234567"))
    }

    @Test
    fun `offset mapping round trips at every digit boundary`() {
        val digits = "5551234567"
        for (count in 0..digits.length) {
            val formatted = UsPhoneFormatter.format(digits.take(count))
            for (offset in 0..count) {
                val transformed = UsPhoneFormatter.originalToTransformed(offset, count)
                assert(transformed in 0..formatted.length) { "offset $offset/$count -> $transformed" }
                assertEquals(offset, UsPhoneFormatter.transformedToOriginal(transformed, count))
            }
        }
    }
}
