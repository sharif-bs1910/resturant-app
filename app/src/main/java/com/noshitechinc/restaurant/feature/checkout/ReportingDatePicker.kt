package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ReportingDatePicker(reporting: ReportingState, onReporting: (ReportingState) -> Unit, modifier: Modifier = Modifier) {
    val dayCellHeight = AppTheme.sizes.iconLg + AppTheme.spacing.xs
    val dayPill = AppTheme.sizes.iconMd + AppTheme.spacing.sm
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Column(
        modifier
            .width(AppTheme.sizes.checkoutDetailWidth)
            .shadow(elevation = AppTheme.spacing.sm, shape = shape)
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.12f), shape)
            .padding(AppTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(
                    R.string.reporting_picker_month_year,
                    stringResource(monthRes(reporting.calendarMonth)),
                    reporting.calendarYear,
                ),
                style = AppTheme.typography.labelLarge,
                color = AppTheme.colors.textPrimary,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                PickerNavButton("‹") { onReporting(reporting.shiftMonth(-1)) }
                PickerNavButton("›") { onReporting(reporting.shiftMonth(1)) }
            }
        }
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            ReportRangePreset.entries.forEach { preset ->
                val selected = reporting.preset == preset
                val chipShape = RoundedCornerShape(AppTheme.radius.pill)
                Box(
                    Modifier
                        .clip(chipShape)
                        .background(if (selected) AppTheme.colors.primary else AppTheme.colors.card)
                        .then(
                            if (selected) {
                                Modifier
                            } else {
                                Modifier.border(
                                    AppTheme.border.thin,
                                    AppTheme.colors.textPrimary.copy(alpha = 0.12f),
                                    chipShape,
                                )
                            },
                        )
                        .clickable { onReporting(reporting.selectPreset(preset)) }
                        .padding(
                            horizontal = AppTheme.spacing.sm + AppTheme.spacing.xxs,
                            vertical = AppTheme.spacing.xs + AppTheme.spacing.xxs,
                        ),
                ) {
                    Text(
                        stringResource(preset.chipRes()),
                        style = AppTheme.typography.labelSmall,
                        color = if (selected) {
                            AppTheme.colors.card
                        } else {
                            AppTheme.colors.textPrimary.copy(alpha = 0.8f)
                        },
                        maxLines = 1,
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs)) {
            Row(Modifier.fillMaxWidth()) {
                listOf(
                    R.string.reporting_dow_s,
                    R.string.reporting_dow_m,
                    R.string.reporting_dow_t,
                    R.string.reporting_dow_w,
                    R.string.reporting_dow_t,
                    R.string.reporting_dow_f,
                    R.string.reporting_dow_s,
                ).forEach { day ->
                    Text(
                        stringResource(day),
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colors.textPrimary.copy(alpha = 0.45f),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            val cells = calendarCells(reporting.calendarYear, reporting.calendarMonth)
            cells.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        DayCell(
                            day = day,
                            start = reporting.selectedStartDay,
                            end = reporting.selectedEndDay,
                            cellHeight = dayCellHeight,
                            pill = dayPill,
                            modifier = Modifier.weight(1f),
                            onClick = { onReporting(reporting.selectDay(it)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerNavButton(label: String, onClick: () -> Unit) {
    val shape = CircleShape
    Box(
        Modifier
            .size(AppTheme.sizes.iconLg)
            .clip(shape)
            .background(AppTheme.colors.background)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.12f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = AppTheme.typography.titleMedium, color = AppTheme.colors.textPrimary)
    }
}

@Composable
private fun DayCell(day: Int?, start: Int?, end: Int?, cellHeight: Dp, pill: Dp, onClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    val inRange = day != null && dayInRange(day, start, end)
    val isEdge = day != null && (day == start || day == end)
    Box(
        modifier.height(cellHeight),
        contentAlignment = Alignment.Center,
    ) {
        if (day != null) {
            if (inRange && !isEdge) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(pill)
                        .background(AppTheme.colors.primary.copy(alpha = 0.12f)),
                )
            }
            Box(
                Modifier
                    .size(pill)
                    .clip(CircleShape)
                    .background(if (isEdge) AppTheme.colors.primary else Color.Transparent)
                    .clickable { onClick(day) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    day.toString(),
                    style = AppTheme.typography.labelMedium,
                    color = if (isEdge) AppTheme.colors.card else AppTheme.colors.textPrimary,
                )
            }
        }
    }
}

private fun monthRes(month: Int): Int = when (month) {
    1 -> R.string.reporting_month_jan
    2 -> R.string.reporting_month_feb
    3 -> R.string.reporting_month_mar
    4 -> R.string.reporting_month_apr
    5 -> R.string.reporting_month_may
    6 -> R.string.reporting_month_jun
    7 -> R.string.reporting_month_jul
    8 -> R.string.reporting_month_aug
    9 -> R.string.reporting_month_sep
    10 -> R.string.reporting_month_oct
    11 -> R.string.reporting_month_nov
    else -> R.string.reporting_month_dec
}

private fun calendarCells(year: Int, month: Int): List<Int?> {
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val firstWeekday = cal.get(Calendar.DAY_OF_WEEK)
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leading = firstWeekday - 1
    return buildList {
        repeat(leading) { add(null) }
        for (day in 1..daysInMonth) add(day)
        while (size % 7 != 0) add(null)
    }
}

private fun dayInRange(day: Int, start: Int?, end: Int?): Boolean {
    if (start == null) return false
    if (end == null) return day == start
    val lo = minOf(start, end)
    val hi = maxOf(start, end)
    return day in lo..hi
}
