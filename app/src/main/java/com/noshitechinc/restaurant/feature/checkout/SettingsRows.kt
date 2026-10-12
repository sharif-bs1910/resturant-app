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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.selection.AppSwitch
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun PageTitle(title: String) {
    Text(title, style = AppTheme.typography.headlineSmall, color = AppTheme.colors.textPrimary)
}

@Composable
internal fun SectionLabel(title: String) {
    Text(title, style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
}

@Composable
internal fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.lg))
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.lg)),
        content = { content() },
    )
}

@Composable
internal fun SettingsToggle(checked: Boolean, onChecked: (Boolean) -> Unit) {
    AppSwitch(checked = checked, onCheckedChange = onChecked)
}

@Composable
internal fun SettingLine(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit, divider: Boolean = true) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding, vertical = AppTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                Text(title, style = AppTheme.typography.titleSmall, color = AppTheme.colors.textPrimary)
                Text(subtitle, style = AppTheme.typography.bodySmall, color = AppTheme.colors.textDisabled)
            }
            SettingsToggle(checked, onChecked)
        }
        if (divider) HoursDivider()
    }
}

@Composable
internal fun DeliveryValueRow(
    title: String,
    value: String,
    onValue: (String) -> Unit,
    prefix: String? = null,
    suffix: String? = null,
    divider: Boolean = true,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding, vertical = AppTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Text(title, style = AppTheme.typography.titleSmall, color = AppTheme.colors.textPrimary, modifier = Modifier.weight(1f))
            if (prefix != null) {
                Text(prefix, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
            }
            InlineValueField(value, onValue, Modifier.width(AppTheme.sizes.passcodeKeyWidth))
            if (suffix != null) {
                Text(suffix, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
            }
        }
        if (divider) HoursDivider()
    }
}

@Composable
internal fun InlineValueField(value: String, onValue: (String) -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    BasicTextField(
        value = value,
        onValueChange = onValue,
        singleLine = true,
        textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.textPrimary),
        modifier = modifier
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(AppTheme.spacing.md),
    )
}

@Composable
internal fun StatusRow(
    title: String,
    subtitle: String? = null,
    value: String,
    connected: Boolean,
    showPaired: Boolean,
    badge: String? = null,
    showDot: Boolean = true,
    showChevron: Boolean = true,
    titleColor: Color = AppTheme.colors.textPrimary,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.sizes.listRowMinHeight)
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        if (showDot) {
            Icon(
                Icons.Filled.Circle,
                contentDescription = null,
                tint = if (connected) AppTheme.colors.success else AppTheme.colors.textMuted,
                modifier = Modifier.size(AppTheme.sizes.checkoutStatusDot),
            )
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = AppTheme.typography.titleSmall, color = titleColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (showPaired) {
            PairedBadge()
        } else if (badge != null) {
            StatusBadge(badge)
        }
        if (value.isNotEmpty()) {
            Text(value, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textPrimary, maxLines = 1, softWrap = false)
        }
        if (showChevron) {
            Text(stringResource(R.string.checkout_collapse), style = AppTheme.typography.titleSmall, color = AppTheme.colors.textMuted)
        }
    }
}

@Composable
private fun PairedBadge() {
    StatusBadge(stringResource(R.string.settings_paired))
}

@Composable
private fun StatusBadge(label: String) {
    Text(
        label,
        style = AppTheme.typography.labelSmall,
        color = AppTheme.colors.card,
        modifier = Modifier
            .clip(RoundedCornerShape(AppTheme.radius.sm))
            .background(AppTheme.colors.success)
            .padding(horizontal = AppTheme.spacing.sm, vertical = AppTheme.spacing.xs),
    )
}

@Composable
internal fun BackLink(label: String, onClick: () -> Unit) {
    Text(
        label,
        style = AppTheme.typography.labelSmall,
        color = AppTheme.colors.primary,
        modifier = Modifier.clickable(onClick = onClick).padding(vertical = AppTheme.spacing.sm),
    )
}

@Composable
internal fun HoursDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(AppTheme.border.thin)
            .background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
    )
}
