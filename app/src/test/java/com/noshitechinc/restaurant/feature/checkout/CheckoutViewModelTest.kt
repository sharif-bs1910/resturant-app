package com.noshitechinc.restaurant.feature.checkout

import com.noshitechinc.restaurant.core.designsystem.component.quantity.KeypadKey
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `new order opens an empty phone pickup builder`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        val state = vm.uiState.value
        assertEquals(CheckoutStep.Building, state.step)
        assertEquals(OrderChannel.Phone, state.channel)
        assertEquals(Fulfillment.Pickup, state.fulfillment)
        assertEquals(0, state.subtotalCents)
        assertEquals(0, state.totalCents)
        assertTrue(state.lines.isEmpty())
    }

    @Test
    fun `pay now opens payment and a gift card leaves the remainder due`() = runTest {
        val vm = CheckoutViewModel()
        vm.onPay()
        assertEquals(CheckoutStep.Idle, vm.uiState.value.step)
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        assertEquals(CheckoutStep.Payment, vm.uiState.value.step)
        assertEquals(CheckoutOverlay.None, vm.uiState.value.overlay)
        vm.onRedeemGiftCard()
        vm.onContinueGiftCard()
        assertEquals(CheckoutOverlay.GiftAmount, vm.uiState.value.overlay)
        vm.onApplyGiftCard()
        val state = vm.uiState.value
        assertEquals(CheckoutStep.Payment, state.step)
        assertEquals(CheckoutOverlay.None, state.overlay)
        assertEquals(2_500, state.giftAppliedCents)
        assertEquals(1_415, state.dueCents)
    }

    @Test
    fun `gift enter amount can apply a partial balance`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onRedeemGiftCard()
        vm.onContinueGiftCard()
        vm.onGiftAmountDelete()
        vm.onGiftAmountDelete()
        vm.onGiftAmountDelete()
        vm.onGiftAmountDelete()
        vm.onGiftAmountDigit("1")
        vm.onGiftAmountDigit("0")
        vm.onGiftAmountDigit("0")
        vm.onGiftAmountDigit("0")
        vm.onApplyGiftCard()
        assertEquals(1_000, vm.uiState.value.giftAppliedCents)
        assertEquals(2_915, vm.uiState.value.dueCents)
    }

    @Test
    fun `split by items charges only the selected lines`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onPayAmountMode(PayAmountMode.ByItems)
        assertEquals(2, vm.uiState.value.selectedPayLineIds.size)
        vm.onSelectAllPayLines()
        vm.onTogglePayLine("line-gyoza")
        val state = vm.uiState.value
        assertEquals(700, state.selectedSubtotalCents)
        assertEquals(61, state.selectedTaxCents)
        assertEquals(761, state.chargeCents)
    }

    @Test
    fun `partial card charge fills paid progress on remaining bill`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        assertEquals(0f, vm.uiState.value.paidProgress)
        vm.onCardChargeCustom(true)
        vm.onCustomAmountDigits("2500")
        assertEquals(2_500, vm.uiState.value.chargeCents)
        vm.onCollectPayment()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(2_000)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        val state = vm.uiState.value
        assertEquals(CheckoutStep.Payment, state.step)
        assertEquals(Settlement.Card, state.settlement)
        assertEquals(2_500, state.paidCents)
        assertEquals(1_415, state.dueCents)
        assertEquals(2_500, state.coveredCents)
        assertTrue(state.paidProgress > 0.6f && state.paidProgress < 0.7f)
    }

    @Test
    fun `tip percent uses amount to pay base not full order total`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onCardChargeCustom(true)
        vm.onCustomAmountDigits("2500")
        vm.onCollectPayment()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(2_000)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.onTipOption(TipOption.Percent5)
        val state = vm.uiState.value
        assertEquals(1_415, state.tipBaseCents)
        assertEquals(70, state.tipCents)
        assertEquals(1_485, state.dueCents)
    }

    @Test
    fun `cash settlement stays selected after partial payment`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onSettlement(Settlement.Cash)
        vm.onCardChargeCustom(true)
        vm.onCustomAmountDigits("1000")
        vm.onCashTenderExact()
        vm.onCollectPayment()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(2_000)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(CheckoutStep.Payment, vm.uiState.value.step)
        assertEquals(Settlement.Cash, vm.uiState.value.settlement)
        assertEquals(1_000, vm.uiState.value.paidCents)
    }

    @Test
    fun `gift card apply fills progress without card charge`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onRedeemGiftCard()
        vm.onContinueGiftCard()
        vm.onApplyGiftCard()
        val state = vm.uiState.value
        assertEquals(2_500, state.giftAppliedCents)
        assertEquals(2_500, state.coveredCents)
        assertTrue(state.paidProgress > 0.6f)
        assertEquals(1_415, state.dueCents)
    }

    @Test
    fun `cash collect of full amount confirms the order`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onSettlement(Settlement.Cash)
        assertEquals(3_915, vm.uiState.value.cashTenderedCents)
        assertEquals(4_000, nextCashRoundUpCents(vm.uiState.value.chargeCents))
        vm.onCashTenderRoundUp()
        assertEquals(4_000, vm.uiState.value.cashTenderedCents)
        assertEquals(85, vm.uiState.value.changeDueCents)
        vm.onCollectPayment()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(2_000)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(CheckoutStep.Confirmed, vm.uiState.value.step)
        assertEquals(0, vm.uiState.value.dueCents)
    }

    @Test
    fun `delivery app marks remaining amount paid`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onSettlement(Settlement.UberEats)
        assertEquals(3_915, vm.uiState.value.chargeCents)
        vm.onCollectPayment()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(2_000)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(CheckoutStep.Confirmed, vm.uiState.value.step)
    }

    @Test
    fun `split custom amount charges entered dollars then updates remaining`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onPayAmountMode(PayAmountMode.Custom)
        vm.onCustomAmountDigits("1000")
        assertEquals(1_000, vm.uiState.value.chargeCents)
        vm.onCollectPayment()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(2_000)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(CheckoutStep.Payment, vm.uiState.value.step)
        assertEquals(1_000, vm.uiState.value.paidCents)
        assertEquals(2_915, vm.uiState.value.dueCents)
        assertTrue(vm.uiState.value.paidProgress > 0.2f)
    }

    @Test
    fun `collect payment shows processing then confirms`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onCollectPayment()
        assertEquals(CheckoutOverlay.Processing, vm.uiState.value.overlay)
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(2_000)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(CheckoutStep.Confirmed, vm.uiState.value.step)
        assertEquals(CheckoutOverlay.None, vm.uiState.value.overlay)
    }

    @Test
    fun `partial card charge stays on payment until remaining is zero`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onCardChargeCustom(true)
        vm.onAmountDigit("1")
        vm.onAmountDigit("0")
        vm.onAmountDigit("0")
        vm.onAmountDigit("0")
        assertEquals(1_000, vm.uiState.value.chargeCents)
        vm.onCollectPayment()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(2_000)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(CheckoutStep.Payment, vm.uiState.value.step)
        assertEquals(1_000, vm.uiState.value.paidCents)
        assertEquals(2_915, vm.uiState.value.dueCents)
        vm.onCollectPayment()
        mainDispatcherRule.dispatcher.scheduler.advanceTimeBy(2_000)
        mainDispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(CheckoutStep.Confirmed, vm.uiState.value.step)
        assertEquals(0, vm.uiState.value.dueCents)
    }

    @Test
    fun `sending the payment link opens confirm then sent dialog`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        vm.onPay()
        vm.onOpenPaymentLink()
        assertEquals(CheckoutOverlay.PaymentLink, vm.uiState.value.overlay)
        vm.onEditPaymentLink()
        assertEquals(CheckoutOverlay.PaymentLinkEdit, vm.uiState.value.overlay)
        vm.onCustomerPhone("(415) 555-0100")
        vm.onSavePaymentLinkDetails()
        assertEquals(CheckoutOverlay.PaymentLink, vm.uiState.value.overlay)
        assertEquals("(415) 555-0100", vm.uiState.value.customerPhone)
        vm.onSendPaymentLink()
        assertEquals(CheckoutOverlay.Saved, vm.uiState.value.overlay)
        vm.onClosePaymentSent()
        assertEquals(CheckoutStep.Payment, vm.uiState.value.step)
        assertTrue(vm.uiState.value.overlay == CheckoutOverlay.None)
    }

    @Test
    fun `customer info overlay saves name and phone`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onEditCustomer()
        assertEquals(CheckoutOverlay.Customer, vm.uiState.value.overlay)
        vm.onCustomerPhone("(415) 555-0100")
        vm.onCustomerName("Alex Kim")
        vm.onSaveCustomer()
        val state = vm.uiState.value
        assertEquals(CheckoutOverlay.None, state.overlay)
        assertEquals("Alex Kim", state.customerName)
        assertEquals("(415) 555-0100", state.customerPhone)
    }

    @Test
    fun `phone demo cart matches the phone order totals`() = runTest {
        val vm = CheckoutViewModel()
        vm.onNewOrder()
        vm.onLoadPhoneDemoCart()
        assertEquals(3_600, vm.uiState.value.subtotalCents)
        assertEquals(3_915, vm.uiState.value.totalCents)
        assertEquals("Maya Rodriguez", vm.uiState.value.customerName)
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
        vm.onLoadPhoneDemoCart()
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
        vm.onApt("Apt 3B")
        vm.onCity("Oakland")
        vm.onRegion("CA")
        vm.onZip("94612")
        vm.onSaveAddress()
        val state = vm.uiState.value
        assertEquals(Fulfillment.Delivery, state.fulfillment)
        assertEquals("1847 Telegraph Ave, Apt 3B", state.addressLine)
        assertEquals("Oakland CA 94612", state.addressDetail)
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
        assertEquals(1_450, draft?.let { unitPrice(it.item, it.selected) })
        vm.onAddToOrder()
        assertEquals(CheckoutOverlay.None, vm.uiState.value.overlay)
        assertEquals(1, vm.uiState.value.lines.size)
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
