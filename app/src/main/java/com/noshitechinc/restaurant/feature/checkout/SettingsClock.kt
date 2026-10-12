package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R

@Composable
internal fun ClockPanel(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    val onShift = settings.staff.filter { it.onShift }
    val offShift = settings.staff.filter { !it.onShift }
    PageTitle(stringResource(R.string.settings_clock))
    SectionLabel(stringResource(R.string.settings_on_shift, onShift.size))
    SettingsCard {
        onShift.forEachIndexed { index, person ->
            StaffRow(person, settings, onChange)
            if (index < onShift.lastIndex) HoursDivider()
        }
    }
    SectionLabel(stringResource(R.string.settings_off_shift))
    SettingsCard {
        offShift.forEachIndexed { index, person ->
            StaffRow(person, settings, onChange)
            if (index < offShift.lastIndex) HoursDivider()
        }
    }
    PillButton(stringResource(R.string.settings_clock_button), { onChange(settings.copy(dialog = SettingsDialog.Pin, pin = "")) })
}

@Composable
private fun StaffRow(person: StaffShift, settings: SettingsState, onChange: (SettingsState) -> Unit) {
    StatusRow(
        title = person.name,
        subtitle = if (person.onShift) {
            stringResource(R.string.settings_clocked_in_at, person.since)
        } else {
            stringResource(R.string.settings_last_shift, person.lastShift)
        },
        value = if (person.onShift) person.listValue else stringResource(R.string.settings_dash),
        connected = person.onShift,
        showPaired = false,
        badge = stringResource(R.string.checkout_paid),
    ) { onChange(settings.copy(dialog = SettingsDialog.Staff, clockStaffId = person.id)) }
}
