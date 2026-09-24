package com.noshitechinc.restaurant.core.designsystem.component.input

import androidx.compose.ui.text.AnnotatedString
import com.noshitechinc.restaurant.core.common.format.CurrencyFormatter
import kotlin.test.Test
import kotlin.test.assertEquals

class InputTransformationsTest {
    @Test
    fun `currency transformation shows formatted amount with cursor at end`() {
        val transformed = CurrencyVisualTransformation(CurrencyFormatter()).filter(AnnotatedString("123456"))
        assertEquals("$1,234.56", transformed.text.text)
        assertEquals(transformed.text.length, transformed.offsetMapping.originalToTransformed(3))
        assertEquals(6, transformed.offsetMapping.transformedToOriginal(2))
    }

    @Test
    fun `phone transformation formats digits and maps offsets`() {
        val transformed = UsPhoneVisualTransformation.filter(AnnotatedString("5551234567"))
        assertEquals("(555) 123-4567", transformed.text.text)
        assertEquals(14, transformed.offsetMapping.originalToTransformed(10))
        assertEquals(3, transformed.offsetMapping.transformedToOriginal(5))
    }
}
