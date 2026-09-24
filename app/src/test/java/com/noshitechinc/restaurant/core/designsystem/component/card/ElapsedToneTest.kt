package com.noshitechinc.restaurant.core.designsystem.component.card

import kotlin.test.Test
import kotlin.test.assertEquals

class ElapsedToneTest {
    @Test
    fun `tone changes at thresholds`() {
        assertEquals(ElapsedTone.OnTime, ElapsedTone.of(9, warningAfterMinutes = 10, overdueAfterMinutes = 20))
        assertEquals(ElapsedTone.Warning, ElapsedTone.of(10, warningAfterMinutes = 10, overdueAfterMinutes = 20))
        assertEquals(ElapsedTone.Warning, ElapsedTone.of(19, warningAfterMinutes = 10, overdueAfterMinutes = 20))
        assertEquals(ElapsedTone.Overdue, ElapsedTone.of(20, warningAfterMinutes = 10, overdueAfterMinutes = 20))
    }
}
