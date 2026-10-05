package com.noshitechinc.restaurant.feature.checkout

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
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun OrdersPanel(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    PageTitle(stringResource(R.string.settings_orders))
    SectionLabel(stringResource(R.string.settings_fulfillment))
    SettingsCard {
        SettingLine(
            stringResource(
                R.string.settings_accept_pickup,
            ),
            stringResource(R.string.settings_accept_pickup_body),
            settings.acceptPickup,
            {
                onChange(settings.copy(acceptPickup = it))
            },
        )
        SettingLine(
            stringResource(
                R.string.settings_accept_delivery,
            ),
            stringResource(R.string.settings_accept_delivery_body),
            settings.acceptDelivery,
            {
                onChange(settings.copy(acceptDelivery = it))
            },
        )
        SettingLine(
            stringResource(
                R.string.settings_scheduled,
            ),
            stringResource(R.string.settings_scheduled_body),
            settings.allowScheduled,
            {
                onChange(settings.copy(allowScheduled = it))
            },
        )
        SettingLine(
            stringResource(
                R.string.settings_auto_accept,
            ),
            stringResource(R.string.settings_auto_accept_body),
            settings.autoAccept,
            {
                onChange(settings.copy(autoAccept = it))
            },
            divider = false,
        )
    }
    SectionLabel(stringResource(R.string.settings_intake))
    SettingsCard {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding, vertical = AppTheme.spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.settings_default_fulfillment),
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                CheckoutChip(
                    stringResource(R.string.checkout_pickup),
                    settings.defaultFulfillment == Fulfillment.Pickup,
                    accent = true,
                ) {
                    onChange(settings.copy(defaultFulfillment = Fulfillment.Pickup))
                }
                CheckoutChip(
                    stringResource(R.string.checkout_delivery),
                    settings.defaultFulfillment == Fulfillment.Delivery,
                    accent = true,
                ) {
                    onChange(settings.copy(defaultFulfillment = Fulfillment.Delivery))
                }
            }
        }
        HoursDivider()
        SettingLine(
            stringResource(
                R.string.settings_pin_orders,
            ),
            stringResource(R.string.settings_pin_orders_body),
            settings.pinOpenOrders,
            {
                onChange(settings.copy(pinOpenOrders = it))
            },
            divider = false,
        )
    }
}

@Composable
internal fun PrepPanel(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    PageTitle(stringResource(R.string.settings_prep))
    SectionLabel(stringResource(R.string.settings_timing))
    SettingsCard {
        DeliveryValueRow(stringResource(R.string.settings_prep_time), settings.prepMinutes, {
            onChange(settings.copy(prepMinutes = it))
        }, suffix = stringResource(R.string.settings_min))
        DeliveryValueRow(stringResource(R.string.settings_max_orders), settings.maxOrders, {
            onChange(settings.copy(maxOrders = it))
        }, suffix = stringResource(R.string.settings_orders_unit), divider = false)
    }
    SectionLabel(stringResource(R.string.settings_automation))
    SettingsCard {
        SettingLine(stringResource(R.string.settings_auto_busy), stringResource(R.string.settings_auto_busy_body), settings.autoBusy, {
            onChange(settings.copy(autoBusy = it))
        })
        SettingLine(
            stringResource(
                R.string.settings_pause_online,
            ),
            stringResource(R.string.settings_pause_online_body),
            settings.pauseOnline,
            {
                onChange(settings.copy(pauseOnline = it))
            },
            divider = false,
        )
    }
}

@Composable
internal fun CateringPanel(settings: SettingsState, onChange: (SettingsState) -> Unit) {
    PageTitle(stringResource(R.string.settings_catering))
    SectionLabel(stringResource(R.string.settings_catering_orders))
    SettingsCard {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(AppTheme.sizes.buttonHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
        ) {
            CateringField(
                stringResource(R.string.settings_lead),
                settings.leadHours,
                stringResource(R.string.settings_hours_unit),
                Modifier.weight(1f),
            ) {
                onChange(settings.copy(leadHours = it))
            }
            CateringField(
                stringResource(R.string.settings_window),
                settings.windowDays,
                stringResource(R.string.settings_days),
                Modifier.weight(1f),
            ) {
                onChange(settings.copy(windowDays = it))
            }
        }
        HoursDivider()
        SettingLine(stringResource(R.string.settings_review), stringResource(R.string.settings_review_body), settings.reviewCatering, {
            onChange(settings.copy(reviewCatering = it))
        }, divider = false)
    }
}

@Composable
private fun CateringField(title: String, value: String, suffix: String, modifier: Modifier = Modifier, onValue: (String) -> Unit) {
    Column(modifier.padding(vertical = AppTheme.spacing.lg), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        Text(title, style = AppTheme.typography.titleSmall, color = AppTheme.colors.textPrimary)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            InlineValueField(value, onValue, Modifier.weight(1f))
            Text(suffix, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
        }
    }
}
