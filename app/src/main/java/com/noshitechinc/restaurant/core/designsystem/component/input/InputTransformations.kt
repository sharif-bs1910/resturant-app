package com.noshitechinc.restaurant.core.designsystem.component.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.noshitechinc.restaurant.core.common.format.CurrencyFormatter
import com.noshitechinc.restaurant.core.common.format.UsPhoneFormatter

class CurrencyVisualTransformation(private val formatter: CurrencyFormatter) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val formatted = if (original.isEmpty()) "" else formatter.format(CurrencyFormatter.centsFromDigits(original))
        return TransformedText(
            AnnotatedString(formatted),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int = formatted.length
                override fun transformedToOriginal(offset: Int): Int = original.length
            },
        )
    }
}

object UsPhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = UsPhoneFormatter.digitsOnly(text.text)
        return TransformedText(
            AnnotatedString(UsPhoneFormatter.format(digits)),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int = UsPhoneFormatter.originalToTransformed(offset, digits.length)

                override fun transformedToOriginal(offset: Int): Int = UsPhoneFormatter.transformedToOriginal(offset, digits.length)
            },
        )
    }
}
