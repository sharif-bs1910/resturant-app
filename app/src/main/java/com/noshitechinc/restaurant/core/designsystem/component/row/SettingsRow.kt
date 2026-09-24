package com.noshitechinc.restaurant.core.designsystem.component.row

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.noshitechinc.restaurant.core.designsystem.component.selection.AppSwitch
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun SettingsNavigationRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    value: String? = null,
    enabled: Boolean = true,
) {
    ListRow(
        title = title,
        modifier = modifier,
        subtitle = subtitle,
        trailingText = value,
        showChevron = true,
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true,
) {
    ListRow(
        title = title,
        modifier = modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Switch,
            onValueChange = onCheckedChange,
        ),
        subtitle = subtitle,
        enabled = enabled,
        trailingContent = {
            AppSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
            )
        },
    )
}

@Composable
fun SettingsValueRow(title: String, value: String, modifier: Modifier = Modifier) {
    ListRow(
        title = title,
        modifier = modifier,
        trailingText = value,
    )
}

@Composable
fun SettingsDestructiveRow(title: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    ListRow(
        title = title,
        modifier = modifier,
        enabled = enabled,
        destructive = true,
        onClick = onClick,
    )
}

@ComponentPreviews
@Composable
private fun SettingsRowVariantsPreview() {
    PreviewSurface {
        Column {
            SettingsNavigationRow(
                title = "Printers",
                subtitle = "Kitchen and receipt",
                value = "2 connected",
                onClick = {},
            )
            SettingsSwitchRow(
                title = "Sound alerts",
                checked = true,
                onCheckedChange = {},
                subtitle = "Play a tone for new tickets",
            )
            SettingsValueRow(title = "App version", value = "1.0.0")
            SettingsDestructiveRow(title = "Sign out", onClick = {})
            SettingsNavigationRow(
                title = "Disabled navigation",
                onClick = {},
                enabled = false,
            )
            SettingsSwitchRow(
                title = "Disabled switch",
                checked = false,
                onCheckedChange = {},
                enabled = false,
            )
            SettingsDestructiveRow(title = "Disabled destructive", onClick = {}, enabled = false)
        }
    }
}

@ComponentPreviews
@Composable
private fun SettingsRowLongTextPreview() {
    PreviewSurface {
        Column(modifier = Modifier.width(360.dp)) {
            SettingsNavigationRow(
                title = PreviewData.LONG_NAME,
                subtitle = PreviewData.LONG_DESCRIPTION,
                value = PreviewData.LONG_PRICE,
                onClick = {},
            )
            SettingsSwitchRow(
                title = PreviewData.TRANSLATED_LABEL,
                checked = true,
                onCheckedChange = {},
                subtitle = PreviewData.LONG_DESCRIPTION,
            )
            SettingsValueRow(title = PreviewData.LONG_NAME, value = PreviewData.LONG_PRICE)
            SettingsDestructiveRow(title = PreviewData.TRANSLATED_LABEL, onClick = {})
        }
    }
}
