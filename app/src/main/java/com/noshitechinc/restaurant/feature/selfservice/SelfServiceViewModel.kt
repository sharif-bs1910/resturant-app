package com.noshitechinc.restaurant.feature.selfservice

import androidx.lifecycle.viewModelScope
import com.noshitechinc.restaurant.core.ui.BaseViewModel
import com.noshitechinc.restaurant.feature.checkout.CartLine
import com.noshitechinc.restaurant.feature.checkout.GiftCardBalanceCents
import com.noshitechinc.restaurant.feature.checkout.GiftField
import com.noshitechinc.restaurant.feature.checkout.MenuItem
import com.noshitechinc.restaurant.feature.checkout.ModifierDraft
import com.noshitechinc.restaurant.feature.checkout.ModifierGroup
import com.noshitechinc.restaurant.feature.checkout.defaultModifiers
import com.noshitechinc.restaurant.feature.checkout.unitPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SelfServiceViewModel @Inject constructor() : BaseViewModel() {
    private val _uiState = MutableStateFlow(SelfServiceUiState())
    val uiState: StateFlow<SelfServiceUiState> = _uiState.asStateFlow()
    private var lineSerial = 0
    private var paymentJob: Job? = null

    fun onSearch(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun onMeal(meal: SelfServiceMeal) {
        _uiState.update { state ->
            state.copy(
                meal = meal,
                category = if (meal == SelfServiceMeal.Drinks) {
                    SelfServiceCategory.Drinks
                } else if (state.category == SelfServiceCategory.Drinks) {
                    SelfServiceCategory.Pizzas
                } else {
                    state.category
                },
            )
        }
    }

    fun onCategory(category: SelfServiceCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onOrderType(orderType: SelfServiceOrderType) {
        _uiState.update { it.copy(orderType = orderType) }
    }

    fun onOpenItem(item: MenuItem) {
        _uiState.update {
            it.copy(
                overlay = SelfServiceOverlay.Modifiers,
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
                id = "ss-line-$lineSerial",
                name = draft.item.name,
                unitCents = unitPrice(draft.item, draft.selected),
                quantity = draft.quantity,
                selectedModifierIds = draft.selected,
                note = draft.note.trim(),
            )
            state.copy(lines = state.lines + line, overlay = SelfServiceOverlay.None, draft = null)
        }
    }

    fun onDismissOverlay() {
        _uiState.update { it.copy(overlay = SelfServiceOverlay.None, draft = null) }
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

    fun onPlaceOrder() {
        _uiState.update { state ->
            if (state.lines.isEmpty()) {
                state
            } else {
                state.copy(step = SelfServiceStep.Guest, overlay = SelfServiceOverlay.None)
            }
        }
    }

    fun onGuestPhone(digits: String) {
        _uiState.update { it.copy(guestPhoneDigits = digits.filter(Char::isDigit).take(10)) }
    }

    fun onGuestName(value: String) {
        _uiState.update { it.copy(guestName = value) }
    }

    fun onTableNumber(value: String) {
        _uiState.update { it.copy(tableNumber = value.filter { ch -> ch.isDigit() || ch.isLetter() }.take(8)) }
    }

    fun onGuestContinue() {
        _uiState.update { state ->
            if (!state.guestReady) {
                state
            } else {
                state.copy(step = SelfServiceStep.Review)
            }
        }
    }

    fun onBackToMenu() {
        cancelPayment()
        _uiState.update { it.copy(step = SelfServiceStep.Menu, overlay = SelfServiceOverlay.None) }
    }

    fun onBackToGuest() {
        _uiState.update { it.copy(step = SelfServiceStep.Guest, overlay = SelfServiceOverlay.None) }
    }

    fun onToggleLineExpanded(lineId: String) {
        _uiState.update { state ->
            val next = if (lineId in state.expandedLineIds) {
                state.expandedLineIds - lineId
            } else {
                state.expandedLineIds + lineId
            }
            state.copy(expandedLineIds = next)
        }
    }

    fun onRedeemGiftCard() {
        _uiState.update {
            it.copy(
                overlay = SelfServiceOverlay.GiftCard,
                giftNumber = it.giftNumber.ifBlank { "8043 2210 4821" },
                giftPin = it.giftPin.ifBlank { "4821" },
                giftField = GiftField.Number,
            )
        }
    }

    fun onGiftField(field: GiftField) {
        _uiState.update { it.copy(giftField = field) }
    }

    fun onGiftDigit(digit: String) {
        _uiState.update { state ->
            when (state.giftField) {
                GiftField.Number -> state.copy(giftNumber = (state.giftNumber + digit).take(19))
                GiftField.Pin -> state.copy(giftPin = (state.giftPin + digit).take(4))
            }
        }
    }

    fun onGiftDelete() {
        _uiState.update { state ->
            when (state.giftField) {
                GiftField.Number -> state.copy(giftNumber = state.giftNumber.dropLast(1))
                GiftField.Pin -> state.copy(giftPin = state.giftPin.dropLast(1))
            }
        }
    }

    fun onContinueGiftCard() {
        _uiState.update {
            it.copy(overlay = SelfServiceOverlay.GiftAmount, giftApplyDigits = "")
        }
    }

    fun onGiftAmountDigits(digits: String) {
        _uiState.update { it.copy(giftApplyDigits = digits.filter(Char::isDigit).take(8)) }
    }

    fun onGiftAmountDelete() {
        _uiState.update { it.copy(giftApplyDigits = it.giftApplyDigits.dropLast(1)) }
    }

    fun onBackFromGift() {
        _uiState.update { state ->
            when (state.overlay) {
                SelfServiceOverlay.GiftAmount -> state.copy(overlay = SelfServiceOverlay.GiftCard)
                else -> state.copy(overlay = SelfServiceOverlay.None)
            }
        }
    }

    fun onApplyGiftCard() {
        _uiState.update { state ->
            val maxApply = minOf(GiftCardBalanceCents, state.dueCents.coerceAtLeast(1))
            val apply = state.giftApplyCents.coerceIn(0, maxApply)
            if (apply <= 0) {
                state
            } else {
                state.copy(
                    giftAppliedCents = state.giftAppliedCents + apply,
                    giftApplyDigits = "",
                    overlay = SelfServiceOverlay.None,
                )
            }
        }
    }

    fun onMakePayment() {
        _uiState.update { state ->
            if (state.dueCents <= 0) {
                state.copy(step = SelfServiceStep.Success, overlay = SelfServiceOverlay.None)
            } else {
                state.copy(step = SelfServiceStep.PaymentWaiting, overlay = SelfServiceOverlay.None)
            }
        }
        if (_uiState.value.step == SelfServiceStep.PaymentWaiting) {
            startPaymentSequence(decline = false)
        }
    }

    fun onCancelPayment() {
        cancelPayment()
        _uiState.update { it.copy(step = SelfServiceStep.Review, overlay = SelfServiceOverlay.None) }
    }

    fun onTryPaymentAgain() {
        _uiState.update { it.copy(step = SelfServiceStep.PaymentWaiting) }
        startPaymentSequence(decline = false)
    }

    fun onBackToOrderFromDecline() {
        cancelPayment()
        _uiState.update { it.copy(step = SelfServiceStep.Review) }
    }

    /** Demo path: force a declined card after the wait/process delay. */
    fun onSimulateDecline() {
        _uiState.update { it.copy(step = SelfServiceStep.PaymentWaiting, overlay = SelfServiceOverlay.None) }
        startPaymentSequence(decline = true)
    }

    fun onNewOrder() {
        cancelPayment()
        lineSerial = 0
        _uiState.value = SelfServiceUiState(
            orderNumber = ((_uiState.value.orderNumber.toIntOrNull() ?: 1042) + 1).toString(),
            tokenNumber = ((_uiState.value.tokenNumber.toIntOrNull() ?: 1042) + 1).toString(),
        )
    }

    private fun startPaymentSequence(decline: Boolean) {
        cancelPayment()
        paymentJob = viewModelScope.launch {
            delay(WaitingDelayMs)
            if (_uiState.value.step != SelfServiceStep.PaymentWaiting) return@launch
            _uiState.update { it.copy(step = SelfServiceStep.PaymentProcessing) }
            delay(ProcessingDelayMs)
            if (_uiState.value.step != SelfServiceStep.PaymentProcessing) return@launch
            _uiState.update { state ->
                if (decline) {
                    state.copy(step = SelfServiceStep.PaymentDeclined)
                } else {
                    state.copy(step = SelfServiceStep.Success)
                }
            }
        }
    }

    private fun cancelPayment() {
        paymentJob?.cancel()
        paymentJob = null
    }

    private companion object {
        const val WaitingDelayMs = 1_600L
        const val ProcessingDelayMs = 1_400L
    }
}
