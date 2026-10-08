package com.noshitechinc.restaurant.feature.role

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
class SelectRoleViewModel @Inject constructor() : BaseViewModel() {
    private val _uiState = MutableStateFlow(SelectRoleUiState())
    val uiState: StateFlow<SelectRoleUiState> = _uiState.asStateFlow()

    fun onSelect(role: DeviceRole) {
        _uiState.update { it.copy(selected = role) }
    }

    fun onContinue() {
        if (_uiState.value.selected != null) sendEffect(OpenSignIn)
    }
}
