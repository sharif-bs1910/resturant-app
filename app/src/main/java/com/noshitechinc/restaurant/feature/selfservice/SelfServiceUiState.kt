package com.noshitechinc.restaurant.feature.selfservice

import com.noshitechinc.restaurant.feature.checkout.CartLine
import com.noshitechinc.restaurant.feature.checkout.CheckoutOverlay
import com.noshitechinc.restaurant.feature.checkout.CheckoutStep
import com.noshitechinc.restaurant.feature.checkout.CheckoutUiState
import com.noshitechinc.restaurant.feature.checkout.GiftField
import com.noshitechinc.restaurant.feature.checkout.MenuItem
import com.noshitechinc.restaurant.feature.checkout.ModifierDraft
import com.noshitechinc.restaurant.feature.checkout.SampleCart
import com.noshitechinc.restaurant.feature.checkout.subtotalCents
import com.noshitechinc.restaurant.feature.checkout.taxCents

enum class SelfServiceStep {
    Menu,
    Guest,
    Review,
    PaymentWaiting,
    PaymentProcessing,
    PaymentDeclined,
    Success,
}

enum class SelfServiceOverlay {
    None,
    Modifiers,
    GiftCard,
    GiftAmount,
}

data class SelfServiceUiState(
    val step: SelfServiceStep = SelfServiceStep.Menu,
    val overlay: SelfServiceOverlay = SelfServiceOverlay.None,
    val meal: SelfServiceMeal = SelfServiceMeal.Dinner,
    val category: SelfServiceCategory = SelfServiceCategory.Pizzas,
    val orderType: SelfServiceOrderType = SelfServiceOrderType.Takeout,
    val query: String = "",
    val lines: List<CartLine> = emptyList(),
    val draft: ModifierDraft? = null,
    val guestPhoneDigits: String = "",
    val guestName: String = "",
    val tableNumber: String = "",
    val giftNumber: String = "",
    val giftPin: String = "",
    val giftField: GiftField = GiftField.Number,
    val giftApplyDigits: String = "",
    val giftAppliedCents: Int = 0,
    val orderNumber: String = "1042",
    val tokenNumber: String = "1042",
    val cardLastFour: String = "4242",
    val expandedLineIds: Set<String> = emptySet(),
) {
    val subtotalCents: Int = subtotalCents(lines)
    val taxCents: Int = taxCents(subtotalCents)
    val totalCents: Int = subtotalCents + taxCents
    val giftApplyCents: Int =
        giftApplyDigits.filter { it.isDigit() }.toLongOrNull()?.toInt()?.coerceAtLeast(0) ?: 0
    val dueCents: Int = (totalCents - giftAppliedCents).coerceAtLeast(0)
    val itemCount: Int = lines.sumOf { it.quantity }
    val guestReady: Boolean =
        guestPhoneDigits.length >= 10 && guestName.isNotBlank() && tableNumber.isNotBlank()
    val visibleMenu: List<MenuItem> = filteredSelfServiceMenu(meal, category, query)

    fun asCheckoutProjection(): CheckoutUiState = CheckoutUiState(
        step = when (step) {
            SelfServiceStep.Success -> CheckoutStep.Confirmed

            SelfServiceStep.PaymentWaiting,
            SelfServiceStep.PaymentProcessing,
            SelfServiceStep.PaymentDeclined,
            -> CheckoutStep.Payment

            else -> CheckoutStep.Building
        },
        overlay = when (overlay) {
            SelfServiceOverlay.Modifiers -> CheckoutOverlay.Modifiers
            SelfServiceOverlay.GiftCard -> CheckoutOverlay.GiftCard
            SelfServiceOverlay.GiftAmount -> CheckoutOverlay.GiftAmount
            SelfServiceOverlay.None -> CheckoutOverlay.None
        },
        builderQuery = query,
        lines = lines,
        draft = draft,
        customerName = guestName,
        customerPhone = guestPhoneDigits,
        tableNumber = tableNumber,
        giftNumber = giftNumber,
        giftPin = giftPin,
        giftField = giftField,
        giftApplyDigits = giftApplyDigits,
        giftAppliedCents = giftAppliedCents,
        orderNumber = orderNumber,
        cardLastFour = cardLastFour,
        paidCents = 0,
    )
}

/** Demo seed cart matching Figma Place Order samples. */
val SelfServiceDemoCart: List<CartLine> = SampleCart
