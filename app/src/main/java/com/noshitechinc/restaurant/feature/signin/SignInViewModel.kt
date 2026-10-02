package com.noshitechinc.restaurant.feature.signin

import com.noshitechinc.restaurant.core.ui.BaseViewModel
import com.noshitechinc.restaurant.core.ui.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data object OpenSelectRole : UiEffect

@HiltViewModel
class SignInViewModel @Inject constructor() : BaseViewModel() {
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun onDigit(digit: Int) {
        val current = _uiState.value
        if (current.pin.length >= PIN_LENGTH) return
        val next = current.pin + digit
        _uiState.update { it.copy(pin = next, hasError = false) }
        if (next.length == PIN_LENGTH) sendEffect(OpenSelectRole)
    }

    fun onDelete() {
        _uiState.update { state ->
            state.copy(pin = state.pin.dropLast(1), hasError = false)
        }
    }

    private companion object {
        const val PIN_LENGTH = 4
    }
}
