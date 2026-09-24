package com.noshitechinc.restaurant.core.common

import androidx.annotation.StringRes

sealed interface UiText {
    data class Resource(@StringRes val id: Int, val args: List<Any> = emptyList()) : UiText
    data class Dynamic(val value: String) : UiText
}
