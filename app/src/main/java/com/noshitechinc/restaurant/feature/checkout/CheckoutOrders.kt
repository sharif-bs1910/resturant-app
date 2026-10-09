package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Icon
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

data class BoardActions(
    val onFilter: (BoardFilter) -> Unit,
    val onSearch: (String) -> Unit,
    val onSelect: (String) -> Unit,
    val onToggleLine: (String) -> Unit,
    val onMarkComplete: () -> Unit,
    val onToggleFulfillment: () -> Unit,
    val onClose: () -> Unit,
    val onAddCharge: () -> Unit = {},
    val onRefund: () -> Unit = {},
    val onCancel: () -> Unit = {},
    val onDismissDialog: () -> Unit = {},
    val onChargeAmount: (String) -> Unit = {},
    val onChargeReason: (String) -> Unit = {},
    val onRefundAmount: (String) -> Unit = {},
    val onRefundReason: (String) -> Unit = {},
    val onBoardStreet: (String) -> Unit = {},
    val onBoardApt: (String) -> Unit = {},
    val onBoardZip: (String) -> Unit = {},
    val onBoardNotes: (String) -> Unit = {},
    val onConfirmCharge: () -> Unit = {},
    val onConfirmRefund: () -> Unit = {},
    val onConfirmCancel: () -> Unit = {},
    val onKeepPickup: () -> Unit = {},
    val onSaveAddress: () -> Unit = {},
    val onNewOrder: () -> Unit = {},
)

@Composable
fun OrderBoardContent(state: CheckoutUiState, actions: BoardActions) {
    val canceled = state.canceledOrder
    if (canceled != null) {
        CanceledOrderContent(canceled, actions.onNewOrder)
        return
    }
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
            QuietButton(stringResource(R.string.checkout_add_charge), actions.onAddCharge)
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
            PillButton(stringResource(R.string.checkout_refund), actions.onRefund, filled = false)
            PillButton(stringResource(R.string.checkout_cancel_order), actions.onCancel, filled = false)
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

@Composable
fun BoardDialogHost(state: CheckoutUiState, actions: BoardActions) {
    val order = state.selectedBoard ?: return
    if (state.boardDialog == BoardDialog.None) return
    Box(
        Modifier
            .fillMaxSize()
            .background(AppTheme.colors.textPrimary.copy(alpha = 0.45f))
            .clickable(onClick = actions.onDismissDialog)
            .padding(AppTheme.spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.clickable(onClick = {})) {
            when (state.boardDialog) {
                BoardDialog.Charge -> ChargeDialog(order, state, actions)
                BoardDialog.Refund -> RefundDialog(order, state, actions)
                BoardDialog.Address -> BoardAddressDialog(order, state, actions)
                BoardDialog.Cancel -> CancelOrderDialog(order, actions)
                BoardDialog.None -> Unit
            }
        }
    }
}

@Composable
private fun ChargeDialog(order: BoardOrder, state: CheckoutUiState, actions: BoardActions) {
    BoardDialogCard {
        DialogCopy(
            stringResource(R.string.checkout_charge_title, order.number),
            stringResource(R.string.checkout_charge_body, order.detailGuest),
        )
        BoardFieldInput(
            stringResource(R.string.checkout_amount),
            state.chargeAmount,
            state.boardField == BoardField.Amount,
            actions.onChargeAmount,
        )
        BoardFieldInput(
            stringResource(R.string.checkout_reason),
            state.chargeReason,
            state.boardField == BoardField.Reason,
            actions.onChargeReason,
        )
        DialogButtons(
            confirm = stringResource(R.string.checkout_add_charge_amount, formatMoney(parseDollars(state.chargeAmount))),
            onCancel = actions.onDismissDialog,
            onConfirm = actions.onConfirmCharge,
        )
    }
}

@Composable
private fun RefundDialog(order: BoardOrder, state: CheckoutUiState, actions: BoardActions) {
    BoardDialogCard {
        DialogCopy(
            stringResource(R.string.checkout_refund_title, order.number),
            stringResource(R.string.checkout_refund_body, order.detailGuest),
        )
        BoardFieldInput(
            stringResource(R.string.checkout_refund_amount),
            state.refundAmount,
            state.boardField == BoardField.Amount,
            actions.onRefundAmount,
        )
        BoardFieldInput(
            stringResource(R.string.checkout_reason),
            state.refundReason,
            state.boardField == BoardField.Reason,
            actions.onRefundReason,
        )
        DialogButtons(
            confirm = stringResource(R.string.checkout_refund_confirm, formatMoney(parseDollars(state.refundAmount))),
            onCancel = actions.onDismissDialog,
            onConfirm = actions.onConfirmRefund,
            destructive = true,
        )
    }
}

@Composable
private fun BoardAddressDialog(order: BoardOrder, state: CheckoutUiState, actions: BoardActions) {
    BoardDialogCard {
        DialogCopy(
            stringResource(R.string.checkout_address_title),
            stringResource(R.string.checkout_board_address_body, order.number, order.detailGuest),
        )
        BoardFieldInput(
            stringResource(R.string.checkout_street),
            state.boardStreet,
            state.boardField == BoardField.Street,
            actions.onBoardStreet,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            BoardFieldInput(
                stringResource(R.string.checkout_apt),
                state.boardApt,
                state.boardField == BoardField.Apt,
                actions.onBoardApt,
                Modifier.weight(1f),
            )
            BoardFieldInput(
                stringResource(R.string.checkout_zip),
                state.boardZip,
                state.boardField == BoardField.Zip,
                actions.onBoardZip,
                Modifier.width(AppTheme.sizes.passcodeKeyWidth + AppTheme.sizes.minTouchTarget),
            )
        }
        BoardFieldInput(
            stringResource(R.string.checkout_delivery_notes),
            state.boardNotes,
            state.boardField == BoardField.Notes,
            actions.onBoardNotes,
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Box(Modifier.size(AppTheme.spacing.sm).clip(CircleShape).background(AppTheme.colors.success))
            Text(
                stringResource(R.string.checkout_radius),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.success,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DialogButtons(
            confirm = stringResource(R.string.checkout_save_address),
            cancel = stringResource(R.string.checkout_keep_pickup),
            onCancel = actions.onKeepPickup,
            onConfirm = actions.onSaveAddress,
        )
    }
}

@Composable
private fun CancelOrderDialog(order: BoardOrder, actions: BoardActions) {
    BoardDialogCard(Modifier.widthIn(max = AppTheme.sizes.checkoutCartWidth)) {
        DialogCopy(
            stringResource(R.string.checkout_cancel_title, order.number),
            stringResource(R.string.checkout_cancel_body, formatMoney(order.totalCents), order.detailGuest),
        )
        DialogButtons(
            confirm = stringResource(R.string.checkout_cancel_refund),
            onCancel = actions.onDismissDialog,
            onConfirm = actions.onConfirmCancel,
            destructive = true,
        )
    }
}

@Composable
private fun CanceledOrderContent(order: BoardOrder, onNewOrder: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(AppTheme.spacing.xl), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .width(AppTheme.sizes.dialogMaxWidth)
                .clip(RoundedCornerShape(AppTheme.radius.lg))
                .background(AppTheme.colors.card)
                .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.lg))
                .padding(horizontal = AppTheme.spacing.xxxl, vertical = AppTheme.spacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        ) {
            Box(
                Modifier.size(AppTheme.sizes.logoMark).clip(CircleShape).background(AppTheme.colors.destructive),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.PriorityHigh, contentDescription = null, tint = AppTheme.colors.background)
            }
            Text(
                stringResource(R.string.checkout_canceled_heading, order.number),
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.checkout_canceled_body, formatMoney(order.totalCents), order.detailGuest),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
                textAlign = TextAlign.Center,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppTheme.radius.sm))
                    .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.sm))
                    .padding(AppTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                MoneyRow(stringResource(R.string.checkout_recap_channel), stringResource(R.string.checkout_phone))
                MoneyRow(
                    stringResource(R.string.checkout_recap_fulfillment),
                    stringResource(R.string.checkout_canceled),
                    destructiveValue = true,
                )
                MoneyRow(stringResource(R.string.checkout_recap_items), order.lines.sumOf { it.quantity }.toString())
                MoneyRow(
                    stringResource(R.string.checkout_total),
                    formatMoney(-order.totalCents),
                    destructiveValue = true,
                )
                MoneyRow(stringResource(R.string.checkout_recap_payment), stringResource(R.string.checkout_refunded))
            }
            PillButton(stringResource(R.string.checkout_print_cancellation), {}, Modifier.fillMaxWidth(), filled = false)
            PillButton(stringResource(R.string.checkout_new_order), onNewOrder, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun BoardDialogCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier
            .widthIn(max = AppTheme.sizes.checkoutDetailWidth)
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.lg))
            .background(AppTheme.colors.card)
            .padding(AppTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        content = content,
    )
}

