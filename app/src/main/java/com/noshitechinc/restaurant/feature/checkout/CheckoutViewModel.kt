package com.noshitechinc.restaurant.feature.checkout

import com.noshitechinc.restaurant.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
                lines = SampleCart,
                channel = OrderChannel.InStore,
                fulfillment = Fulfillment.Pickup,
                giftAppliedCents = 0,
                settlement = Settlement.Cash,
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
            if (state.lines.isEmpty()) state else state.copy(overlay = CheckoutOverlay.Tender)
        }
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
        _uiState.update { it.copy(section = section, overlay = CheckoutOverlay.None, boardDialog = BoardDialog.None, menuPromptId = null) }
    }

    fun onSettlement(settlement: Settlement) {
        _uiState.update { it.copy(settlement = settlement) }
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

    fun onApplyGiftCard() {
        _uiState.update { state ->
            val applied = minOf(GiftCardBalanceCents, state.totalCents)
            state.copy(giftAppliedCents = applied, overlay = CheckoutOverlay.Tender)
        }
    }

    fun onSendPaymentLink() {
        _uiState.update { it.copy(step = CheckoutStep.Building, overlay = CheckoutOverlay.Saved) }
    }

    fun onClosePaymentSent() {
        _uiState.update { it.copy(step = CheckoutStep.Confirmed, overlay = CheckoutOverlay.None) }
    }

    fun onBackToCart() {
        _uiState.update { it.copy(overlay = CheckoutOverlay.None, draft = null) }
    }

    fun onBackToTender() {
        _uiState.update { it.copy(overlay = CheckoutOverlay.Tender) }
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
            val detail = "Oakland ${state.zip} · ${state.deliveryNotes}"
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
                step = CheckoutStep.Building,
                lines = order.toCart(),
                channel = order.channel,
                fulfillment = order.fulfillment,
                orderNumber = order.number,
                overlay = CheckoutOverlay.Tender,
            )
        }
    }

    fun onGiftFromQueue() {
        _uiState.update { state ->
            val order = state.selectedOrder ?: return@update state
            state.copy(
                step = CheckoutStep.Building,
                lines = order.toCart(),
                channel = order.channel,
                fulfillment = order.fulfillment,
                orderNumber = order.number,
                overlay = CheckoutOverlay.GiftCard,
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
    }
}
