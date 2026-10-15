package com.noshitechinc.restaurant.feature.home

import com.noshitechinc.restaurant.core.common.AppEnvironment
import com.noshitechinc.restaurant.core.common.AppInfo
import com.noshitechinc.restaurant.core.terminal.TerminalConfig
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Rule

class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val terminalOff = TerminalConfig(
        enabled = false,
        locationId = "",
        stubBaseUrl = "",
        useSimulatedReader = false,
    )

    @Test
    fun `non production builds expose the environment`() {
        val vm = HomeViewModel(AppInfo(AppEnvironment.Staging, "1.0.0", 3), terminalOff)
        assertEquals(HomeUiState(environment = AppEnvironment.Staging, versionName = "1.0.0"), vm.uiState.value)
    }

    @Test
    fun `production hides the environment`() {
        val vm = HomeViewModel(AppInfo(AppEnvironment.Prod, "1.0.0", 3), terminalOff)
        assertNull(vm.uiState.value.environment)
    }
}
