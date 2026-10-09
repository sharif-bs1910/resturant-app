package com.noshitechinc.restaurant.feature.checkout

import androidx.lifecycle.viewModelScope
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.UiText
import com.noshitechinc.restaurant.core.ui.BaseViewModel
import com.noshitechinc.restaurant.core.ui.MessageTone
import com.noshitechinc.restaurant.core.ui.ShowMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CheckoutViewModel @Inject constructor() : BaseViewModel() {
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()
    private var lineSerial = 0

    fun onOpenOrders() {
        _uiState.update { it.copy(step = CheckoutStep.Queue, overlay = CheckoutOverlay.None) }
    }

    fun onNewOrder() {
        _uiState.update {
            it.copy(
                section = KitchenSection.Checkout,
                step = CheckoutStep.Building,
                overlay = CheckoutOverlay.None,
                boardDialog = BoardDialog.None,
                canceledOrder = null,
                lines = emptyList(),
                channel = OrderChannel.Phone,
                fulfillment = Fulfillment.Pickup,
                builderQuery = "",
                giftAppliedCents = 0,
                settlement = Settlement.Card,
                customerName = "",
                customerPhone = "",
                customerDraftName = "",
                customerDraftPhone = "",
                street = "",
                apt = "",
                city = "",
                region = "",
                zip = "",
                deliveryNotes = "",
                addressLine = "",
                addressDetail = "",
                paidCents = 0,
                tipOption = TipOption.None,
                tipCustomDigits = "",
            )
        }
    }

    fun onBuilderSearch(value: String) {
        _uiState.update { it.copy(builderQuery = value) }
    }

    /** Seeds the phone demo cart for builder / payment flows that start empty. */
    fun onLoadPhoneDemoCart() {
        _uiState.update {
            it.copy(
                lines = PhoneCart,
                channel = OrderChannel.Phone,
                fulfillment = Fulfillment.Pickup,
                customerName = "Maya Rodriguez",
                customerPhone = "(415) 555-0192",
            )
        }
    }

    fun onSearch(value: String) {
        _uiState.update { it.copy(search = value) }
    }

    fun onCategory(category: MenuCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onChannel(channel: OrderChannel) {
        _uiState.update { state ->
            val lines = when {
                channel == OrderChannel.Phone && state.lines == SampleCart -> PhoneCart
                channel == OrderChannel.InStore && state.lines == PhoneCart -> SampleCart
                else -> state.lines
            }
            state.copy(channel = channel, lines = lines)
        }
    }

    fun onFulfillment(fulfillment: Fulfillment) {
        _uiState.update { it.copy(fulfillment = fulfillment) }
    }

    fun onOpenItem(item: MenuItem) {
        _uiState.update {
            it.copy(
                overlay = CheckoutOverlay.Modifiers,
                draft = ModifierDraft(item, defaultModifiers(item.id), quantity = 1, note = ""),
            )
        }
    }

    fun onToggleOption(group: ModifierGroup, optionId: String) {
        _uiState.update { state ->
            val draft = state.draft ?: return@update state
            val selected = if (group.single) {
                draft.selected.filterNot { id -> group.options.any { it.id == id } }.toSet() + optionId
            } else if (optionId in draft.selected) {
                draft.selected - optionId
            } else {
                draft.selected + optionId
            }
            state.copy(draft = draft.copy(selected = selected))
        }
    }

    fun onDraftQuantity(quantity: Int) {
        _uiState.update { state ->
            val draft = state.draft ?: return@update state
            state.copy(draft = draft.copy(quantity = quantity.coerceIn(1, 99)))
        }
    }

    fun onDraftNote(note: String) {
        _uiState.update { state ->
            val draft = state.draft ?: return@update state
            state.copy(draft = draft.copy(note = note))
        }
    }

    fun onAddToOrder() {
        _uiState.update { state ->
            val draft = state.draft ?: return@update state
            lineSerial += 1
            val line = CartLine(
                id = "line-$lineSerial",
                name = draft.item.name,
                unitCents = unitPrice(draft.item, draft.selected),
                quantity = draft.quantity,
            )
            state.copy(lines = state.lines + line, overlay = CheckoutOverlay.None, draft = null)
        }
    }

    fun onLineQuantity(lineId: String, quantity: Int) {
        _uiState.update { state ->
            state.copy(
                lines = state.lines.map { line ->
                    if (line.id == lineId) line.copy(quantity = quantity.coerceAtLeast(1)) else line
                },
            )
        }
    }

    fun onRemoveLine(lineId: String) {
        _uiState.update { it.copy(lines = it.lines.filterNot { line -> line.id == lineId }) }
    }

    fun onPay() {
        _uiState.update { state ->
            if (state.lines.isEmpty()) {
                state
            } else {
                state.copy(
                    step = CheckoutStep.Payment,
                    overlay = CheckoutOverlay.None,
                    payAmountMode = PayAmountMode.Full,
                    customChargeDigits = "",
                    cashTenderedDigits = "",
                    selectedPayLineIds = emptySet(),
                    settlement = Settlement.Card,
                    paidCents = 0,
                    pendingChargeCents = 0,
                    giftAppliedCents = 0,
                    tipOption = TipOption.None,
                    tipCustomDigits = "",
                    cardChargeCustom = false,
                    deliveryApp = null,
                    platformOrderId = "",
                )
            }
        }
    }

    fun onBackFromPayment() {
        _uiState.update {
            it.copy(
                step = CheckoutStep.Building,
                overlay = CheckoutOverlay.None,
                customChargeDigits = "",
                cashTenderedDigits = "",
                paidCents = 0,
                pendingChargeCents = 0,
                giftAppliedCents = 0,
                tipOption = TipOption.None,
                tipCustomDigits = "",
                cardChargeCustom = false,
                deliveryApp = null,
                platformOrderId = "",
            )
        }
    }

    fun onEditCustomer() {
        _uiState.update {
            it.copy(
                overlay = CheckoutOverlay.Customer,
                customerDraftName = it.customerName,
                customerDraftPhone = it.customerPhone,
            )
        }
    }

    fun onCustomerName(value: String) {
        _uiState.update { it.copy(customerDraftName = value) }
    }

    fun onCustomerPhone(value: String) {
        _uiState.update { it.copy(customerDraftPhone = value) }
    }

    fun onSaveCustomer() {
        _uiState.update {
            it.copy(
                customerName = it.customerDraftName.trim(),
                customerPhone = it.customerDraftPhone.trim(),
                overlay = CheckoutOverlay.None,
            )
        }
    }

    fun onPayAmountMode(mode: PayAmountMode) {
        _uiState.update { state ->
            state.copy(
                payAmountMode = mode,
                customChargeDigits = if (mode == PayAmountMode.Full) "" else state.customChargeDigits,
                cardChargeCustom = false,
                selectedPayLineIds = when (mode) {
                    PayAmountMode.ByItems ->
                        state.selectedPayLineIds.ifEmpty { state.lines.map { it.id }.toSet() }

                    else -> emptySet()
                },
            )
        }
    }

    fun onTogglePayLine(lineId: String) {
        _uiState.update { state ->
            val next = if (lineId in state.selectedPayLineIds) {
                state.selectedPayLineIds - lineId
            } else {
                state.selectedPayLineIds + lineId
            }
            state.copy(selectedPayLineIds = next, customChargeDigits = "")
        }
    }

    fun onSelectAllPayLines() {
        _uiState.update { state ->
            val allIds = state.lines.map { it.id }.toSet()
            val next = if (state.selectedPayLineIds.containsAll(allIds) && allIds.isNotEmpty()) {
                emptySet()
            } else {
                allIds
            }
            state.copy(selectedPayLineIds = next, customChargeDigits = "")
        }
    }

    fun onClearItemCustomAmount() {
        _uiState.update { it.copy(customChargeDigits = "", cardChargeCustom = false) }
    }

    fun onCardChargeCustom(enabled: Boolean) {
        _uiState.update { state ->
            val next = state.copy(
                cardChargeCustom = enabled,
                customChargeDigits = if (enabled) state.customChargeDigits else "",
            )
            if (state.settlement == Settlement.Cash && !enabled && next.chargeCents > 0) {
                next.copy(cashTenderedDigits = next.chargeCents.toString())
            } else {
                next
            }
        }
    }

    fun onTipOption(option: TipOption) {
        _uiState.update {
            it.copy(
                tipOption = option,
                tipCustomDigits = if (option == TipOption.Custom) it.tipCustomDigits else "",
            )
        }
    }

    fun onTipDigit(digit: String) {
        _uiState.update { state ->
            if (state.tipOption != TipOption.Custom) return@update state
            val next = (state.tipCustomDigits + digit.filter { it.isDigit() }).take(MaxAmountDigits)
            state.copy(tipCustomDigits = next)
        }
    }

    fun onTipDelete() {
        _uiState.update { it.copy(tipCustomDigits = it.tipCustomDigits.dropLast(1)) }
    }

    fun onDeliveryApp(app: DeliveryApp) {
        _uiState.update { it.copy(deliveryApp = app) }
    }

    fun onPlatformOrderId(value: String) {
        _uiState.update { it.copy(platformOrderId = value) }
    }

    fun onAmountDigit(digit: String) {
        _uiState.update { state ->
            val allow = state.payAmountMode == PayAmountMode.Custom ||
                state.payAmountMode == PayAmountMode.ByItems ||
                (
                    (state.settlement == Settlement.Card || state.settlement == Settlement.Cash) &&
                        state.cardChargeCustom
                    )
            if (!allow) return@update state
            val next = (state.customChargeDigits + digit.filter { it.isDigit() }).take(MaxAmountDigits)
            state.copy(customChargeDigits = next)
        }
    }

    fun onCustomAmountDigits(digits: String) {
        _uiState.update { state ->
            val allow = state.payAmountMode == PayAmountMode.Custom ||
                state.payAmountMode == PayAmountMode.ByItems ||
                (
                    (state.settlement == Settlement.Card || state.settlement == Settlement.Cash) &&
                        state.cardChargeCustom
                    )
            if (!allow) return@update state
            state.copy(customChargeDigits = digits.filter { it.isDigit() }.take(MaxAmountDigits))
        }
    }

    fun onTipAmountDigits(digits: String) {
        _uiState.update { state ->
            if (state.tipOption != TipOption.Custom) return@update state
            state.copy(tipCustomDigits = digits.filter { it.isDigit() }.take(MaxAmountDigits))
        }
    }

    fun onAmountDelete() {
        _uiState.update { it.copy(customChargeDigits = it.customChargeDigits.dropLast(1)) }
    }

    fun onCashDigit(digit: String) {
        _uiState.update { state ->
            val next = (state.cashTenderedDigits + digit.filter { it.isDigit() }).take(MaxAmountDigits)
            state.copy(cashTenderedDigits = next)
        }
    }

    fun onCashAmountDigits(digits: String) {
        _uiState.update {
            it.copy(cashTenderedDigits = digits.filter { ch -> ch.isDigit() }.take(MaxAmountDigits))
        }
    }

    fun onCashTenderExact() {
        _uiState.update { state ->
            val charge = state.chargeCents
            state.copy(cashTenderedDigits = if (charge > 0) charge.toString() else "")
        }
    }

    fun onCashTenderRoundUp() {
        _uiState.update { state ->
            val roundUp = nextCashRoundUpCents(state.chargeCents)
            state.copy(cashTenderedDigits = if (roundUp > 0) roundUp.toString() else "")
        }
    }

    fun onCashDelete() {
        _uiState.update { it.copy(cashTenderedDigits = it.cashTenderedDigits.dropLast(1)) }
    }

    fun onCollectPayment() {
        if (!tryStartPayment()) return
        viewModelScope.launch {
            val charge = _uiState.value.chargeCents.coerceAtLeast(0)
            _uiState.update { state ->
                val tendered = if (state.settlement == Settlement.Cash && state.cashTenderedDigits.isEmpty()) {
                    charge.toString()
                } else {
                    state.cashTenderedDigits
                }
                state.copy(
                    overlay = CheckoutOverlay.Processing,
                    pendingChargeCents = charge,
                    cashTenderedDigits = tendered,
                )
            }
            delay(ProcessingDelayMs)
            _uiState.update { state ->
                val appliedCharge = state.pendingChargeCents.coerceAtLeast(charge)
                val nextPaid = state.paidCents + appliedCharge
                val remaining = (
                    state.totalCents + state.tipCents - state.giftAppliedCents - nextPaid
                    ).coerceAtLeast(0)
                if (remaining <= 0) {
                    state.copy(
                        paidCents = nextPaid,
                        pendingChargeCents = 0,
                        step = CheckoutStep.Confirmed,
                        overlay = CheckoutOverlay.None,
                        customChargeDigits = "",
                        cashTenderedDigits = "",
                        selectedPayLineIds = emptySet(),
                        cardChargeCustom = false,
                        payAmountMode = PayAmountMode.Full,
                    )
                } else {
                    val next = state.copy(
                        paidCents = nextPaid,
                        pendingChargeCents = 0,
                        step = CheckoutStep.Payment,
                        overlay = CheckoutOverlay.None,
                        customChargeDigits = "",
                        cashTenderedDigits = "",
                        selectedPayLineIds = emptySet(),
                        cardChargeCustom = false,
                        payAmountMode = PayAmountMode.Full,
                        tipOption = TipOption.None,
                        tipCustomDigits = "",
                    )
                    if (next.settlement == Settlement.Cash && next.chargeCents > 0) {
                        next.copy(cashTenderedDigits = next.chargeCents.toString())
                    } else {
                        next
                    }
                }
            }
        }
    }

    private fun tryStartPayment(): Boolean {
        val state = _uiState.value
        if (state.overlay == CheckoutOverlay.Processing) return false
        if (state.pendingChargeCents > 0) return false
        if (state.chargeCents <= 0) return false
        if (state.settlement == Settlement.Cash &&
            state.cashTenderedDigits.isNotEmpty() &&
            state.cashTenderedCents < state.chargeCents
        ) {
            return false
        }
        return true
    }

    fun onSaveDraft() {
        _uiState.update { state ->
            if (state.step != CheckoutStep.Building || state.lines.isEmpty()) {
                state
            } else {
                state.copy(overlay = CheckoutOverlay.Draft)
            }
        }
    }

    fun onSection(section: KitchenSection) {
        _uiState.update {
            it.copy(
                section = section,
                overlay = CheckoutOverlay.None,
                boardDialog = BoardDialog.None,
                menuPromptId = null,
                settings = it.settings.copy(dialog = SettingsDialog.None, detail = SettingsDetail.None, pin = ""),
            )
        }
    }

    fun onSettingsChange(next: SettingsState) {
        _uiState.update { it.copy(settings = next) }
    }

    fun onKitchenMode(busy: Boolean) {
        _uiState.update { it.copy(kitchenBusy = busy) }
    }

    fun onSettlement(settlement: Settlement) {
        _uiState.update { state ->
            val next = state.copy(
                settlement = settlement,
                deliveryApp = when (settlement) {
                    Settlement.UberEats -> DeliveryApp.UberEats
                    Settlement.DoorDash -> DeliveryApp.DoorDash
                    Settlement.Grubhub -> DeliveryApp.Grubhub
                    else -> null
                },
                cardChargeCustom = when (settlement) {
                    Settlement.Card, Settlement.Cash -> state.cardChargeCustom
                    else -> false
                },
                cashTenderedDigits = "",
            )
            if (settlement == Settlement.Cash && next.chargeCents > 0) {
                next.copy(cashTenderedDigits = next.chargeCents.toString())
            } else {
                next
            }
        }
    }

    fun onRedeemGiftCard() {
        _uiState.update { it.copy(overlay = CheckoutOverlay.GiftCard) }
    }

    fun onGiftField(field: GiftField) {
        _uiState.update { it.copy(giftField = field) }
    }

    fun onGiftDigit(digit: String) {
        _uiState.update { state ->
            if (state.giftField == GiftField.Pin) {
                state.copy(giftPin = (state.giftPin.filter { it.isDigit() } + digit).take(MaxPin))
            } else {
                val digits = (state.giftNumber.filter { it.isDigit() } + digit).take(MaxCardDigits)
                state.copy(giftNumber = digits.chunked(CardGroup).joinToString(" "))
            }
        }
    }

    fun onGiftDelete() {
        _uiState.update { state ->
            if (state.giftField == GiftField.Pin) {
                state.copy(giftPin = state.giftPin.dropLast(1))
            } else {
                val digits = state.giftNumber.filter { it.isDigit() }.dropLast(1)
                state.copy(giftNumber = digits.chunked(CardGroup).joinToString(" "))
            }
        }
    }

    fun onContinueGiftCard() {
        _uiState.update { state ->
            val maxApply = minOf(GiftCardBalanceCents, state.dueCents.takeIf { it > 0 } ?: state.totalCents)
            state.copy(
                overlay = CheckoutOverlay.GiftAmount,
                giftApplyDigits = maxApply.toString(),
            )
        }
    }

    fun onGiftAmountDigit(digit: String) {
        _uiState.update { state ->
            val next = (state.giftApplyDigits + digit.filter { it.isDigit() }).take(MaxAmountDigits)
            state.copy(giftApplyDigits = next)
        }
    }

    fun onGiftAmountDigits(digits: String) {
        _uiState.update {
            it.copy(giftApplyDigits = digits.filter { ch -> ch.isDigit() }.take(MaxAmountDigits))
        }
    }

    fun onGiftAmountDelete() {
        _uiState.update { it.copy(giftApplyDigits = it.giftApplyDigits.dropLast(1)) }
    }

    fun onPrintTicket() {
        sendEffect(ShowMessage(UiText.Resource(R.string.checkout_print_ticket_queued), MessageTone.Success))
    }

    fun onPrintReceipt() {
        sendEffect(ShowMessage(UiText.Resource(R.string.checkout_print_receipt_queued), MessageTone.Success))
    }

    fun onApplyGiftCard() {
        _uiState.update { state ->
            val maxApply = minOf(
                GiftCardBalanceCents,
                (state.totalCents + state.tipCents - state.paidCents).coerceAtLeast(0),
            )
            val applied = if (state.overlay == CheckoutOverlay.GiftAmount) {
                state.giftApplyCents.coerceIn(0, maxApply)
            } else {
                maxApply
            }
            val remainingAfter = (
                state.totalCents + state.tipCents - state.paidCents - applied
                ).coerceAtLeast(0)
            when {
                remainingAfter <= 0 && state.step == CheckoutStep.Payment ->
                    state.copy(
                        giftAppliedCents = applied,
                        giftApplyDigits = "",
                        overlay = CheckoutOverlay.None,
                        step = CheckoutStep.Confirmed,
                        customChargeDigits = "",
                        cashTenderedDigits = "",
                        cardChargeCustom = false,
                        payAmountMode = PayAmountMode.Full,
                    )

                state.step == CheckoutStep.Payment ->
                    state.copy(
                        giftAppliedCents = applied,
                        giftApplyDigits = "",
                        overlay = CheckoutOverlay.None,
                        customChargeDigits = "",
                        cashTenderedDigits = "",
                        cardChargeCustom = false,
                        payAmountMode = PayAmountMode.Full,
                    )

                else ->
                    state.copy(
                        giftAppliedCents = applied,
                        giftApplyDigits = "",
                        overlay = CheckoutOverlay.Tender,
                    )
            }
        }
    }

    fun onOpenPaymentLink() {
        _uiState.update { it.copy(overlay = CheckoutOverlay.PaymentLink) }
    }

    fun onEditPaymentLink() {
        _uiState.update {
            it.copy(
                overlay = CheckoutOverlay.PaymentLinkEdit,
                customerDraftName = it.customerName,
                customerDraftPhone = it.customerPhone,
            )
        }
    }

    fun onSavePaymentLinkDetails() {
        _uiState.update {
            it.copy(
                customerName = it.customerDraftName.trim().ifBlank { it.customerName },
                customerPhone = it.customerDraftPhone.trim().ifBlank { it.customerPhone },
                overlay = CheckoutOverlay.PaymentLink,
            )
        }
    }

    fun onSendPaymentLink() {
        _uiState.update { it.copy(overlay = CheckoutOverlay.Saved) }
    }

    fun onClosePaymentSent() {
        _uiState.update { it.copy(overlay = CheckoutOverlay.None) }
    }

    fun onBackToCart() {
        _uiState.update { state ->
            when (state.overlay) {
                CheckoutOverlay.PaymentLinkEdit -> state.copy(overlay = CheckoutOverlay.PaymentLink)
                else -> state.copy(overlay = CheckoutOverlay.None, draft = null)
            }
        }
    }

    fun onBackToTender() {
        _uiState.update { state ->
            val overlay = when {
                state.overlay == CheckoutOverlay.GiftAmount -> CheckoutOverlay.GiftCard
                state.overlay == CheckoutOverlay.PaymentLinkEdit -> CheckoutOverlay.PaymentLink
                state.step == CheckoutStep.Payment -> CheckoutOverlay.None
                else -> CheckoutOverlay.Tender
            }
            state.copy(overlay = overlay)
        }
    }

    fun onEditAddress() {
        _uiState.update { it.copy(overlay = CheckoutOverlay.Address, addressField = AddressField.Street) }
    }

    fun onAddressField(field: AddressField) {
        _uiState.update { it.copy(addressField = field) }
    }

    fun onStreet(value: String) {
        _uiState.update { it.copy(street = value, addressField = AddressField.Street) }
    }

    fun onApt(value: String) {
        _uiState.update { it.copy(apt = value, addressField = AddressField.Apt) }
    }

    fun onCity(value: String) {
        _uiState.update { it.copy(city = value, addressField = AddressField.City) }
    }

    fun onRegion(value: String) {
        _uiState.update { it.copy(region = value.take(2).uppercase(), addressField = AddressField.Region) }
    }

    fun onZip(value: String) {
        _uiState.update { it.copy(zip = value.filter(Char::isDigit).take(MaxZip), addressField = AddressField.Zip) }
    }

    fun onDeliveryNotes(value: String) {
        _uiState.update { it.copy(deliveryNotes = value, addressField = AddressField.Notes) }
    }

    fun onKeepAsPickup() {
        _uiState.update { it.copy(fulfillment = Fulfillment.Pickup, overlay = CheckoutOverlay.None) }
    }

    fun onSaveAddress() {
        _uiState.update { state ->
            val line = if (state.apt.isBlank()) state.street else "${state.street}, ${state.apt}"
            val locality = listOf(state.city, state.region, state.zip).filter { it.isNotBlank() }.joinToString(" ")
            val detail = listOf(locality, state.deliveryNotes).filter { it.isNotBlank() }.joinToString(" · ")
            state.copy(
                addressLine = line,
                addressDetail = detail,
                fulfillment = Fulfillment.Delivery,
                overlay = CheckoutOverlay.None,
            )
        }
    }

    fun onOpenOrder(order: OpenOrder) {
        _uiState.update {
            it.copy(step = CheckoutStep.Queue, overlay = CheckoutOverlay.None, selectedOrderNumber = order.number)
        }
    }

    fun onQueueFilter(filter: QueueFilter) {
        _uiState.update { it.copy(queueFilter = filter) }
    }

    fun onQueueSearch(value: String) {
        _uiState.update { it.copy(queueQuery = value) }
    }

    fun onSelectQueueOrder(number: String) {
        _uiState.update { it.copy(selectedOrderNumber = number) }
    }

    fun onCloseQueue() {
        _uiState.update { it.copy(step = CheckoutStep.Idle, overlay = CheckoutOverlay.None) }
    }

    fun onBoardFilter(filter: BoardFilter) {
        _uiState.update { it.copy(boardFilter = filter) }
    }

    fun onBoardSearch(value: String) {
        _uiState.update { it.copy(boardQuery = value) }
    }

    fun onSelectBoard(number: String) {
        _uiState.update { it.copy(selectedBoardNumber = number) }
    }

    fun onToggleBoardLine(lineId: String) {
        _uiState.update { state ->
            state.copy(
                boardOrders = state.boardOrders.map { order ->
                    if (order.number != state.selectedBoardNumber) {
                        order
                    } else {
                        val expanded = if (lineId in order.expanded) order.expanded - lineId else order.expanded + lineId
                        order.copy(expanded = expanded)
                    }
                },
            )
        }
    }

    fun onMarkBoardComplete() {
        _uiState.update { state ->
            state.copy(
                boardOrders = state.boardOrders.map { order ->
                    if (order.number == state.selectedBoardNumber) order.copy(status = OpenOrderStatus.Completed) else order
                },
            )
        }
    }

    fun onToggleBoardFulfillment() {
        _uiState.update { state ->
            val order = state.selectedBoard ?: return@update state
            if (order.fulfillment == Fulfillment.Pickup && order.addressLine == null) {
                state.copy(boardDialog = BoardDialog.Address, boardField = BoardField.Street)
            } else {
                state.copy(
                    boardOrders = state.boardOrders.map { current ->
                        if (current.number != order.number) {
                            current
                        } else {
                            val next = if (current.fulfillment == Fulfillment.Pickup) Fulfillment.Delivery else Fulfillment.Pickup
                            current.copy(fulfillment = next)
                        }
                    },
                )
            }
        }
    }

    fun onAddCharge() {
        _uiState.update { it.copy(boardDialog = BoardDialog.Charge, boardField = BoardField.Amount) }
    }

    fun onRefund() {
        _uiState.update { state ->
            val total = state.selectedBoard?.totalCents ?: return@update state
            state.copy(boardDialog = BoardDialog.Refund, boardField = BoardField.Amount, refundAmount = moneyInput(total))
        }
    }

    fun onCancelOrder() {
        _uiState.update { it.copy(boardDialog = BoardDialog.Cancel) }
    }

    fun onDismissBoardDialog() {
        _uiState.update { it.copy(boardDialog = BoardDialog.None) }
    }

    fun onBoardField(field: BoardField) {
        _uiState.update { it.copy(boardField = field) }
    }

    fun onChargeAmount(value: String) {
        _uiState.update { it.copy(chargeAmount = value, boardField = BoardField.Amount) }
    }

    fun onChargeReason(value: String) {
        _uiState.update { it.copy(chargeReason = value, boardField = BoardField.Reason) }
    }

    fun onRefundAmount(value: String) {
        _uiState.update { it.copy(refundAmount = value, boardField = BoardField.Amount) }
    }

    fun onRefundReason(value: String) {
        _uiState.update { it.copy(refundReason = value, boardField = BoardField.Reason) }
    }

    fun onBoardStreet(value: String) {
        _uiState.update { it.copy(boardStreet = value, boardField = BoardField.Street) }
    }

    fun onBoardApt(value: String) {
        _uiState.update { it.copy(boardApt = value, boardField = BoardField.Apt) }
    }

    fun onBoardZip(value: String) {
        _uiState.update { it.copy(boardZip = value.filter(Char::isDigit).take(MaxZip), boardField = BoardField.Zip) }
    }

    fun onBoardNotes(value: String) {
        _uiState.update { it.copy(boardNotes = value, boardField = BoardField.Notes) }
    }

    fun onConfirmCharge() {
        _uiState.update { state ->
            val add = parseDollars(state.chargeAmount)
            state.copy(
                boardDialog = BoardDialog.None,
                boardOrders = state.boardOrders.map { order ->
                    if (order.number == state.selectedBoardNumber) order.copy(subtotalCents = order.subtotalCents + add) else order
                },
            )
        }
    }

    fun onConfirmRefund() {
        _uiState.update { state ->
            val take = parseDollars(state.refundAmount)
            state.copy(
                boardDialog = BoardDialog.None,
                boardOrders = state.boardOrders.map { order ->
                    if (order.number == state.selectedBoardNumber) {
                        order.copy(subtotalCents = (order.subtotalCents - take).coerceAtLeast(0))
                    } else {
                        order
                    }
                },
            )
        }
    }

    fun onConfirmCancel() {
        _uiState.update { state ->
            val order = state.selectedBoard ?: return@update state.copy(boardDialog = BoardDialog.None)
            state.copy(
                boardDialog = BoardDialog.None,
                canceledOrder = order,
                boardOrders = state.boardOrders.filter { it.number != order.number },
            )
        }
    }

    fun onKeepBoardPickup() {
        _uiState.update { it.copy(boardDialog = BoardDialog.None) }
    }

    fun onSaveBoardAddress() {
        _uiState.update { state ->
            val line = if (state.boardApt.isBlank()) state.boardStreet else "${state.boardStreet}, ${state.boardApt}"
            state.copy(
                boardDialog = BoardDialog.None,
                boardOrders = state.boardOrders.map { order ->
                    if (order.number == state.selectedBoardNumber) {
                        order.copy(fulfillment = Fulfillment.Delivery, addressLine = line)
                    } else {
                        order
                    }
                },
            )
        }
    }

    fun onCloseBoardDetail() {
        _uiState.update { it.copy(selectedBoardNumber = "") }
    }

    fun onEditQueueOrder() {
        _uiState.update { state ->
            val order = state.selectedOrder ?: return@update state
            state.copy(
                step = CheckoutStep.Building,
                overlay = CheckoutOverlay.None,
                lines = order.toCart(),
                channel = order.channel,
                fulfillment = order.fulfillment,
                orderNumber = order.number,
            )
        }
    }

    fun onPayQueueOrder() {
        _uiState.update { state ->
            val order = state.selectedOrder ?: return@update state
            state.copy(
                step = CheckoutStep.Payment,
                lines = order.toCart(),
                channel = order.channel,
                fulfillment = order.fulfillment,
                orderNumber = order.number,
                overlay = CheckoutOverlay.None,
                payAmountMode = PayAmountMode.Full,
                customChargeDigits = "",
                cashTenderedDigits = "",
                settlement = Settlement.Card,
                giftAppliedCents = 0,
            )
        }
    }

    fun onGiftFromQueue() {
        _uiState.update { state ->
            val order = state.selectedOrder ?: return@update state
            state.copy(
                step = CheckoutStep.Payment,
                lines = order.toCart(),
                channel = order.channel,
                fulfillment = order.fulfillment,
                orderNumber = order.number,
                overlay = CheckoutOverlay.GiftCard,
                payAmountMode = PayAmountMode.Full,
                customChargeDigits = "",
                cashTenderedDigits = "",
            )
        }
    }

    fun onMenuFilter(filter: MenuListFilter) {
        _uiState.update { it.copy(menuFilter = filter) }
    }

    fun onMenuSearch(query: String) {
        _uiState.update { it.copy(menuQuery = query) }
    }

    fun onSelectMenuItem(id: String) {
        _uiState.update { state ->
            val item = state.menuItems.firstOrNull { it.id == id } ?: return@update state
            state.copy(menuDraft = item.toDraft(), menuPromptId = null)
        }
    }

    fun onMenuName(value: String) {
        _uiState.update { it.copy(menuDraft = it.menuDraft.copy(name = value)) }
    }

    fun onMenuDescription(value: String) {
        _uiState.update { it.copy(menuDraft = it.menuDraft.copy(description = value)) }
    }

    fun onMenuPrice(value: String) {
        _uiState.update { it.copy(menuDraft = it.menuDraft.copy(priceText = value)) }
    }

    fun onMenuStock(value: String) {
        _uiState.update { it.copy(menuDraft = it.menuDraft.copy(stockText = value.filter(Char::isDigit))) }
    }

    fun onStopTracking() {
        _uiState.update { it.copy(menuDraft = it.menuDraft.copy(trackStock = false)) }
    }

    fun onTrackInventory() {
        _uiState.update { state ->
            val stock = state.menuDraft.stockText.ifEmpty { "0" }
            state.copy(menuDraft = state.menuDraft.copy(trackStock = true, stockText = stock))
        }
    }

    fun onMenuAvailability(id: String, available: Boolean) {
        _uiState.update { state ->
            if (available) {
                state.copy(
                    menuItems = state.menuItems.map { item ->
                        if (item.id == id) item.copy(available = true, unavailableUntil = null) else item
                    },
                    menuDraft = if (state.menuDraft.id == id) state.menuDraft.copy(available = true) else state.menuDraft,
                    menuPromptId = null,
                )
            } else {
                state.copy(menuPromptId = id, unavailableUntil = UnavailableUntil.EndOfDay)
            }
        }
    }

    fun onUnavailableUntil(until: UnavailableUntil) {
        _uiState.update { it.copy(unavailableUntil = until) }
    }

    fun onConfirmUnavailable() {
        _uiState.update { state ->
            val id = state.menuPromptId ?: return@update state
            state.copy(
                menuItems = state.menuItems.map { item ->
                    if (item.id == id) item.copy(available = false, unavailableUntil = state.unavailableUntil) else item
                },
                menuDraft = if (state.menuDraft.id == id) state.menuDraft.copy(available = false) else state.menuDraft,
                menuPromptId = null,
            )
        }
    }

    fun onDismissUnavailable() {
        _uiState.update { it.copy(menuPromptId = null) }
    }

    fun onSaveMenuItem() {
        _uiState.update { state ->
            val draft = state.menuDraft
            val stock = if (draft.trackStock) draft.stockText.toIntOrNull() ?: 0 else null
            state.copy(
                menuItems = state.menuItems.map { item ->
                    if (item.id != draft.id) {
                        item
                    } else {
                        item.copy(
                            name = draft.name,
                            description = draft.description,
                            priceCents = parseMenuPrice(draft.priceText),
                            stock = stock,
                            available = draft.available,
                        )
                    }
                },
            )
        }
    }

    fun onCancelMenuEdit() {
        _uiState.update { state ->
            val item = state.menuItems.firstOrNull { it.id == state.menuDraft.id } ?: return@update state
            state.copy(menuDraft = item.toDraft())
        }
    }

    private companion object {
        const val MaxPin = 4
        const val MaxCardDigits = 12
        const val CardGroup = 4
        const val MaxZip = 5
        const val MaxAmountDigits = 7
        const val ProcessingDelayMs = 1_600L
    }
}
