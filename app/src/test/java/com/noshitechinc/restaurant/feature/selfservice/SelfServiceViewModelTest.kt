package com.noshitechinc.restaurant.feature.selfservice

import com.noshitechinc.restaurant.feature.checkout.MenuCatalog
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class SelfServiceViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `place order moves to guest when the cart has lines`() = runTest {
        val vm = SelfServiceViewModel()
        vm.onOpenItem(MenuCatalog.first())
        vm.onAddToOrder()
        vm.onPlaceOrder()
        assertEquals(SelfServiceStep.Guest, vm.uiState.value.step)
    }

    @Test
    fun `guest continue requires phone name and table`() = runTest {
        val vm = SelfServiceViewModel()
        vm.onOpenItem(MenuCatalog.first())
        vm.onAddToOrder()
        vm.onPlaceOrder()
        vm.onGuestContinue()
        assertEquals(SelfServiceStep.Guest, vm.uiState.value.step)
        vm.onGuestPhone("4155550192")
        vm.onGuestName("Maya")
        vm.onTableNumber("12")
        vm.onGuestContinue()
        assertEquals(SelfServiceStep.Review, vm.uiState.value.step)
    }

    @Test
    fun `make payment reaches success after wait and process`() = runTest {
        val vm = SelfServiceViewModel()
        vm.onOpenItem(MenuCatalog.first())
        vm.onAddToOrder()
        vm.onPlaceOrder()
        vm.onGuestPhone("4155550192")
        vm.onGuestName("Maya")
        vm.onTableNumber("12")
        vm.onGuestContinue()
        vm.onMakePayment()
        assertEquals(SelfServiceStep.PaymentWaiting, vm.uiState.value.step)
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(1_601)
        assertEquals(SelfServiceStep.PaymentProcessing, vm.uiState.value.step)
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(1_401)
        assertEquals(SelfServiceStep.Success, vm.uiState.value.step)
        assertTrue(vm.uiState.value.itemCount >= 1)
    }

    @Test
    fun `simulate decline lands on declined then try again recovers`() = runTest {
        val vm = SelfServiceViewModel()
        vm.onOpenItem(MenuCatalog.first())
        vm.onAddToOrder()
        vm.onPlaceOrder()
        vm.onGuestPhone("4155550192")
        vm.onGuestName("Maya")
        vm.onTableNumber("12")
        vm.onGuestContinue()
        vm.onSimulateDecline()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(3_100)
        assertEquals(SelfServiceStep.PaymentDeclined, vm.uiState.value.step)
        vm.onTryPaymentAgain()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(3_100)
        assertEquals(SelfServiceStep.Success, vm.uiState.value.step)
    }

    @Test
    fun `new order resets the flow`() = runTest {
        val vm = SelfServiceViewModel()
        vm.onOpenItem(MenuCatalog.first())
        vm.onAddToOrder()
        vm.onPlaceOrder()
        vm.onGuestPhone("4155550192")
        vm.onGuestName("Maya")
        vm.onTableNumber("12")
        vm.onGuestContinue()
        vm.onMakePayment()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(3_100)
        vm.onNewOrder()
        assertEquals(SelfServiceStep.Menu, vm.uiState.value.step)
        assertTrue(vm.uiState.value.lines.isEmpty())
    }
}
