package com.noshitechinc.restaurant.feature.pairing

import app.cash.turbine.test
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class PairingViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `digits fill the code and the sixth opens sign in`() = runTest {
        val vm = PairingViewModel()
        vm.effects.test {
            repeat(5) { vm.onDigit(it + 1) }
            assertEquals("12345", vm.uiState.value.code)
            expectNoEvents()
            vm.onDigit(6)
            assertEquals(OpenSignIn, awaitItem())
        }
        assertEquals("123456", vm.uiState.value.code)
        assertFalse(vm.uiState.value.hasError)
    }

    @Test
    fun `delete removes the last digit`() {
        val vm = PairingViewModel()
        vm.onDigit(4)
        vm.onDigit(8)
        vm.onDelete()
        assertEquals("4", vm.uiState.value.code)
    }
}
