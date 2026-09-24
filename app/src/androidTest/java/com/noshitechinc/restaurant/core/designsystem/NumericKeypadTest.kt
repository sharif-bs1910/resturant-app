package com.noshitechinc.restaurant.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.noshitechinc.restaurant.core.designsystem.component.quantity.KeypadMode
import com.noshitechinc.restaurant.core.designsystem.component.quantity.KeypadReducer
import com.noshitechinc.restaurant.core.designsystem.component.quantity.NumericKeypad
import com.noshitechinc.restaurant.core.designsystem.component.quantity.NumericKeypadTags
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import org.junit.Rule
import org.junit.Test

class NumericKeypadTest {
    @get:Rule
    val rule = createComposeRule()

    private fun setKeypad(mode: KeypadMode) {
        rule.setContent {
            AppTheme {
                var value by remember { mutableStateOf("") }
                Column {
                    Text(text = value, modifier = Modifier.testTag("value"))
                    NumericKeypad(onKey = { value = KeypadReducer.reduce(value, it, mode) }, mode = mode)
                }
            }
        }
    }

    @Test
    fun integerTypingAndBackspace() {
        setKeypad(KeypadMode.Integer)
        rule.onNodeWithTag(NumericKeypadTags.digit(1)).performClick()
        rule.onNodeWithTag(NumericKeypadTags.digit(2)).performClick()
        rule.onNodeWithTag(NumericKeypadTags.BACKSPACE).performClick()
        rule.onNodeWithTag(NumericKeypadTags.digit(5)).performClick()
        rule.onNodeWithTag("value").assertTextEquals("15")
    }

    @Test
    fun decimalAllowsSingleSeparator() {
        setKeypad(KeypadMode.Decimal)
        rule.onNodeWithTag(NumericKeypadTags.digit(3)).performClick()
        rule.onNodeWithTag(NumericKeypadTags.DECIMAL).performClick()
        rule.onNodeWithTag(NumericKeypadTags.DECIMAL).performClick()
        rule.onNodeWithTag(NumericKeypadTags.digit(5)).performClick()
        rule.onNodeWithTag("value").assertTextEquals("3.5")
    }

    @Test
    fun currencyDoubleZero() {
        setKeypad(KeypadMode.Currency)
        rule.onNodeWithTag(NumericKeypadTags.digit(7)).performClick()
        rule.onNodeWithTag(NumericKeypadTags.DOUBLE_ZERO).performClick()
        rule.onNodeWithTag("value").assertTextEquals("700")
    }
}
