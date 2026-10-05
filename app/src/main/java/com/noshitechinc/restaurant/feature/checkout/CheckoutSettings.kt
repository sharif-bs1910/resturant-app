package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.adaptive.rememberAdaptiveInfo
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsContent(state: CheckoutUiState, onChange: (SettingsState) -> Unit, modifier: Modifier = Modifier) {
    val settings = state.settings
    val adaptive = rememberAdaptiveInfo()
    if (adaptive.usesTwoPane) {
        Row(modifier.fillMaxSize()) {
            SettingsNav(settings, onChange, Modifier.width(AppTheme.sizes.checkoutRailWidth))
            SettingsPanel(settings, onChange, Modifier.weight(1f))
        }
    } else {
        Column(modifier.fillMaxSize()) {
            FlowRow(
                Modifier.padding(horizontal = AppTheme.spacing.xl, vertical = AppTheme.spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                SettingsTab.entries.forEach { tab ->
                    CheckoutChip(stringResource(tab.labelRes), settings.tab == tab && settings.detail == SettingsDetail.None) {
                        onChange(settings.copy(tab = tab, detail = SettingsDetail.None, dialog = SettingsDialog.None))
                    }
                }
            }
            SettingsPanel(settings, onChange, Modifier.weight(1f))
        }
    }
}

@Composable
fun SettingsDialogHost(state: CheckoutUiState, onChange: (SettingsState) -> Unit) {
    val settings = state.settings
    if (settings.dialog == SettingsDialog.None) return
    Box(
        Modifier
            .fillMaxSize()
            .background(AppTheme.colors.textPrimary.copy(alpha = 0.45f))
            .clickable { onChange(settings.copy(dialog = SettingsDialog.None, pin = "")) }
            .padding(AppTheme.spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.clickable(onClick = {})) {
            when (settings.dialog) {
                SettingsDialog.RemoveZone -> RemoveZoneDialog(settings, onChange)
                SettingsDialog.Pin -> PinDialog(settings, onChange)
                SettingsDialog.ClockedOut -> ClockResultDialog(settings, clockedOut = true, onChange)
                SettingsDialog.ClockedIn -> ClockResultDialog(settings, clockedOut = false, onChange)
                SettingsDialog.Staff -> StaffDialog(settings, onChange)
                SettingsDialog.EditStore -> HoursDialog(settings, online = false, onChange)
                SettingsDialog.EditOnline -> HoursDialog(settings, online = true, onChange)
                SettingsDialog.AddSpecial -> SpecialDialog(settings, editing = false, onChange)
                SettingsDialog.EditSpecial -> SpecialDialog(settings, editing = true, onChange)
                SettingsDialog.RemoveSpecial -> RemoveSpecialDialog(settings, onChange)
                SettingsDialog.None -> Unit
            }
        }
    }
}

private val SettingsTab.labelRes: Int
    get() = when (this) {
        SettingsTab.Station -> R.string.settings_station
        SettingsTab.Delivery -> R.string.settings_delivery
        SettingsTab.Orders -> R.string.settings_orders
        SettingsTab.Prep -> R.string.settings_prep
        SettingsTab.Catering -> R.string.settings_catering
        SettingsTab.Clock -> R.string.settings_clock
        SettingsTab.Hours -> R.string.settings_hours
    }

@Composable
private fun SettingsNav(settings: SettingsState, onChange: (SettingsState) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .padding(AppTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
    ) {
        SettingsTab.entries.forEach { tab ->
            val selected = settings.tab == tab && settings.detail == SettingsDetail.None
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = AppTheme.sizes.minTouchTarget)
                    .clip(RoundedCornerShape(AppTheme.radius.pill))
                    .background(if (selected) AppTheme.colors.surface else AppTheme.colors.background)
                    .clickable { onChange(settings.copy(tab = tab, detail = SettingsDetail.None)) }
                    .padding(horizontal = AppTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                Box(
                    Modifier
                        .width(AppTheme.spacing.xs)
                        .height(AppTheme.spacing.lg)
                        .clip(RoundedCornerShape(AppTheme.radius.pill))
                        .background(if (selected) AppTheme.colors.primary else AppTheme.colors.background),
                )
                Text(stringResource(tab.labelRes), style = AppTheme.typography.titleSmall, color = AppTheme.colors.textPrimary)
            }
        }
    }
}

@Composable
private fun SettingsPanel(settings: SettingsState, onChange: (SettingsState) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        when (settings.detail) {
            SettingsDetail.Printer -> PrinterPanel(settings, onChange)

            SettingsDetail.Wifi -> WifiPanel(settings, onChange)

            SettingsDetail.None -> when (settings.tab) {
                SettingsTab.Station -> StationPanel(settings, onChange)
                SettingsTab.Delivery -> DeliveryPanel(settings, onChange)
                SettingsTab.Orders -> OrdersPanel(settings, onChange)
                SettingsTab.Prep -> PrepPanel(settings, onChange)
                SettingsTab.Catering -> CateringPanel(settings, onChange)
                SettingsTab.Clock -> ClockPanel(settings, onChange)
                SettingsTab.Hours -> HoursPanel(settings, onChange)
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun SettingsStationPreview() {
    PreviewSurface {
        SettingsContent(CheckoutUiState(section = KitchenSection.Settings), {})
    }
}

@ScreenPreviews
@Composable
private fun SettingsPrinterPreview() {
    PreviewSurface {
        SettingsContent(CheckoutUiState(section = KitchenSection.Settings, settings = SettingsState(detail = SettingsDetail.Printer)), {})
    }
}

@ScreenPreviews
@Composable
private fun SettingsRemoveZonePreview() {
    PreviewSurface {
        SettingsDialogHost(
            CheckoutUiState(
                section = KitchenSection.Settings,
                settings = SettingsState(dialog = SettingsDialog.RemoveZone, pendingZoneId = "zone-b"),
            ),
        ) {}
    }
}
