package com.noshitechinc.restaurant.core.designsystem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.noshitechinc.restaurant.core.designsystem.component.quantity.QuantityStepper
import com.noshitechinc.restaurant.core.designsystem.component.quantity.QuantityStepperTags
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import org.junit.Rule
import org.junit.Test

class QuantityStepperTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun stepperRespectsMinAndMax() {
        rule.setContent {
            AppTheme {
                var quantity by remember { mutableIntStateOf(1) }
                QuantityStepper(quantity = quantity, onQuantityChange = { quantity = it }, min = 1, max = 3)
            }
        }
        rule.onNodeWithTag(QuantityStepperTags.DECREMENT).assertIsNotEnabled()
        rule.onNodeWithTag(QuantityStepperTags.INCREMENT).performClick().performClick()
        rule.onNodeWithTag(QuantityStepperTags.VALUE).assertTextEquals("3")
        rule.onNodeWithTag(QuantityStepperTags.INCREMENT).assertIsNotEnabled()
        rule.onNodeWithTag(QuantityStepperTags.DECREMENT).assertIsEnabled()
    }
}
