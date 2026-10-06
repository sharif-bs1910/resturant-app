package com.noshitechinc.restaurant.feature.signin

import app.cash.turbine.test
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class SignInViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `the fourth digit opens checkout`() = runTest {
        val vm = SignInViewModel()
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
}
