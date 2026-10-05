package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun KitchenRail(
    section: KitchenSection = KitchenSection.Checkout,
    kitchenBusy: Boolean = false,
    onSection: (KitchenSection) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    Column(
        modifier = modifier
            .width(AppTheme.sizes.checkoutRailWidth)
            .fillMaxHeight()
            .background(colors.card)
            .padding(horizontal = spacing.lg, vertical = spacing.xl),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Column(modifier = Modifier.padding(start = spacing.sm, bottom = spacing.sm)) {
            Text(text = stringResource(R.string.checkout_brand), style = AppTheme.typography.headlineSmall, color = colors.textPrimary)
            Text(
                text = stringResource(R.string.checkout_product),
                style = AppTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }
        Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(colors.textPrimary.copy(alpha = 0.14f)))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs), modifier = Modifier.padding(top = spacing.sm)) {
            RailItem(stringResource(R.string.checkout_nav_checkout), section == KitchenSection.Checkout) {
                onSection(KitchenSection.Checkout)
            }
            RailItem(stringResource(R.string.checkout_nav_orders), section == KitchenSection.Orders) {
                onSection(KitchenSection.Orders)
            }
            RailItem(stringResource(R.string.checkout_nav_menu), section == KitchenSection.Menu) {
                onSection(KitchenSection.Menu)
            }
            RailItem(stringResource(R.string.checkout_nav_settings), section == KitchenSection.Settings) {
                onSection(KitchenSection.Settings)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.sizes.logoMark)
                .clip(RoundedCornerShape(AppTheme.radius.sm))
                .border(AppTheme.border.thin, colors.surface, RoundedCornerShape(AppTheme.radius.sm))
                .clickable { onSection(KitchenSection.Status) }
                .padding(horizontal = AppTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Box(
                Modifier
                    .size(AppTheme.sizes.checkoutStatusDot)
                    .clip(CircleShape)
                    .background(if (kitchenBusy) colors.warning else colors.success),
            )
            Column {
                Text(
                    text = stringResource(R.string.checkout_kitchen_status),
                    style = AppTheme.typography.labelSmall,
                    color = colors.textPrimary.copy(alpha = 0.6f),
                )
                Text(
                    text = stringResource(if (kitchenBusy) R.string.checkout_kitchen_busy else R.string.checkout_kitchen_normal),
                    style = AppTheme.typography.titleSmall,
                    color = colors.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun RailItem(label: String, active: Boolean, onClick: (() -> Unit)? = null) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.sm))
            .background(if (active) colors.surface else colors.card)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(AppTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        Box(
            Modifier
                .size(AppTheme.sizes.checkoutNavDot)
                .clip(CircleShape)
                .background(if (active) colors.primary else colors.textPrimary),
        )
        Text(
            text = label,
            style = if (active) AppTheme.typography.titleSmall else AppTheme.typography.labelMedium,
            color = colors.textPrimary,
        )
    }
}

@Composable
fun CheckoutTopBar(title: String, onOpenOrders: () -> Unit, onNewOrder: () -> Unit, ordersSelected: Boolean, showActions: Boolean = true) {
    val colors = AppTheme.colors
    Column(Modifier.fillMaxWidth().background(colors.card)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.spacing.xxl, vertical = AppTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleLarge,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (showActions) {
                Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                    CheckoutChip(stringResource(R.string.checkout_open_orders), ordersSelected) { onOpenOrders() }
                    CheckoutChip(stringResource(R.string.checkout_new_order), !ordersSelected) { onNewOrder() }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(colors.textPrimary.copy(alpha = 0.14f)))
    }
}

@Composable
fun CheckoutChip(label: String, selected: Boolean, modifier: Modifier = Modifier, accent: Boolean = false, onClick: () -> Unit) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(AppTheme.radius.pill)
    val fill = when {
        selected && accent -> colors.primary
        selected -> colors.textPrimary
        else -> colors.card
    }
    val labelColor = when {
        selected && accent -> colors.card
        selected -> colors.background
        else -> colors.textPrimary
    }
    val labelText = @Composable {
        Text(
            text = label,
            style = AppTheme.typography.labelMedium,
            color = labelColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    if (accent) {
        Box(
            modifier = modifier
                .heightIn(min = AppTheme.sizes.minTouchTarget)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .height(AppTheme.sizes.iconLg)
                    .clip(shape)
                    .background(fill)
                    .border(AppTheme.border.thin, if (selected) colors.primary else colors.textPrimary, shape)
                    .padding(horizontal = AppTheme.spacing.lg),
                contentAlignment = Alignment.Center,
            ) { labelText() }
        }
    } else {
        Box(
            modifier = modifier
                .heightIn(min = AppTheme.sizes.minTouchTarget)
                .clip(shape)
                .background(fill)
                .border(AppTheme.border.thin, colors.textPrimary, shape)
                .clickable(onClick = onClick)
                .padding(horizontal = AppTheme.spacing.md),
            contentAlignment = Alignment.Center,
        ) { labelText() }
    }
}

@Composable
fun PillButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = true,
    enabled: Boolean = true,
    destructive: Boolean = false,
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(AppTheme.radius.pill)
    val background = when {
        !enabled -> colors.primary.copy(alpha = 0.4f)
        destructive -> colors.statusDanger
        filled -> colors.primary
        else -> colors.card
    }
    Box(
        modifier = modifier
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clip(shape)
            .background(background)
            .then(
                if (filled) {
                    Modifier
                } else {
                    Modifier.border(AppTheme.border.medium, colors.textPrimary, shape)
                },
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = AppTheme.typography.labelLarge,
            color = when {
                destructive -> colors.background
                filled -> colors.card
                else -> colors.textPrimary
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@ScreenPreviews
@Composable
private fun KitchenRailPreview() {
    PreviewSurface {
        Row(Modifier.height(AppTheme.sizes.heroHeight)) {
            KitchenRail()
            CheckoutTopBar("Checkout", onOpenOrders = {}, onNewOrder = {}, ordersSelected = true)
        }
    }
}
