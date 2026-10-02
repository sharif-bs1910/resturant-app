package com.noshitechinc.restaurant.feature.checkout

enum class CheckoutStep {
    Idle,
    Queue,
    Building,
    Confirmed,
}

enum class KitchenSection {
    Checkout,
    Orders,
}

enum class CheckoutOverlay {
    None,
    Modifiers,
    Tender,
    GiftCard,
    Address,
    Draft,
    Saved,
}

enum class AddressField {
    Street,
    Apt,
    Zip,
    Notes,
}

enum class GiftField {
    Number,
    Pin,
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
    val addressLine: String = "1847 Telegraph Ave, Apt 3B",
    val addressDetail: String = "Oakland 94612 · gate #1980",
    val street: String = "1847 Telegraph Ave",
    val apt: String = "Apt 3B",
    val zip: String = "94612",
    val deliveryNotes: String = "Gate code #1980 · leave at door",
    val addressField: AddressField = AddressField.Street,
    val search: String = "",
    val giftNumber: String = "8043 2210 4821",
    val giftPin: String = "4821",
    val giftField: GiftField = GiftField.Number,
    val giftAppliedCents: Int = 0,
    val settlement: Settlement = Settlement.Cash,
    val draft: ModifierDraft? = null,
    val orderNumber: String = "1043",
    val queueFilter: QueueFilter = QueueFilter.All,
    val queueQuery: String = "",
    val selectedOrderNumber: String = "1042",
    val boardOrders: List<BoardOrder> = DefaultBoard,
    val boardFilter: BoardFilter = BoardFilter.All,
    val boardQuery: String = "",
    val selectedBoardNumber: String = "1042",
) {
    val subtotalCents: Int = subtotalCents(lines)
    val taxCents: Int = taxCents(subtotalCents)
    val totalCents: Int = subtotalCents + taxCents
    val dueCents: Int = (totalCents - giftAppliedCents).coerceAtLeast(0)
    val itemCount: Int = lines.sumOf { it.quantity }

    val visibleOrders: List<OpenOrder> = OpenOrders.filter { order ->
        val query = search.trim()
        query.isEmpty() || order.number.contains(query, ignoreCase = true) ||
            order.guest.contains(query, ignoreCase = true) ||
            order.summary.contains(query, ignoreCase = true)
    }

    val visibleMenu: List<MenuItem> = MenuCatalog.filter { category == MenuCategory.All || it.category == category }

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
            val statusMatches = boardFilter.status == null || order.status == boardFilter.status
            val query = boardQuery.trim()
            val textMatches = query.isEmpty() || order.number.contains(query, ignoreCase = true) ||
                order.guest.contains(query, ignoreCase = true)
            statusMatches && textMatches
        }

    val selectedBoard: BoardOrder?
        get() = visibleBoard.firstOrNull { it.number == selectedBoardNumber }
}
