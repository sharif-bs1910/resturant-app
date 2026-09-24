package com.noshitechinc.restaurant.core.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.ui.state.StateTags
import org.junit.Rule
import org.junit.Test

class OfflineBlockingTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun blockingStateFollowsConnectivity() {
        var online by mutableStateOf(false)
        rule.setContent {
            AppTheme {
                CompositionLocalProvider(LocalIsOnline provides online) {
                    AppScaffold(requiresNetwork = true) { Text("content") }
                }
            }
        }
        rule.onNodeWithTag(StateTags.OFFLINE).assertIsDisplayed()
        online = true
        rule.waitForIdle()
        rule.onNodeWithTag(StateTags.OFFLINE).assertDoesNotExist()
    }
}
