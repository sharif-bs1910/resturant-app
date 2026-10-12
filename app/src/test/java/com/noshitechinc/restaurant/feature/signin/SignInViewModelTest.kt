package com.noshitechinc.restaurant.feature.signin

import app.cash.turbine.test
import com.noshitechinc.restaurant.feature.role.DeviceRole
import com.noshitechinc.restaurant.feature.role.DeviceRoleStore
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class SignInViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `the fourth digit opens checkout by default`() = runTest {
        val vm = SignInViewModel(DeviceRoleStore())
        vm.effects.test {
            vm.onDigit(1)
            vm.onDigit(2)
            vm.onDigit(3)
            expectNoEvents()
            vm.onDigit(4)
            assertEquals(OpenCheckout, awaitItem())
        }
        assertEquals("1234", vm.uiState.value.pin)
    }

    @Test
    fun `the fourth digit opens self service when that role is selected`() = runTest {
        val store = DeviceRoleStore().also { it.setRole(DeviceRole.SelfService) }
        val vm = SignInViewModel(store)
        vm.effects.test {
            vm.onDigit(1)
            vm.onDigit(2)
            vm.onDigit(3)
            vm.onDigit(4)
            assertEquals(OpenSelfService, awaitItem())
        }
    }
}
