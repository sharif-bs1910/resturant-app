package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun HoursPanel(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    PageTitle(stringResource(R.string.settings_hours))
    SectionLabel(stringResource(R.string.settings_store_hours))
    Text(stringResource(R.string.settings_store_hours_body), style = AppTheme.typography.bodySmall, color = AppTheme.colors.textMuted)
    SettingsCard {
        val groups = groupedHours(settings.storeDays)
        groups.forEachIndexed { index, group ->
            HoursSummaryRow(group, divider = index < groups.lastIndex) {
                onChange(settings.copy(dialog = SettingsDialog.EditStore, storeDraft = settings.storeDays))
            }
        }
    }
    SectionLabel(stringResource(R.string.settings_online_hours))
    Text(stringResource(R.string.settings_online_hours_body), style = AppTheme.typography.bodySmall, color = AppTheme.colors.textMuted)
    SettingsCard {
        if (settings.onlineMatchesStore) {
            StatusRow(
                stringResource(R.string.settings_pickup_delivery),
                value = stringResource(R.string.settings_same_as_store),
                connected = false,
                showDot = false,
                showPaired = false,
            ) {
                onChange(settings.copy(dialog = SettingsDialog.EditOnline, onlineDraft = settings.onlineDays, onlineDraftMatches = true))
            }
        } else {
            val groups = groupedHours(settings.onlineDays)
            groups.forEachIndexed { index, group ->
                HoursSummaryRow(group, divider = index < groups.lastIndex) {
                    onChange(
                        settings.copy(dialog = SettingsDialog.EditOnline, onlineDraft = settings.onlineDays, onlineDraftMatches = false),
                    )
                }
            }
        }
    }
    SectionLabel(stringResource(R.string.settings_special_hours))
    Text(stringResource(R.string.settings_special_hours_body), style = AppTheme.typography.bodySmall, color = AppTheme.colors.textMuted)
    SettingsCard {
        settings.specials.forEachIndexed { index, special ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding, vertical = AppTheme.spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                Text(
                    specialLabel(special),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    specialHoursValue(special),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textMuted,
                    maxLines = 1,
                    softWrap = false,
                )
                Text(
                    stringResource(R.string.settings_edit),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.clickable {
                        onChange(settings.copy(dialog = SettingsDialog.EditSpecial, specialDraft = special, pendingSpecialId = special.id))
                    },
                )
                Text(
                    stringResource(R.string.settings_remove),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.statusDanger,
                    modifier = Modifier.clickable {
                        onChange(settings.copy(dialog = SettingsDialog.RemoveSpecial, pendingSpecialId = special.id))
                    },
                )
            }
            if (index < settings.specials.lastIndex) HoursDivider()
        }
    }
    PillButton(stringResource(R.string.settings_add_special), {
        onChange(settings.copy(dialog = SettingsDialog.AddSpecial, specialDraft = BlankSpecial))
    }, filled = false)
}

@Composable
internal fun specialLabel(special: SpecialHour): String {
    val name = special.name.trim()
    return if (name.isEmpty()) special.label else "${special.label} · $name"
}

@Composable
private fun joinedDayLabels(group: List<DayHours>): String {
    val labels = ArrayList<String>(group.size)
    for (day in group) {
        labels.add(stringResource(day.shortRes))
    }
    return labels.joinToString()
}

@Composable
private fun specialHoursValue(special: SpecialHour): String = if (special.closed) {
    stringResource(R.string.settings_closed)
} else {
    "${displayTime(special.opens)} ${stringResource(R.string.settings_zone_range)} ${displayTime(special.closes)}"
}

private fun displayTime(value: String): String = value.replace(":00", "")

@Composable
private fun HoursSummaryRow(group: List<DayHours>, divider: Boolean, onClick: () -> Unit) {
    val day = group.first()
    val label = joinedDayLabels(group)
    val value = if (!day.open) {
        stringResource(R.string.settings_closed)
    } else {
        "${displayTime(day.opens)} ${stringResource(R.string.settings_zone_range)} ${displayTime(day.closes)}"
    }
    Column(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding, vertical = AppTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            Text(label, style = AppTheme.typography.titleSmall, color = AppTheme.colors.textPrimary, modifier = Modifier.weight(1f))
            Text(value, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted, maxLines = 1, softWrap = false)
            Text(stringResource(R.string.checkout_collapse), style = AppTheme.typography.titleSmall, color = AppTheme.colors.textMuted)
        }
        if (divider) HoursDivider()
    }
}
