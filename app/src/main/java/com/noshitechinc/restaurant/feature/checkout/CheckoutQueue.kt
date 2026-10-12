package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

data class QueueActions(
    val onFilter: (QueueFilter) -> Unit,
    val onSearch: (String) -> Unit,
    val onSelect: (String) -> Unit,
    val onClose: () -> Unit,
    val onEdit: () -> Unit,
    val onPay: () -> Unit,
    val onGift: () -> Unit,
    val onSave: () -> Unit,
)

@Composable
fun QueueContent(state: CheckoutUiState, actions: QueueActions) {
    BoxWithConstraints(Modifier.fillMaxSize().padding(AppTheme.spacing.xl)) {
        val sideBySide = maxWidth >= AppTheme.sizes.checkoutDetailWidth + AppTheme.sizes.checkoutCartWidth
        if (sideBySide) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                QueueList(state, actions, Modifier.weight(1f).fillMaxHeight())
                state.selectedOrder?.let {
                    QueueDetail(it, actions, Modifier.width(AppTheme.sizes.checkoutDetailWidth).fillMaxHeight(), scroll = true)
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                QueueList(state, actions, Modifier.fillMaxWidth())
                state.selectedOrder?.let { QueueDetail(it, actions, Modifier.fillMaxWidth(), scroll = false) }
            }
        }
    }
}

@Composable
private fun QueueList(state: CheckoutUiState, actions: QueueActions, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            QueueFilter.entries.forEach { filter ->
                CheckoutChip(
                    label = stringResource(filter.labelRes),
                    selected = state.queueFilter == filter,
                    onClick = { actions.onFilter(filter) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        QueueSearch(state.queueQuery, actions.onSearch)
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.radius.lg))
                .background(AppTheme.colors.card)
                .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.lg)),
        ) {
            if (state.visibleQueue.isEmpty()) {
                Text(
                    stringResource(R.string.checkout_no_orders),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textMuted,
                    modifier = Modifier.padding(AppTheme.spacing.lg),
                )
            }
            state.visibleQueue.forEach { order ->
                QueueRow(order, selected = order.number == state.selectedOrder?.number, onClick = { actions.onSelect(order.number) })
            }
        }
    }
}

@Composable
private fun QueueRow(order: QueueOrder, selected: Boolean, onClick: () -> Unit) {
    val dot = when (order.status) {
        OpenOrderStatus.New -> AppTheme.colors.primary
        OpenOrderStatus.Cooking -> AppTheme.colors.secondary
        OpenOrderStatus.Ready -> AppTheme.colors.success
        OpenOrderStatus.Completed -> AppTheme.colors.textMuted
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) AppTheme.colors.surface else AppTheme.colors.card)
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Box(Modifier.size(AppTheme.sizes.checkoutStatusDot).clip(CircleShape).background(dot))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
            Text(
                "#${order.number} · ${order.guest}",
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                order.subtitle,
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            stringResource(R.string.checkout_paid),
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.background,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .clip(RoundedCornerShape(AppTheme.radius.xs))
                .background(AppTheme.colors.success)
                .padding(horizontal = AppTheme.spacing.xs, vertical = AppTheme.spacing.xxs),
        )
        Text(
            formatMoney(order.totalCents),
            style = AppTheme.typography.titleSmall,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            softWrap = false,
        )
        Text("›", style = AppTheme.typography.titleMedium, color = AppTheme.colors.textMuted)
    }
}

@Composable
private fun QueueDetail(order: QueueOrder, actions: QueueActions, modifier: Modifier, scroll: Boolean) {
    val shape = RoundedCornerShape(AppTheme.radius.lg)
    Column(
        modifier = modifier
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(AppTheme.spacing.lg)
            .then(if (scroll) Modifier.verticalScroll(rememberScrollState()) else Modifier),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Text(
                "#${order.number} · ${order.guest}",
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                stringResource(order.status.labelRes),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .clip(RoundedCornerShape(AppTheme.radius.xs))
                    .background(AppTheme.colors.primary)
                    .padding(horizontal = AppTheme.spacing.xs, vertical = AppTheme.spacing.xxs),
            )
            Box(
                Modifier
                    .size(AppTheme.sizes.iconLg)
                    .clip(RoundedCornerShape(AppTheme.radius.sm))
                    .background(AppTheme.colors.surface)
                    .clickable(onClick = actions.onClose),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(R.string.checkout_dismiss),
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textMuted,
                    maxLines = 1,
                )
            }
        }
        Text(
            order.meta,
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.textMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)))
        order.lines.forEach { line ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.checkout_line_qty, line.quantity, line.name),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    formatMoney(line.unitCents * line.quantity),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)))
        MoneyPair(stringResource(R.string.checkout_subtotal), formatMoney(order.subtotalCents), emphasize = false)
        MoneyPair(stringResource(R.string.checkout_tax), formatMoney(order.orderTaxCents), emphasize = false)
        MoneyPair(stringResource(R.string.checkout_total), formatMoney(order.totalCents), emphasize = true)
        PillButton(stringResource(R.string.checkout_edit_order), actions.onEdit, Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            QuietButton(stringResource(R.string.checkout_add_charge), {})
            QuietButton(stringResource(R.string.checkout_redeem), actions.onGift)
        }
        PillButton(stringResource(R.string.checkout_refund), {}, filled = false)
        PillButton(stringResource(R.string.checkout_cancel_order), {}, filled = false)
        PillButton(stringResource(R.string.checkout_pay_now), actions.onPay, Modifier.fillMaxWidth())
        QuietButton(stringResource(R.string.checkout_save_draft), actions.onSave, Modifier.fillMaxWidth())
    }
}

@Composable
internal fun MoneyPair(label: String, value: String, emphasize: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (emphasize) AppTheme.typography.titleMedium else AppTheme.typography.bodyMedium,
            color = if (emphasize) AppTheme.colors.textPrimary else AppTheme.colors.textMuted,
        )
        Text(
            value,
            style = if (emphasize) AppTheme.typography.priceMedium else AppTheme.typography.bodyMedium,
            color = if (emphasize) AppTheme.colors.primary else AppTheme.colors.textPrimary,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
internal fun QuietButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(AppTheme.radius.pill))
            .background(AppTheme.colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.sizes.buttonHorizontalPadding, vertical = AppTheme.spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = AppTheme.typography.labelLarge,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun QueueSearch(value: String, onValue: (String) -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppTheme.colors.surface)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.12f), shape)
            .padding(AppTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = AppTheme.colors.textMuted,
            modifier = Modifier.size(AppTheme.sizes.iconSm),
        )
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValue,
            textStyle = AppTheme.typography.bodySmall.copy(color = AppTheme.colors.textPrimary),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        stringResource(R.string.checkout_search_orders),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted,
                    )
                }
                inner()
            },
        )
    }
}

@ScreenPreviews
@Composable
private fun QueuePreview() {
    PreviewSurface {
        QueueContent(
            CheckoutUiState(step = CheckoutStep.Queue),
            QueueActions({}, {}, {}, {}, {}, {}, {}, {}),
        )
    }
}
