package com.noshitechinc.restaurant.feature.checkout

enum class CheckoutStep {
    Idle,
    Queue,
    Building,
    Payment,
    Confirmed,
}

enum class KitchenSection {
    Checkout,
    Orders,
    Menu,
    Settings,
    Status,
}

enum class CheckoutOverlay {
    None,
    Modifiers,
    Tender,
    GiftCard,
    GiftAmount,
    Address,
    Draft,
    Saved,
    Customer,
    PaymentLink,
    PaymentLinkEdit,
    Processing,
}

enum class AddressField {
    Street,
    Apt,
    City,
    Region,
    Zip,
    Notes,
}

enum class GiftField {
    Number,
    Pin,
}

enum class BoardDialog {
    None,
    Charge,
    Refund,
    Address,
    Cancel,
}

enum class BoardField {
    Amount,
    Reason,
    Street,
    Apt,
    Zip,
    Notes,
}

data class CheckoutUiState(
    val section: KitchenSection = KitchenSection.Checkout,
    val step: CheckoutStep = CheckoutStep.Idle,
    val overlay: CheckoutOverlay = CheckoutOverlay.None,
    val category: MenuCategory = MenuCategory.All,
    val channel: OrderChannel = OrderChannel.InStore,
    val fulfillment: Fulfillment = Fulfillment.Pickup,
    val lines: List<CartLine> = emptyList(),
    val customerName: String = "Maya Rodriguez",
    val customerPhone: String = "(415) 555-0192",
    val customerDraftName: String = "",
    val customerDraftPhone: String = "",
    val addressLine: String = "1847 Telegraph Ave, Apt 3B",
    val addressDetail: String = "Oakland, CA 94612 · gate #1980",
    val street: String = "1847 Telegraph Ave",
    val apt: String = "Apt 3B",
    val city: String = "Oakland",
    val region: String = "CA",
    val zip: String = "94612",
    val deliveryNotes: String = "Gate code #1980 · leave at door",
    val addressField: AddressField = AddressField.Street,
    val search: String = "",
    val builderQuery: String = "",
    val giftNumber: String = "8043 2210 4821",
    val giftPin: String = "4821",
    val giftField: GiftField = GiftField.Number,
    val giftAppliedCents: Int = 0,
    val giftApplyDigits: String = "",
    val paidCents: Int = 0,
    val pendingChargeCents: Int = 0,
    val tipOption: TipOption = TipOption.None,
    val tipCustomDigits: String = "",
    val cardChargeCustom: Boolean = false,
    val settlement: Settlement = Settlement.Card,
    val payAmountMode: PayAmountMode = PayAmountMode.Full,
    val customChargeDigits: String = "",
    val cashTenderedDigits: String = "",
    val selectedPayLineIds: Set<String> = emptySet(),
    val deliveryApp: DeliveryApp? = null,
    val platformOrderId: String = "",
    val tableNumber: String = "12",
    val cardLastFour: String = "4242",
    val draft: ModifierDraft? = null,
    val orderNumber: String = "1043",
    val queueFilter: QueueFilter = QueueFilter.All,
    val queueQuery: String = "",
    val selectedOrderNumber: String = "1042",
    val boardOrders: List<BoardOrder> = DefaultBoard,
    val boardFilter: BoardFilter = BoardFilter.All,
    val boardQuery: String = "",
    val selectedBoardNumber: String = "1042",
    val boardDialog: BoardDialog = BoardDialog.None,
    val boardField: BoardField = BoardField.Amount,
    val chargeAmount: String = "5.00",
    val chargeReason: String = "Extra protein",
    val refundAmount: String = "",
    val refundReason: String = "Item out of stock",
    val boardStreet: String = "1847 Telegraph Ave",
    val boardApt: String = "Apt 3B",
    val boardZip: String = "94612",
    val boardNotes: String = "Gate code #1980 · leave at door",
    val canceledOrder: BoardOrder? = null,
    val menuItems: List<KitchenMenuItem> = DefaultKitchenMenu,
    val menuQuery: String = "",
    val menuFilter: MenuListFilter = MenuListFilter.All,
    val menuDraft: MenuDraft = DefaultKitchenMenu.first().toDraft(),
    val menuPromptId: String? = null,
    val unavailableUntil: UnavailableUntil = UnavailableUntil.EndOfDay,
    val settings: SettingsState = SettingsState(),
    val kitchenBusy: Boolean = false,
) {
    val subtotalCents: Int = subtotalCents(lines)
    val taxCents: Int = taxCents(subtotalCents)
    val totalCents: Int = subtotalCents + taxCents
    val tipCustomCents: Int = digitsToCents(tipCustomDigits)

    /** Remaining food/tax before the current tip selection. */
    val remainingBeforeTipCents: Int =
        (totalCents - giftAppliedCents - paidCents).coerceAtLeast(0)
    val selectedPayLines: List<CartLine> = lines.filter { it.id in selectedPayLineIds }
    val selectedSubtotalCents: Int = subtotalCents(selectedPayLines)
    val selectedTaxCents: Int = taxCents(selectedSubtotalCents)
    val selectedTotalCents: Int =
        (selectedSubtotalCents + selectedTaxCents).coerceAtMost(remainingBeforeTipCents)
    val customChargeCents: Int = digitsToCents(customChargeDigits)

    /** Base used for tip % chips (amount being paid this round, before tip). */
    val tipBaseCents: Int
        get() = when (payAmountMode) {
            PayAmountMode.Full -> remainingBeforeTipCents

            PayAmountMode.Custom ->
                if (customChargeDigits.isEmpty()) {
                    remainingBeforeTipCents
                } else {
                    customChargeCents.coerceIn(0, remainingBeforeTipCents)
                }

            PayAmountMode.ByItems ->
                if (customChargeDigits.isEmpty()) {
                    selectedTotalCents
                } else {
                    customChargeCents.coerceIn(0, selectedTotalCents)
                }
        }
    val tipCents: Int
        get() = when (tipOption) {
            TipOption.None -> 0
            TipOption.Percent5 -> tipBaseCents * 5 / 100
            TipOption.Percent10 -> tipBaseCents * 10 / 100
            TipOption.Percent20 -> tipBaseCents * 20 / 100
            TipOption.Custom -> tipCustomCents.coerceAtLeast(0)
        }
    val dueCents: Int = (remainingBeforeTipCents + tipCents).coerceAtLeast(0)
    val amountToPayCents: Int
        get() = when (payAmountMode) {
            PayAmountMode.Full -> dueCents

            PayAmountMode.Custom ->
                if (customChargeDigits.isEmpty()) 0 else tipBaseCents + tipCents

            PayAmountMode.ByItems ->
                if (selectedPayLineIds.isEmpty() && customChargeDigits.isEmpty()) {
                    0
                } else {
                    tipBaseCents + tipCents
                }
        }
    val chargeCents: Int
        get() = when {
            settlement.isDeliveryApp -> amountToPayCents

            payAmountMode == PayAmountMode.Full &&
                (settlement == Settlement.Card || settlement == Settlement.Cash) &&
                cardChargeCustom ->
                if (customChargeDigits.isEmpty()) {
                    0
                } else {
                    customChargeCents.coerceIn(0, dueCents)
                }

            else -> amountToPayCents
        }
    val cashTenderedCents: Int = digitsToCents(cashTenderedDigits)
    val giftApplyCents: Int = digitsToCents(giftApplyDigits)
    val changeDueCents: Int = (cashTenderedCents - chargeCents).coerceAtLeast(0)
    val billCents: Int
        get() = (totalCents + tipCents).coerceAtLeast(0)
    val coveredCents: Int
        get() = (paidCents + giftAppliedCents).coerceAtMost(billCents.coerceAtLeast(0))
    val paidProgress: Float
        get() {
            val goal = billCents.coerceAtLeast(1)
            return (coveredCents.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
        }
    val hasCustomer: Boolean = customerName.isNotBlank() || customerPhone.isNotBlank()
    val itemCount: Int = lines.sumOf { it.quantity }
    val billGuestLabel: String
        get() = customerName.trim().ifBlank { "Guest" }.substringBefore(' ').ifBlank { "Guest" }

    val visibleOrders: List<OpenOrder> = OpenOrders.filter { order ->
        val query = search.trim()
        query.isEmpty() || order.number.contains(query, ignoreCase = true) ||
            order.guest.contains(query, ignoreCase = true) ||
            order.summary.contains(query, ignoreCase = true)
    }

    val visibleMenu: List<MenuItem> = MenuCatalog.filter { item ->
        val categoryMatches = category == MenuCategory.All || item.category == category
        val query = builderQuery.trim()
        val textMatches = query.isEmpty() || item.name.contains(query, ignoreCase = true)
        categoryMatches && textMatches
    }

    val visibleQueue: List<QueueOrder> = QueueOrders.filter { order ->
        val statusMatches = queueFilter.status == null || order.status == queueFilter.status
        val query = queueQuery.trim()
        val textMatches = query.isEmpty() || order.number.contains(query, ignoreCase = true) ||
            order.guest.contains(query, ignoreCase = true)
        statusMatches && textMatches
    }

    val selectedOrder: QueueOrder?
        get() = visibleQueue.firstOrNull { it.number == selectedOrderNumber } ?: visibleQueue.firstOrNull()

    val visibleBoard: List<BoardOrder>
        get() = boardOrders.filter { order ->
            val statusMatches = when {
                boardFilter.dueOnly -> order.due
                boardFilter.status == null -> true
                else -> order.status == boardFilter.status
            }
            val query = boardQuery.trim()
            val textMatches = query.isEmpty() || order.number.contains(query, ignoreCase = true) ||
                order.guest.contains(query, ignoreCase = true)
            statusMatches && textMatches
        }

    val selectedBoard: BoardOrder?
        get() = visibleBoard.firstOrNull { it.number == selectedBoardNumber }

    val visibleKitchenMenu: List<KitchenMenuItem>
        get() = menuItems.filter { item ->
            val statusMatches = menuFilter != MenuListFilter.EightySixed || !item.available
            val query = menuQuery.trim()
            val textMatches = query.isEmpty() ||
                item.name.contains(query, ignoreCase = true) ||
                item.category.name.contains(query, ignoreCase = true)
            statusMatches && textMatches
        }

    val promptedMenuItem: KitchenMenuItem?
        get() = menuItems.firstOrNull { it.id == menuPromptId }
}

private fun digitsToCents(digits: String): Int {
    if (digits.isEmpty()) return 0
    return digits.filter { it.isDigit() }.toLongOrNull()?.toInt()?.coerceAtLeast(0) ?: 0
}
