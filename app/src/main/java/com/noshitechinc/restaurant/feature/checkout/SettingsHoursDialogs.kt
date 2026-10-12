package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
private fun InlineAction(label: String, color: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = AppTheme.typography.labelMedium, color = color, maxLines = 1, softWrap = false)
    }
}

@Composable
internal fun HoursDialog(settings: SettingsState, online: Boolean, onChange: (SettingsState) -> Unit) {
    val days = if (online) settings.onlineDraft else settings.storeDraft
    val timeWidth = AppTheme.sizes.passcodeKeyWidth + AppTheme.sizes.minTouchTarget
    SettingsDialogCard(wide = true) {
        Text(
            stringResource(if (online) R.string.settings_online_title else R.string.settings_store_title),
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.textPrimary,
        )
        Text(
            stringResource(if (online) R.string.settings_online_dialog_body else R.string.settings_store_dialog_body),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textMuted,
        )
        if (online) {
            SwitchLine(stringResource(R.string.settings_same_as_store), settings.onlineDraftMatches) { checked ->
                onChange(
                    settings.copy(onlineDraftMatches = checked, onlineDraft = if (checked) settings.storeDays else settings.onlineDays),
                )
            }
            HoursDivider()
        }
        days.forEach { day ->
            Row(
                Modifier.fillMaxWidth().heightIn(min = AppTheme.sizes.minTouchTarget),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                Text(
                    stringResource(day.fullRes),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(timeWidth),
                )
                SettingsToggle(day.open) { open ->
                    onChange(
                        updateDraft(settings, online) { current ->
                            current.map { if (it.fullRes == day.fullRes) it.copy(open = open) else it }
                        },
                    )
                }
                DialogField(
                    day.opens,
                    enabled = day.open,
                    modifier = Modifier.width(timeWidth),
                    onValue = { value ->
                        onChange(
                            updateDraft(settings, online) { current ->
                                current.map { if (it.fullRes == day.fullRes) it.copy(opens = value) else it }
                            },
                        )
                    },
                )
                Text(
                    stringResource(R.string.settings_zone_range),
                    color = AppTheme.colors.textMuted,
                    style = AppTheme.typography.bodyMedium,
                )
                DialogField(
                    day.closes,
                    enabled = day.open,
                    modifier = Modifier.width(timeWidth),
                    onValue = { value ->
                        onChange(
                            updateDraft(settings, online) { current ->
                                current.map { if (it.fullRes == day.fullRes) it.copy(closes = value) else it }
                            },
                        )
                    },
                )
            }
            HoursDivider()
        }
        DialogActions(stringResource(R.string.menu_save), {
            onChange(settings.copy(dialog = SettingsDialog.None))
        }) {
            if (online) {
                onChange(
                    settings.copy(
                        onlineDays = if (settings.onlineDraftMatches) settings.storeDays else settings.onlineDraft,
                        onlineMatchesStore = settings.onlineDraftMatches,
                        dialog = SettingsDialog.None,
                    ),
                )
            } else {
                onChange(settings.copy(storeDays = settings.storeDraft, dialog = SettingsDialog.None))
            }
        }
    }
}

@Composable
private fun SwitchLine(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = AppTheme.typography.titleSmall, color = AppTheme.colors.textPrimary, modifier = Modifier.weight(1f))
        SettingsToggle(checked, onChecked)
    }
}

private fun updateDraft(settings: SettingsState, online: Boolean, block: (List<DayHours>) -> List<DayHours>): SettingsState =
    if (online) settings.copy(onlineDraft = block(settings.onlineDraft)) else settings.copy(storeDraft = block(settings.storeDraft))

@Composable
internal fun SpecialDialog(settings: SettingsState, editing: Boolean, onChange: (SettingsState) -> Unit) {
    val draft = settings.specialDraft
    val timeWidth = AppTheme.sizes.passcodeKeyWidth + AppTheme.sizes.minTouchTarget
    SettingsDialogCard {
        Text(
            stringResource(if (editing) R.string.settings_edit_special else R.string.settings_add_special),
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.textPrimary,
        )
        Text(stringResource(R.string.settings_add_special_body), style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
        DialogLabeledField(stringResource(R.string.settings_date), draft.date) {
            onChange(settings.copy(specialDraft = draft.copy(date = it)))
        }
        DialogLabeledField(stringResource(R.string.settings_name_optional), draft.name) {
            onChange(settings.copy(specialDraft = draft.copy(name = it)))
        }
        SwitchLine(stringResource(R.string.settings_closed_all_day), draft.closed) {
            onChange(settings.copy(specialDraft = draft.copy(closed = it)))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            DialogLabeledField(
                stringResource(R.string.settings_opens),
                draft.opens,
                enabled = !draft.closed,
                fill = false,
                modifier = Modifier.width(timeWidth),
            ) { onChange(settings.copy(specialDraft = draft.copy(opens = it))) }
            DialogLabeledField(
                stringResource(R.string.settings_closes),
                draft.closes,
                enabled = !draft.closed,
                fill = false,
                modifier = Modifier.width(timeWidth),
            ) { onChange(settings.copy(specialDraft = draft.copy(closes = it))) }
        }
        if (editing) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                InlineAction(stringResource(R.string.settings_remove), AppTheme.colors.statusDanger) {
                    onChange(settings.copy(dialog = SettingsDialog.RemoveSpecial))
                }
                Spacer(Modifier.weight(1f))
                DialogActions(stringResource(R.string.menu_save), {
                    onChange(settings.copy(dialog = SettingsDialog.None))
                }, fill = false) { onChange(saveSpecial(settings, editing)) }
            }
        } else {
            DialogActions(
                stringResource(R.string.settings_add_special),
                onCancel = { onChange(settings.copy(dialog = SettingsDialog.None)) },
                onConfirm = { onChange(saveSpecial(settings, editing)) },
                centered = true,
            )
        }
    }
}

private fun saveSpecial(settings: SettingsState, editing: Boolean): SettingsState {
    val draft = settings.specialDraft
    val next = if (editing) {
        settings.specials.map { if (it.id == draft.id) draft else it }
    } else {
        settings.specials + draft.copy(id = "special-${settings.specials.size}", label = draft.date)
    }
    return settings.copy(specials = next, dialog = SettingsDialog.None)
}

@Composable
internal fun RemoveSpecialDialog(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    val special = settings.specials.firstOrNull { it.id == settings.pendingSpecialId } ?: settings.specialDraft
    SettingsDialogCard {
        Text(stringResource(R.string.settings_remove_special), style = AppTheme.typography.titleLarge, color = AppTheme.colors.textPrimary)
        Text(
            stringResource(R.string.settings_remove_special_body, specialLabel(special)),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textMuted,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(stringResource(R.string.checkout_dialog_cancel), {
                onChange(settings.copy(dialog = SettingsDialog.None))
            }, Modifier.weight(1f), filled = false)
            PillButton(stringResource(R.string.settings_remove), {
                onChange(
                    settings.copy(
                        specials = settings.specials.filter {
                            it.id != special.id
                        },
                        dialog = SettingsDialog.None,
                        pendingSpecialId = null,
                    ),
                )
            }, Modifier.weight(1f), destructive = true)
        }
    }
}
