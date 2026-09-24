package com.noshitechinc.restaurant.core.common.model

data class TimeOfDay(val hour: Int, val minute: Int) {
    init {
        require(hour in 0..MAX_HOUR) { "hour out of range: $hour" }
        require(minute in 0..MAX_MINUTE) { "minute out of range: $minute" }
    }

    private companion object {
        const val MAX_HOUR = 23
        const val MAX_MINUTE = 59
    }
}
