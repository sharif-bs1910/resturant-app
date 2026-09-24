package com.noshitechinc.restaurant.core.network.error

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MessageSanitizerTest {
    @Test
    fun `keeps short human messages`() {
        assertEquals(
            "Table 4 already has an open order.",
            MessageSanitizer.sanitize("  Table 4 already has an open order. "),
        )
    }

    @Test
    fun `rejects blank long and technical messages`() {
        assertNull(MessageSanitizer.sanitize(null))
        assertNull(MessageSanitizer.sanitize("   "))
        assertNull(MessageSanitizer.sanitize("x".repeat(MessageSanitizer.MAX_LENGTH + 1)))
        assertNull(MessageSanitizer.sanitize("java.lang.NullPointerException: order"))
        assertNull(MessageSanitizer.sanitize("<html><body>502</body></html>"))
        assertNull(MessageSanitizer.sanitize("SQLSTATE[23000]: Integrity constraint"))
        assertNull(MessageSanitizer.sanitize("failed at com.example.Foo.bar(Foo.kt:12)"))
    }
}
