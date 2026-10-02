package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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

data class BoardActions(
    val onFilter: (BoardFilter) -> Unit,
    val onSearch: (String) -> Unit,
    val onSelect: (String) -> Unit,
    val onToggleLine: (String) -> Unit,
    val onMarkComplete: () -> Unit,
    val onToggleFulfillment: () -> Unit,
    val onClose: () -> Unit,
)

@Composable
fun OrderBoardContent(state: CheckoutUiState, actions: BoardActions) {
    BoxWithConstraints(Modifier.fillMaxSize().padding(AppTheme.spacing.xl)) {
        val sideBySide = maxWidth >= AppTheme.sizes.checkoutDetailWidth + AppTheme.sizes.checkoutCartWidth
        if (sideBySide) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                BoardList(state, actions, Modifier.weight(1f).fillMaxHeight())
                state.selectedBoard?.let {
                    BoardDetail(it, actions, Modifier.width(AppTheme.sizes.checkoutDetailWidth).fillMaxHeight(), pinActions = true)
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                BoardList(state, actions, Modifier.fillMaxWidth())
                state.selectedBoard?.let { BoardDetail(it, actions, Modifier.fillMaxWidth(), pinActions = false) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BoardList(state: CheckoutUiState, actions: BoardActions, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            BoardFilter.entries.forEach { filter ->
                CheckoutChip(stringResource(filter.labelRes), state.boardFilter == filter) { actions.onFilter(filter) }
            }
        }
        QueueSearch(state.boardQuery, actions.onSearch)
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.radius.lg))
                .background(AppTheme.colors.card)
                .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.lg)),
        ) {
            if (state.visibleBoard.isEmpty()) {
                Text(
                    stringResource(R.string.checkout_no_orders),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textMuted,
                    modifier = Modifier.padding(AppTheme.spacing.lg),
                )
            }
            state.visibleBoard.forEach { order ->
                BoardRow(order, selected = order.number == state.selectedBoard?.number, onClick = { actions.onSelect(order.number) })
            }
        }
    }
}

@Composable
private fun BoardRow(order: BoardOrder, selected: Boolean, onClick: () -> Unit) {
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
                stringResource(
                    R.string.checkout_board_subtitle,
                    stringResource(order.status.boardLabelRes),
                    stringResource(order.fulfillment.labelRes),
                    order.time,
                ),
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
                .background(if (order.paidDeep) AppTheme.colors.primaryPressed else AppTheme.colors.success)
                .padding(horizontal = AppTheme.spacing.xs, vertical = AppTheme.spacing.xxs),
        )
        Text(
            formatMoney(order.totalCents),
            style = AppTheme.typography.titleSmall,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            softWrap = false,
        )
        Text(stringResource(R.string.checkout_collapse), style = AppTheme.typography.titleMedium, color = AppTheme.colors.textMuted)
    }
}

@Composable
private fun BoardDetail(order: BoardOrder, actions: BoardActions, modifier: Modifier, pinActions: Boolean) {
    val shape = RoundedCornerShape(AppTheme.radius.lg)
    val next = if (order.fulfillment == Fulfillment.Pickup) Fulfillment.Delivery else Fulfillment.Pickup
    Column(
        modifier = modifier
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(AppTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Column(
            Modifier
                .then(if (pinActions) Modifier.weight(1f).verticalScroll(rememberScrollState()) else Modifier),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                Text(
                    "#${order.number} · ${order.detailGuest}",
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
                        .background(if (order.status == OpenOrderStatus.New) AppTheme.colors.secondary else AppTheme.colors.surface)
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
                stringResource(
                    R.string.checkout_board_meta,
                    stringResource(order.fulfillment.labelRes).uppercase(),
                    order.time,
                    stringResource(R.string.checkout_phone_order_meta),
                ),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)))
            order.lines.forEach { line ->
                BoardLineRow(line, expanded = line.id in order.expanded, onClick = { actions.onToggleLine(line.id) })
            }
            if (order.lines.isNotEmpty()) {
                Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)))
            }
            MoneyPair(stringResource(R.string.checkout_subtotal), formatMoney(order.subtotalCents), emphasize = false)
            MoneyPair(stringResource(R.string.checkout_tax), formatMoney(order.taxCents), emphasize = false)
            MoneyPair(stringResource(R.string.checkout_total), formatMoney(order.totalCents), emphasize = true)
        }
        if (pinActions) Spacer(Modifier.height(AppTheme.spacing.xs))
        PillButton(stringResource(R.string.checkout_mark_complete), actions.onMarkComplete, Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            QuietButton(stringResource(R.string.checkout_add_charge), {})
            QuietButton(
                stringResource(
                    R.string.checkout_switch_fulfillment,
                    stringResource(order.fulfillment.labelRes),
                    stringResource(next.labelRes),
                ),
                actions.onToggleFulfillment,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            PillButton(stringResource(R.string.checkout_refund), {}, filled = false)
            PillButton(stringResource(R.string.checkout_cancel_order), {}, filled = false)
        }
    }
}

@Composable
private fun BoardLineRow(line: BoardLine, expanded: Boolean, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Text(
                stringResource(R.string.checkout_line_qty, line.quantity, line.name),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!expanded && line.collapsedRes != null) {
                Text(
                    stringResource(line.collapsedRes),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted,
                    maxLines = 1,
                    softWrap = false,
                )
            }
            Text(
                stringResource(if (expanded) R.string.checkout_expand else R.string.checkout_collapse),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
            )
            Spacer(Modifier.weight(1f))
            Text(
                formatMoney(line.lineCents),
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                softWrap = false,
            )
        }
        if (expanded && (line.details.isNotEmpty() || line.mutedDetail != null)) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                Box(
                    Modifier
                        .width(AppTheme.border.thick)
                        .height(AppTheme.sizes.logoMark)
                        .background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
                )
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                    line.details.forEach { detail ->
                        Text(
                            stringResource(detail),
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    line.mutedDetail?.let { detail ->
                        Text(
                            stringResource(detail),
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun OrderBoardPreview() {
    PreviewSurface {
        OrderBoardContent(
            CheckoutUiState(section = KitchenSection.Orders),
            BoardActions({}, {}, {}, {}, {}, {}, {}),
        )
    }
}
