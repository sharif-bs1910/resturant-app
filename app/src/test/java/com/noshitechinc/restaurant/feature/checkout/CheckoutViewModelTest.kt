package com.noshitechinc.restaurant.feature.checkout

import com.noshitechinc.restaurant.core.designsystem.component.quantity.KeypadKey
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class CheckoutViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `new order opens the builder with the sample cart`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        val state = vm.uiState.value
        assertEquals(CheckoutStep.Building, state.step)
        assertEquals(4_100, state.subtotalCents)
        assertEquals(359, state.taxCents)
        assertEquals(4_459, state.totalCents)
    }

    @Test
    fun `pay now opens tender and a gift card leaves the remainder due`() = runTest {
        val vm = CheckoutViewModel()
        vm.onPay()
        assertEquals(CheckoutOverlay.None, vm.uiState.value.overlay)
        vm.onNewOrder()
        vm.onPay()
        assertEquals(CheckoutOverlay.Tender, vm.uiState.value.overlay)
        vm.onRedeemGiftCard()
        vm.onApplyGiftCard()
        val state = vm.uiState.value
        assertEquals(CheckoutOverlay.Tender, state.overlay)
        assertEquals(2_500, state.giftAppliedCents)
        assertEquals(1_959, state.dueCents)
    }

    @Test
    fun `sending the payment link shows the sent dialog then confirms`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onPay()
        vm.onSendPaymentLink()
        assertEquals(CheckoutOverlay.Saved, vm.uiState.value.overlay)
        vm.onClosePaymentSent()
        assertEquals(CheckoutStep.Confirmed, vm.uiState.value.step)
        assertTrue(vm.uiState.value.overlay == CheckoutOverlay.None)
    }

    @Test
    fun `phone channel uses the phone order cart`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onChannel(OrderChannel.Phone)
        assertEquals(3_600, vm.uiState.value.subtotalCents)
        assertEquals(3_915, vm.uiState.value.totalCents)
    }

    @Test
    fun `open orders opens the queue`() = runTest {
        val vm = CheckoutViewModel()
        vm.onOpenOrders()
        assertEquals(CheckoutStep.Queue, vm.uiState.value.step)
        assertEquals("1042", vm.uiState.value.selectedOrder?.number)
    }

    @Test
    fun `save draft opens the draft dialog on the current cart`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onChannel(OrderChannel.Phone)
        vm.onSaveDraft()
        val state = vm.uiState.value
        assertEquals(CheckoutStep.Building, state.step)
        assertEquals(CheckoutOverlay.Draft, state.overlay)
        assertEquals("0192", phoneTail(state.customerPhone))
    }

    @Test
    fun `delivery address saves the street and keep as pickup leaves it pickup`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onFulfillment(Fulfillment.Delivery)
        vm.onEditAddress()
        assertEquals(CheckoutOverlay.Address, vm.uiState.value.overlay)
        vm.onKeepAsPickup()
        assertEquals(Fulfillment.Pickup, vm.uiState.value.fulfillment)
        assertEquals(CheckoutOverlay.None, vm.uiState.value.overlay)
        vm.onFulfillment(Fulfillment.Delivery)
        vm.onEditAddress()
        vm.onStreet("1847 Telegraph Ave")
        vm.onSaveAddress()
        val state = vm.uiState.value
        assertEquals(Fulfillment.Delivery, state.fulfillment)
        assertEquals("1847 Telegraph Ave, Apt 3B", state.addressLine)
        assertEquals(CheckoutOverlay.None, state.overlay)
    }

    @Test
    fun `orders board expands modifiers and can be marked complete`() = runTest {
        val vm = CheckoutViewModel()
        vm.onSection(KitchenSection.Orders)
        assertEquals("1042", vm.uiState.value.selectedBoard?.number)
        assertEquals("Maya R.", vm.uiState.value.selectedBoard?.detailGuest)
        assertTrue("spicy" in vm.uiState.value.selectedBoard!!.expanded)
        vm.onToggleBoardLine("gyoza")
        assertTrue("gyoza" in vm.uiState.value.selectedBoard!!.expanded)
        vm.onMarkBoardComplete()
        assertEquals(OpenOrderStatus.Completed, vm.uiState.value.selectedBoard?.status)
        vm.onBoardFilter(BoardFilter.Completed)
        assertEquals(3, vm.uiState.value.visibleBoard.size)
        vm.onBoardFilter(BoardFilter.Due)
        assertEquals(listOf("1043"), vm.uiState.value.visibleBoard.map { it.number })
    }

    @Test
    fun `orders board opens charge refund address and cancel`() = runTest {
        val vm = CheckoutViewModel()
        vm.onSection(KitchenSection.Orders)
        vm.onAddCharge()
        assertEquals(BoardDialog.Charge, vm.uiState.value.boardDialog)
        vm.onConfirmCharge()
        assertEquals(4_100, vm.uiState.value.selectedBoard?.subtotalCents)
        vm.onRefund()
        assertEquals(BoardDialog.Refund, vm.uiState.value.boardDialog)
        vm.onRefundAmount("5.00")
        vm.onConfirmRefund()
        assertEquals(3_600, vm.uiState.value.selectedBoard?.subtotalCents)
        vm.onToggleBoardFulfillment()
        assertEquals(BoardDialog.Address, vm.uiState.value.boardDialog)
        assertEquals(Fulfillment.Pickup, vm.uiState.value.selectedBoard?.fulfillment)
        vm.onSaveBoardAddress()
        assertEquals(Fulfillment.Delivery, vm.uiState.value.selectedBoard?.fulfillment)
        assertEquals("1847 Telegraph Ave, Apt 3B", vm.uiState.value.selectedBoard?.addressLine)
        vm.onToggleBoardFulfillment()
        assertEquals(Fulfillment.Pickup, vm.uiState.value.selectedBoard?.fulfillment)
        vm.onToggleBoardFulfillment()
        assertEquals(Fulfillment.Delivery, vm.uiState.value.selectedBoard?.fulfillment)
        assertEquals(BoardDialog.None, vm.uiState.value.boardDialog)
        vm.onCancelOrder()
        vm.onConfirmCancel()
        assertEquals("1042", vm.uiState.value.canceledOrder?.number)
        assertEquals(null, vm.uiState.value.boardOrders.firstOrNull { it.number == "1042" })
    }

    @Test
    fun `spicy tonkotsu modifiers price the add to order line`() = runTest {
        val item = MenuCatalog.first { it.id == "spicy-tonkotsu" }
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onOpenItem(item)
        val draft = vm.uiState.value.draft
        assertEquals(2_000, draft?.let { unitPrice(it.item, it.selected) })
        vm.onAddToOrder()
        assertEquals(CheckoutOverlay.None, vm.uiState.value.overlay)
        assertEquals(4, vm.uiState.value.lines.size)
    }

    @Test
    fun `menu search filters ramen and marking unavailable hides the item`() = runTest {
        val vm = CheckoutViewModel()
        vm.onSection(KitchenSection.Menu)
        vm.onMenuSearch("ramen")
        assertEquals(3, vm.uiState.value.visibleKitchenMenu.size)
        vm.onMenuAvailability("edamame", available = false)
        assertEquals("edamame", vm.uiState.value.menuPromptId)
        vm.onUnavailableUntil(UnavailableUntil.Week)
        vm.onConfirmUnavailable()
        assertEquals(false, vm.uiState.value.menuItems.first { it.id == "edamame" }.available)
        assertEquals(UnavailableUntil.Week, vm.uiState.value.menuItems.first { it.id == "edamame" }.unavailableUntil)
        vm.onMenuFilter(MenuListFilter.EightySixed)
        vm.onMenuSearch("")
        assertEquals(listOf("edamame"), vm.uiState.value.visibleKitchenMenu.map { it.id })
        vm.onStopTracking()
        vm.onSaveMenuItem()
        assertEquals(null, vm.uiState.value.menuItems.first { it.id == "spicy-tonkotsu" }.stock)
    }

    @Test
    fun `settings keeps zones pins and hours on the device`() = runTest {
        val vm = CheckoutViewModel()
        vm.onSection(KitchenSection.Settings)
        val start = vm.uiState.value.settings
        vm.onSettingsChange(start.withAddedZone())
        assertEquals(3, vm.uiState.value.settings.zones.size)
        assertEquals("Zone C", vm.uiState.value.settings.zones.last().name)
        vm.onSettingsChange(vm.uiState.value.settings.copy(dialog = SettingsDialog.RemoveZone, pendingZoneId = "zone-b"))
        vm.onSettingsChange(vm.uiState.value.settings.withoutPendingZone())
        assertEquals(listOf("zone-a", "zone-C"), vm.uiState.value.settings.zones.map { it.id })
        vm.onSettingsChange(vm.uiState.value.settings.afterPinKey(KeypadKey.Digit(2)))
        vm.onSettingsChange(vm.uiState.value.settings.afterPinKey(KeypadKey.Digit(2)))
        vm.onSettingsChange(vm.uiState.value.settings.afterPinKey(KeypadKey.Digit(0)))
        vm.onSettingsChange(vm.uiState.value.settings.afterPinKey(KeypadKey.Digit(5)))
        assertEquals(SettingsDialog.ClockedOut, vm.uiState.value.settings.dialog)
        assertEquals(false, vm.uiState.value.settings.staff.first { it.id == "maya" }.onShift)
        vm.onSettingsChange(
            vm.uiState.value.settings.copy(
                storeDraft = vm.uiState.value.settings.storeDraft.map {
                    it.copy(opens = "10:00 AM")
                },
            ),
        )
        vm.onSettingsChange(vm.uiState.value.settings.copy(storeDays = vm.uiState.value.settings.storeDraft, dialog = SettingsDialog.None))
        assertEquals("10:00 AM", vm.uiState.value.settings.storeDays.first().opens)
        vm.onSection(KitchenSection.Checkout)
        assertEquals(SettingsDialog.None, vm.uiState.value.settings.dialog)
        assertEquals(SettingsDetail.None, vm.uiState.value.settings.detail)
    }

    @Test
    fun `kitchen status switches between normal and busy`() = runTest {
        val vm = CheckoutViewModel()
        vm.onSection(KitchenSection.Status)
        vm.onKitchenMode(true)
        assertEquals(KitchenSection.Status, vm.uiState.value.section)
        assertEquals(true, vm.uiState.value.kitchenBusy)
        vm.onKitchenMode(false)
        assertEquals(false, vm.uiState.value.kitchenBusy)
    }
}
