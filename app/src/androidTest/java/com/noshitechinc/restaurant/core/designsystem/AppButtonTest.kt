package com.noshitechinc.restaurant.core.designsystem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AppButtonTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun loadingButtonIgnoresTaps() {
        var clicks = 0
        var loading by mutableStateOf(true)
        rule.setContent {
            AppTheme {
                AppButton(
                    text = "Send",
                    onClick = { clicks++ },
                    loading = loading,
                    modifier = Modifier.testTag("button"),
                )
            }
        }
        rule.onNodeWithTag("button").performClick()
        assertEquals(0, clicks)
        loading = false
        rule.onNodeWithTag("button").performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun disabledButtonIgnoresTaps() {
        var clicks = 0
        rule.setContent {
            AppTheme {
                AppButton(
                    text = "Send",
                    onClick = { clicks++ },
                    enabled = false,
                    modifier = Modifier.testTag("button"),
                )
            }
        }
        rule.onNodeWithTag("button").performClick()
        assertEquals(0, clicks)
    }
}
