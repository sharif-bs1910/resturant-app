package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.adaptive.rememberAdaptiveInfo
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun KitchenStatusContent(state: CheckoutUiState, onMode: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val adaptive = rememberAdaptiveInfo()
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
    ) {
        Text(
            stringResource(R.string.checkout_kitchen_mode),
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.textPrimary.copy(alpha = 0.45f),
        )
        Text(
            stringResource(R.string.checkout_kitchen_question),
            style = AppTheme.typography.headlineMedium,
            color = AppTheme.colors.textPrimary,
        )
        Text(
            stringResource(R.string.checkout_kitchen_body),
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.textPrimary.copy(alpha = 0.6f),
        )
        if (adaptive.usesTwoPane) {
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
            ) {
                ModeCard(normal = true, busy = state.kitchenBusy, onMode = onMode, Modifier.weight(1f).fillMaxHeight())
                ModeCard(normal = false, busy = state.kitchenBusy, onMode = onMode, Modifier.weight(1f).fillMaxHeight())
            }
        } else {
            ModeCard(normal = true, busy = state.kitchenBusy, onMode = onMode, Modifier.fillMaxWidth())
            ModeCard(normal = false, busy = state.kitchenBusy, onMode = onMode, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ModeCard(normal: Boolean, busy: Boolean, onMode: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val active = if (normal) !busy else busy
    val accent = if (normal) colors.success else colors.warning
    val shape = RoundedCornerShape(AppTheme.radius.lg)
    Column(
        modifier
            .clip(shape)
            .background(colors.card)
            .border(if (active) AppTheme.border.thick else AppTheme.border.medium, if (active) accent else colors.outline, shape)
            .padding(AppTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(AppTheme.sizes.iconSm).clip(CircleShape).background(accent))
            Text(
                stringResource(if (normal) R.string.checkout_kitchen_normal else R.string.checkout_kitchen_busy),
                style = AppTheme.typography.headlineSmall,
                color = colors.textPrimary,
                modifier = Modifier.padding(start = AppTheme.spacing.md).weight(1f),
            )
            if (active) {
                ActiveBadge(accent, if (normal) colors.card else colors.textPrimary)
            } else {
                Text(
                    stringResource(R.string.checkout_kitchen_not_active),
                    style = AppTheme.typography.labelSmall,
                    color = colors.textPrimary.copy(alpha = 0.35f),
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(colors.textPrimary.copy(alpha = 0.08f)))
        Text(
            stringResource(if (normal) R.string.checkout_kitchen_normal_body else R.string.checkout_kitchen_busy_body),
            style = AppTheme.typography.bodyLarge,
            color = colors.textPrimary.copy(alpha = 0.62f),
        )
        StatRow(
            stringResource(R.string.checkout_kitchen_prep_buffer),
            stringResource(if (normal) R.string.checkout_kitchen_buffer_none else R.string.checkout_kitchen_buffer_busy),
        )
        StatRow(
            stringResource(R.string.checkout_kitchen_online_orders),
            stringResource(if (normal) R.string.checkout_kitchen_accepting else R.string.checkout_kitchen_throttled),
        )
        StatRow(
            stringResource(R.string.checkout_kitchen_quoted_wait),
            stringResource(if (normal) R.string.checkout_kitchen_wait_normal else R.string.checkout_kitchen_wait_busy),
        )
        if (active) {
            SelectedButton()
        } else {
            SwitchButton(
                stringResource(if (normal) R.string.checkout_kitchen_switch_normal else R.string.checkout_kitchen_switch_busy),
                if (normal) colors.successContainer else colors.warningContainer,
            ) { onMode(!normal) }
        }
    }
}

@Composable
private fun ActiveBadge(background: Color, label: Color) {
    Text(
        stringResource(R.string.checkout_kitchen_active),
        style = AppTheme.typography.labelSmall,
        color = label,
        modifier = Modifier
            .clip(RoundedCornerShape(AppTheme.radius.pill))
            .background(background)
            .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.xs),
    )
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = AppTheme.typography.labelMedium, color = AppTheme.colors.textPrimary.copy(alpha = 0.5f))
        Text(value, style = AppTheme.typography.labelMedium, color = AppTheme.colors.textPrimary)
    }
}

@Composable
private fun SelectedButton() {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .alpha(0.48f)
            .clip(shape)
            .border(AppTheme.border.thick, AppTheme.colors.textMuted, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            stringResource(R.string.checkout_kitchen_selected),
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.textPrimary,
        )
    }
}

@Composable
private fun SwitchButton(label: String, background: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clip(RoundedCornerShape(AppTheme.radius.md))
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = AppTheme.typography.labelLarge, color = AppTheme.colors.textPrimary)
    }
}

@ScreenPreviews
@Composable
private fun KitchenStatusNormalPreview() {
    PreviewSurface {
        KitchenStatusContent(CheckoutUiState(section = KitchenSection.Status), {})
    }
}

@ScreenPreviews
@Composable
private fun KitchenStatusBusyPreview() {
    PreviewSurface {
        KitchenStatusContent(CheckoutUiState(section = KitchenSection.Status, kitchenBusy = true), {})
    }
}
