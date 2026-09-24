package com.noshitechinc.restaurant.feature.home

import com.noshitechinc.restaurant.core.common.AppEnvironment
import com.noshitechinc.restaurant.core.common.AppInfo
import com.noshitechinc.restaurant.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class HomeViewModel @Inject constructor(appInfo: AppInfo) : BaseViewModel() {
    private val _uiState = MutableStateFlow(
        HomeUiState(
            environment = appInfo.environment.takeUnless { it == AppEnvironment.Prod },
            versionName = appInfo.versionName,
        ),
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
