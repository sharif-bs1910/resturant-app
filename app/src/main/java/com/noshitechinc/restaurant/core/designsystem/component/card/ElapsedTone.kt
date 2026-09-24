package com.noshitechinc.restaurant.core.designsystem.component.card

import com.noshitechinc.restaurant.core.designsystem.theme.StatusTone

enum class ElapsedTone {
    OnTime,
    Warning,
    Overdue,
    ;

    val statusTone: StatusTone
        get() = when (this) {
            OnTime -> StatusTone.Neutral
            Warning -> StatusTone.Warning
            Overdue -> StatusTone.Danger
        }

    companion object {
        fun of(elapsedMinutes: Int, warningAfterMinutes: Int, overdueAfterMinutes: Int): ElapsedTone = when {
            elapsedMinutes >= overdueAfterMinutes -> Overdue
            elapsedMinutes >= warningAfterMinutes -> Warning
            else -> OnTime
        }
    }
}
