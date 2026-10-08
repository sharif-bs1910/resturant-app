package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun DeliveryPanel(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    PageTitle(stringResource(R.string.settings_delivery))
    SectionLabel(stringResource(R.string.settings_delivery_area))
    SettingsCard {
        DeliveryValueRow(stringResource(R.string.settings_radius), settings.radius, {
            onChange(settings.copy(radius = it))
        }, suffix = stringResource(R.string.settings_mi))
        DeliveryValueRow(stringResource(R.string.settings_minimum), settings.minimumOrder, {
            onChange(settings.copy(minimumOrder = it))
        }, prefix = stringResource(R.string.settings_dollar))
        DeliveryValueRow(stringResource(R.string.settings_fee), settings.deliveryFee, {
            onChange(settings.copy(deliveryFee = it))
        }, prefix = stringResource(R.string.settings_dollar), divider = false)
    }
    SectionLabel(stringResource(R.string.settings_zones))
    SettingsCard {
        settings.zones.forEachIndexed { index, zone ->
            ZoneRow(
                zone = zone,
                divider = index < settings.zones.lastIndex,
                onZone = { updated ->
                    onChange(settings.copy(zones = settings.zones.map { if (it.id == zone.id) updated else it }))
                },
                onRemove = { onChange(settings.copy(dialog = SettingsDialog.RemoveZone, pendingZoneId = zone.id)) },
            )
        }
    }
    PillButton(stringResource(R.string.settings_add_zone), { onChange(settings.withAddedZone()) }, filled = false)
}

@Composable
private fun ZoneRow(zone: DeliveryZone, divider: Boolean, onZone: (DeliveryZone) -> Unit, onRemove: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding, vertical = AppTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        ) {
            Text(zone.name, style = AppTheme.typography.titleSmall, color = AppTheme.colors.textPrimary, modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                InlineValueField(zone.fromMi, { onZone(zone.copy(fromMi = it)) }, Modifier.width(AppTheme.sizes.passcodeCellWidth))
                Text(
                    stringResource(R.string.settings_zone_range),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textMuted,
                )
                InlineValueField(zone.toMi, { onZone(zone.copy(toMi = it)) }, Modifier.width(AppTheme.sizes.passcodeCellWidth))
                Text(stringResource(R.string.settings_mi), style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                Text(stringResource(R.string.settings_dollar), style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
                InlineValueField(zone.fee, { onZone(zone.copy(fee = it)) }, Modifier.width(AppTheme.sizes.passcodeKeyWidth))
            }
            Text(
                stringResource(R.string.settings_remove),
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.statusDanger,
                modifier = Modifier.clickable(onClick = onRemove),
            )
        }
        if (divider) HoursDivider()
    }
}

@Composable
internal fun RemoveZoneDialog(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    val zone = settings.zones.firstOrNull { it.id == settings.pendingZoneId } ?: return
    SettingsDialogCard {
        Text(
            stringResource(R.string.settings_remove_zone, zone.name),
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colors.textPrimary,
        )
        Text(
            stringResource(R.string.settings_remove_zone_body, zone.fromMi, zone.toMi, zone.fee),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textMuted,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(stringResource(R.string.checkout_dialog_cancel), {
                onChange(settings.copy(dialog = SettingsDialog.None))
            }, Modifier.weight(1f), filled = false)
            PillButton(stringResource(R.string.settings_remove), {
                onChange(settings.withoutPendingZone())
            }, Modifier.weight(1f), destructive = true)
        }
    }
}
