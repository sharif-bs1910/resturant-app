package com.noshitechinc.restaurant.core.designsystem.theme

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StatusToneTest {
    @Test
    fun `every tone has distinct container and content colors`() {
        StatusTone.entries.forEach { tone ->
            val (container, content) = LightAppColors.toneColors(tone)
            assertNotEquals(container, content, "tone $tone")
        }
    }

    @Test
    fun `danger maps to destructive palette`() {
        assertEquals(
            LightAppColors.destructiveContainer to LightAppColors.onDestructiveContainer,
            LightAppColors.toneColors(StatusTone.Danger),
        )
    }
}
