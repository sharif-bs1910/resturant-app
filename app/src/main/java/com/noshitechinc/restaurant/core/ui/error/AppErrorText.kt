package com.noshitechinc.restaurant.core.ui.error

import androidx.annotation.StringRes
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.common.UiText

fun AppError.toUiText(): UiText = when (this) {
    AppError.NoInternet -> UiText.Resource(R.string.error_no_internet)
    AppError.Timeout -> UiText.Resource(R.string.error_timeout)
    AppError.ServiceUnavailable -> UiText.Resource(R.string.error_service_unavailable)
    AppError.SessionExpired -> UiText.Resource(R.string.error_session_expired)
    AppError.Forbidden -> UiText.Resource(R.string.error_forbidden)
    AppError.NotFound -> UiText.Resource(R.string.error_not_found)
    is AppError.Validation -> message?.let { UiText.Dynamic(it) } ?: UiText.Resource(R.string.error_validation)
    is AppError.Server -> message?.let { UiText.Dynamic(it) } ?: UiText.Resource(R.string.error_server)
    is AppError.Unknown -> UiText.Resource(R.string.error_unknown)
}

@StringRes
fun AppError.titleRes(): Int = when (this) {
    AppError.NoInternet -> R.string.error_title_offline
    AppError.ServiceUnavailable -> R.string.error_title_unavailable
    else -> R.string.error_title_generic
}
