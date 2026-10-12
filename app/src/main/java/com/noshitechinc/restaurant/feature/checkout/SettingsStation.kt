package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun StationPanel(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    PageTitle(stringResource(R.string.settings_station))
    SectionLabel(stringResource(R.string.settings_access))
    SettingsCard {
        SettingLine(
            stringResource(
                R.string.settings_require_passcode,
            ),
            stringResource(R.string.settings_require_passcode_body),
            settings.requirePasscode,
            {
                onChange(settings.copy(requirePasscode = it))
            },
        )
        SettingLine(stringResource(R.string.settings_auto_lock), stringResource(R.string.settings_auto_lock_body), settings.autoLock, {
            onChange(settings.copy(autoLock = it))
        }, divider = false)
    }
    SectionLabel(stringResource(R.string.settings_printing))
    SettingsCard {
        StatusRow(
            stringResource(R.string.settings_receipt_printer),
            stringResource(R.string.settings_printer_sub),
            stringResource(R.string.settings_printer_ready),
            connected = settings.printerPaired,
            showPaired = false,
        ) { onChange(settings.copy(detail = SettingsDetail.Printer)) }
        HoursDivider()
        SettingLine(stringResource(R.string.settings_auto_print), stringResource(R.string.settings_auto_print_body), settings.autoPrint, {
            onChange(settings.copy(autoPrint = it))
        })
        SettingLine(
            stringResource(
                R.string.settings_option_names,
            ),
            stringResource(R.string.settings_option_names_body),
            settings.optionNames,
            {
                onChange(settings.copy(optionNames = it))
            },
            divider = false,
        )
    }
    SectionLabel(stringResource(R.string.settings_alerts))
    SettingsCard {
        SettingLine(
            stringResource(
                R.string.settings_new_order_alert,
            ),
            stringResource(R.string.settings_new_order_alert_body),
            settings.newOrderAlert,
            {
                onChange(settings.copy(newOrderAlert = it))
            },
        )
        SettingLine(
            stringResource(
                R.string.settings_ready_alert,
            ),
            stringResource(R.string.settings_ready_alert_body),
            settings.readyAlert,
            {
                onChange(settings.copy(readyAlert = it))
            },
        )
        SettingLine(
            stringResource(
                R.string.settings_offline_alert,
            ),
            stringResource(R.string.settings_offline_alert_body),
            settings.offlineAlert,
            {
                onChange(settings.copy(offlineAlert = it))
            },
            divider = false,
        )
    }
    SectionLabel(stringResource(R.string.settings_connectivity))
    SettingsCard {
        StatusRow(
            stringResource(R.string.settings_wifi),
            stringResource(R.string.settings_wifi_sub),
            stringResource(R.string.settings_wifi_value),
            connected = settings.wifiOn,
            showPaired = false,
        ) {
            onChange(settings.copy(detail = SettingsDetail.Wifi))
        }
        HoursDivider()
        SettingLine(
            stringResource(
                R.string.settings_block_offline,
            ),
            stringResource(R.string.settings_block_offline_body),
            settings.blockOffline,
            {
                onChange(settings.copy(blockOffline = it))
            },
        )
        SettingLine(
            stringResource(
                R.string.settings_auto_update,
            ),
            stringResource(R.string.settings_auto_update_body),
            settings.autoUpdate,
            {
                onChange(settings.copy(autoUpdate = it))
            },
        )
        StatusRow(
            stringResource(R.string.settings_pos),
            stringResource(R.string.settings_pos_body),
            stringResource(R.string.settings_pos_value),
            connected = true,
            showPaired = false,
            showDot = false,
            showChevron = false,
            onClick = {},
        )
    }
}

