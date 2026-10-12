package com.noshitechinc.restaurant.feature.checkout

import androidx.annotation.StringRes
import com.noshitechinc.restaurant.R

enum class ReportRangePreset {
    Today,
    Days7,
    Days30,
    ThisMonth,
    Custom,
}

data class ReportingState(
    val pickerOpen: Boolean = false,
    val preset: ReportRangePreset = ReportRangePreset.Days30,
    @param:StringRes val rangeLabelRes: Int = R.string.reporting_range_30d,
    @param:StringRes val compareRes: Int = R.string.reporting_range,
    val calendarMonth: Int = 7,
    val calendarYear: Int = 2026,
    val selectedStartDay: Int? = 12,
    val selectedEndDay: Int? = 18,
)

@StringRes
fun ReportRangePreset.chipRes(): Int = when (this) {
    ReportRangePreset.Today -> R.string.reporting_preset_today
    ReportRangePreset.Days7 -> R.string.reporting_preset_7d
    ReportRangePreset.Days30 -> R.string.reporting_preset_30d
    ReportRangePreset.ThisMonth -> R.string.reporting_preset_month
    ReportRangePreset.Custom -> R.string.reporting_preset_custom
}

fun ReportingState.openPicker(): ReportingState = copy(pickerOpen = true)

fun ReportingState.dismissPicker(): ReportingState = copy(pickerOpen = false)

fun ReportingState.selectPreset(preset: ReportRangePreset): ReportingState {
    val (range, compare) = when (preset) {
        ReportRangePreset.Today ->
            R.string.reporting_range_today to R.string.reporting_compare_today

        ReportRangePreset.Days7 ->
            R.string.reporting_range_7d to R.string.reporting_compare_7d

        ReportRangePreset.Days30 ->
            R.string.reporting_range_30d to R.string.reporting_range

        ReportRangePreset.ThisMonth ->
            R.string.reporting_range_month to R.string.reporting_compare_month

        ReportRangePreset.Custom ->
            R.string.reporting_range_custom to R.string.reporting_compare_custom
    }
    return copy(
        preset = preset,
        rangeLabelRes = range,
        compareRes = compare,
        pickerOpen = preset == ReportRangePreset.Custom,
        selectedStartDay = if (preset == ReportRangePreset.Days7) 12 else selectedStartDay,
        selectedEndDay = if (preset == ReportRangePreset.Days7) 18 else selectedEndDay,
    )
}

fun ReportingState.shiftMonth(delta: Int): ReportingState {
    var month = calendarMonth + delta
    var year = calendarYear
    when {
        month < 1 -> {
            month = 12
            year -= 1
        }

        month > 12 -> {
            month = 1
            year += 1
        }
    }
    return copy(calendarMonth = month, calendarYear = year)
}

fun ReportingState.selectDay(day: Int): ReportingState {
    val start = selectedStartDay
    val end = selectedEndDay
    return when {
        start == null || end != null ->
            copy(
                preset = ReportRangePreset.Custom,
                selectedStartDay = day,
                selectedEndDay = null,
                rangeLabelRes = R.string.reporting_range_custom,
                compareRes = R.string.reporting_compare_custom,
            )

        day < start ->
            copy(
                preset = ReportRangePreset.Custom,
                selectedStartDay = day,
                selectedEndDay = start,
                rangeLabelRes = R.string.reporting_range_custom,
                compareRes = R.string.reporting_compare_custom,
                pickerOpen = false,
            )

        else ->
            copy(
                preset = ReportRangePreset.Custom,
                selectedStartDay = start,
                selectedEndDay = day,
                rangeLabelRes = R.string.reporting_range_custom,
                compareRes = R.string.reporting_compare_custom,
                pickerOpen = false,
            )
    }
}
