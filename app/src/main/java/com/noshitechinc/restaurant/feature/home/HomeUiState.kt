package com.noshitechinc.restaurant.feature.home

import com.noshitechinc.restaurant.core.common.AppEnvironment

data class HomeUiState(val environment: AppEnvironment? = null, val versionName: String = "")