@Composable
private fun DialogCopy(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        Text(title, style = AppTheme.typography.titleLarge, color = AppTheme.colors.textPrimary)
        Text(body, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
    }
}

@Composable
private fun DialogButtons(
    confirm: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    cancel: String = stringResource(R.string.checkout_dialog_cancel),
    destructive: Boolean = false,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
        PillButton(cancel, onCancel, Modifier.weight(1f), filled = false)
        PillButton(confirm, onConfirm, Modifier.weight(1f), destructive = destructive)
    }
}

@Composable
private fun BoardFieldInput(label: String, value: String, focused: Boolean, onValue: (String) -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        Text(label, style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted, maxLines = 1, softWrap = false)
        BasicTextField(
            value = value,
            onValueChange = onValue,
            textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.textPrimary),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AppTheme.sizes.minTouchTarget)
                .clip(shape)
                .border(
                    if (focused) AppTheme.border.medium else AppTheme.border.thin,
                    if (focused) AppTheme.colors.primary else AppTheme.colors.textPrimary.copy(alpha = 0.14f),
                    shape,
                )
                .padding(AppTheme.spacing.md),
            decorationBox = { inner ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) { inner() }
                    if (focused) {
                        Box(
                            Modifier
                                .size(width = AppTheme.border.medium, height = AppTheme.sizes.iconSm)
                                .background(AppTheme.colors.primary),
                        )
                    }
                }
            },
        )
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

@ScreenPreviews
@Composable
private fun ChargeDialogPreview() {
    PreviewSurface {
        BoardDialogHost(
            CheckoutUiState(section = KitchenSection.Orders, boardDialog = BoardDialog.Charge),
            BoardActions({
            }, {}, {}, {}, {}, {}, {}),
        )
    }
}

@ScreenPreviews
@Composable
private fun CanceledOrderPreview() {
    PreviewSurface {
        OrderBoardContent(
            CheckoutUiState(section = KitchenSection.Orders, canceledOrder = DefaultBoard.first { it.number == "1042" }),
            BoardActions({}, {}, {}, {}, {}, {}, {}),
        )
    }
}
