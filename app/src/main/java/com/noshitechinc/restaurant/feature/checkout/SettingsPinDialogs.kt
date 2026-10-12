package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.quantity.KeypadKey
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun PinDialog(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    SettingsDialogCard {
        Text(
            stringResource(R.string.settings_clock_title),
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            stringResource(R.string.settings_clock_body),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            Modifier.align(Alignment.CenterHorizontally).widthIn(max = AppTheme.sizes.keypadMaxWidth),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            repeat(PIN_LENGTH) { index ->
                val filled = index < settings.pin.length
                val active = index == settings.pin.length.coerceAtMost(PIN_LENGTH - 1) && settings.pin.length < PIN_LENGTH
                val shape = RoundedCornerShape(AppTheme.radius.md)
                Box(
                    Modifier
                        .size(AppTheme.sizes.passcodeCellWidth)
                        .clip(shape)
                        .background(AppTheme.colors.card)
                        .border(
                            if (active) AppTheme.border.medium else AppTheme.border.thin,
                            if (active) AppTheme.colors.primary else AppTheme.colors.outline,
                            shape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (filled) {
                        Text(
                            stringResource(R.string.settings_pin_dot),
                            style = AppTheme.typography.titleLarge,
                            color = AppTheme.colors.textPrimary,
                        )
                    }
                }
            }
        }
        PinKeypad(onKey = { onChange(settings.afterPinKey(it)) }, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun PinKeypad(onKey: (KeypadKey) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.widthIn(max = AppTheme.sizes.keypadMaxWidth),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9)).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                row.forEach { digit -> PinKey(digit.toString()) { onKey(KeypadKey.Digit(digit)) } }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Spacer(Modifier.weight(1f))
            PinKey(stringResource(R.string.keypad_zero)) { onKey(KeypadKey.Digit(0)) }
            PinKey(icon = true) { onKey(KeypadKey.Backspace) }
        }
    }
}

@Composable
private fun RowScope.PinKey(label: String = "", icon: Boolean = false, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Box(
        Modifier
            .weight(1f)
            .heightIn(min = AppTheme.sizes.keypadKey)
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.outline, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (icon) {
            Icon(
                Icons.AutoMirrored.Filled.Backspace,
                contentDescription = stringResource(R.string.a11y_backspace),
                tint = AppTheme.colors.textMuted,
            )
        } else {
            Text(label, style = AppTheme.typography.titleLarge, color = AppTheme.colors.textPrimary)
        }
    }
}

@Composable
internal fun ClockResultDialog(settings: SettingsState, clockedOut: Boolean, onChange: (SettingsState) -> Unit) {
    val person = settings.staff.firstOrNull { it.id == settings.clockStaffId } ?: return
    SettingsDialogCard {
        Text(
            stringResource(if (clockedOut) R.string.settings_clocked_out else R.string.settings_clocked_in),
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.textPrimary,
        )
        Recap(stringResource(R.string.settings_staff), person.name)
        Recap(stringResource(R.string.settings_clocked_in), person.resultIn)
        if (clockedOut) {
            Recap(stringResource(R.string.settings_clocked_out), person.resultOut)
            Recap(stringResource(R.string.settings_today), person.today)
        } else {
            Recap(stringResource(R.string.settings_last_shift_label), person.lastShift)
        }
        Recap(stringResource(R.string.settings_week), person.week)
        PillButton(stringResource(R.string.checkout_dialog_cancel), {
            onChange(settings.copy(dialog = SettingsDialog.None))
        }, filled = false)
    }
}

@Composable
internal fun StaffDialog(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    val person = settings.staff.firstOrNull { it.id == settings.clockStaffId } ?: return
    SettingsDialogCard {
        Text(
            person.name,
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            if (person.onShift) {
                stringResource(R.string.settings_on_shift_detail, person.since)
            } else {
                stringResource(R.string.settings_off_shift_detail, person.lastShift)
            },
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.radius.md))
                .background(AppTheme.colors.surfaceVariant)
                .padding(AppTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Recap(stringResource(R.string.settings_clocked_in), person.resultIn)
            Recap(stringResource(R.string.settings_elapsed), person.elapsed)
            Recap(stringResource(R.string.settings_week), person.week)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            PillButton(stringResource(R.string.settings_adjust_time), {}, Modifier.weight(1f), filled = false)
            PillButton(stringResource(R.string.settings_view_timesheet), {}, Modifier.weight(1f), filled = false)
        }
        PillButton(
            stringResource(if (person.onShift) R.string.settings_clock_out else R.string.settings_clock_in_action),
            { onChange(settings.withStaffClocked()) },
            Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun Recap(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
        Text(value, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textPrimary)
    }
}