@Composable
internal fun PrinterPanel(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    BackLink(stringResource(R.string.settings_back_printing)) { onChange(settings.copy(detail = SettingsDetail.None)) }
    PageTitle(stringResource(R.string.settings_receipt_printer))
    SectionLabel(stringResource(R.string.settings_status))
    SettingsCard {
        StatusRow(
            stringResource(R.string.settings_printer_name),
            stringResource(R.string.settings_printer_connected),
            stringResource(if (settings.printerPaired) R.string.settings_ready else R.string.settings_dash),
            connected = settings.printerPaired,
            showPaired = settings.printerPaired,
            showChevron = false,
            onClick = {},
        )
    }
    SectionLabel(stringResource(R.string.settings_printing))
    SettingsCard {
        SettingLine(
            stringResource(
                R.string.settings_kitchen_tickets,
            ),
            stringResource(R.string.settings_kitchen_tickets_body),
            settings.kitchenTickets,
            {
                onChange(settings.copy(kitchenTickets = it))
            },
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.settings_copies), style = AppTheme.typography.titleSmall, color = AppTheme.colors.textPrimary)
                Text(
                    stringResource(R.string.settings_copies_body),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted,
                )
            }
            CopyStepper(settings.copies) { onChange(settings.copy(copies = it)) }
        }
    }
    SectionLabel(stringResource(R.string.settings_manage))
    SettingsCard {
        StatusRow(
            stringResource(
                R.string.settings_test_page,
            ),
            value = "",
            connected = false,
            showDot = false,
            showPaired = true,
            onClick = {
            },
        )
        HoursDivider()
        StatusRow(
            stringResource(
                R.string.settings_pair_printer,
            ),
            stringResource(R.string.settings_pair_printer_body),
            "",
            connected = false,
            showDot = false,
            showPaired = true,
            onClick = {
            },
        )
        HoursDivider()
        StatusRow(
            stringResource(R.string.settings_unpair),
            value = "",
            connected = false,
            showDot = false,
            showPaired = false,
            showChevron = false,
            titleColor = AppTheme.colors.statusDanger,
            onClick = { onChange(settings.copy(printerPaired = false)) },
        )
    }
}

@Composable
internal fun WifiPanel(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    BackLink(stringResource(R.string.settings_back_connectivity)) { onChange(settings.copy(detail = SettingsDetail.None)) }
    PageTitle(stringResource(R.string.settings_wifi))
    SettingsCard {
        SettingLine(stringResource(R.string.settings_wifi), stringResource(R.string.settings_wifi_on_body), settings.wifiOn, {
            onChange(settings.copy(wifiOn = it))
        }, divider = false)
    }
    SectionLabel(stringResource(R.string.settings_current_network))
    SettingsCard {
        StatusRow(
            stringResource(
                R.string.settings_wifi_value,
            ),
            stringResource(
                R.string.settings_wifi_sub,
            ),
            stringResource(R.string.settings_secured),
            connected = settings.wifiOn,
            showPaired = false,
            onClick = {
            },
        )
        HoursDivider()
        StatusRow(
            stringResource(R.string.settings_forget),
            value = "",
            connected = false,
            showDot = false,
            showPaired = false,
            showChevron = false,
            titleColor = AppTheme.colors.statusDanger,
            onClick = { onChange(settings.copy(wifiOn = false)) },
        )
    }
    SectionLabel(stringResource(R.string.settings_available))
    SettingsCard {
        val networks = listOf(R.string.settings_guest, R.string.settings_back_office, R.string.settings_owner)
        networks.forEachIndexed { index, name ->
            StatusRow(
                stringResource(name),
                stringResource(R.string.settings_in_range),
                "",
                connected = false,
                showDot = false,
                showPaired = false,
                onClick = {},
            )
            if (index < networks.lastIndex) HoursDivider()
        }
    }
}

private const val MIN_COPIES = 1
private const val MAX_COPIES = 5

@Composable
private fun CopyStepper(value: Int, onChange: (Int) -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Row(
        Modifier
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clip(shape)
            .border(AppTheme.border.thin, AppTheme.colors.outline, shape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CopyStep(Icons.Filled.Remove, stringResource(R.string.a11y_decrease), value > MIN_COPIES) { onChange(value - 1) }
        Box(Modifier.width(AppTheme.border.thin).height(AppTheme.sizes.minTouchTarget).background(AppTheme.colors.outline))
        Text(
            value.toString(),
            style = AppTheme.typography.titleSmall,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(AppTheme.sizes.minTouchTarget),
        )
        Box(Modifier.width(AppTheme.border.thin).height(AppTheme.sizes.minTouchTarget).background(AppTheme.colors.outline))
        CopyStep(Icons.Filled.Add, stringResource(R.string.a11y_increase), value < MAX_COPIES) { onChange(value + 1) }
    }
}

@Composable
private fun CopyStep(icon: ImageVector, description: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .size(AppTheme.sizes.minTouchTarget)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = description, tint = if (enabled) AppTheme.colors.textPrimary else AppTheme.colors.textDisabled)
    }
}
