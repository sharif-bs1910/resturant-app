package com.noshitechinc.restaurant.feature.role

import app.cash.turbine.test
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class SelectRoleViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `continue does nothing until a role is selected`() = runTest {
        val vm = SelectRoleViewModel()
        vm.effects.test {
            vm.onContinue()
            expectNoEvents()
            assertNull(vm.uiState.value.selected)
        }
    }

    @Test
    fun `selecting a role enables continue`() = runTest {
        val vm = SelectRoleViewModel()
        vm.onSelect(DeviceRole.SelfService)
        assertEquals(DeviceRole.SelfService, vm.uiState.value.selected)
        vm.effects.test {
            vm.onContinue()
            assertEquals(OpenHome, awaitItem())
        }
    }
}
