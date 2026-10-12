package com.noshitechinc.restaurant.feature.pairing

import com.noshitechinc.restaurant.core.ui.BaseViewModel
import com.noshitechinc.restaurant.core.ui.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data object OpenSignIn : UiEffect

@HiltViewModel
class PairingViewModel @Inject constructor() : BaseViewModel() {
    private val _uiState = MutableStateFlow(PairingUiState())
    val uiState: StateFlow<PairingUiState> = _uiState.asStateFlow()

    fun onDigit(digit: Int) {
        val current = _uiState.value
        if (current.code.length >= CODE_LENGTH) return
        val next = current.code + digit
        _uiState.update { it.copy(code = next, hasError = false) }
        if (next.length == CODE_LENGTH) sendEffect(OpenSignIn)
    }

    fun onDelete() {
        _uiState.update { state ->
            state.copy(code = state.code.dropLast(1), hasError = false)
        }
    }

    private companion object {
        const val CODE_LENGTH = 6
    }
}
