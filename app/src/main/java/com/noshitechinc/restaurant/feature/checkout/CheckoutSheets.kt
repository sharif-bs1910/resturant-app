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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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

@Composable
fun CheckoutOverlayHost(state: CheckoutUiState, actions: OverlayActions) {
    if (state.overlay == CheckoutOverlay.None) return
    Box(
        Modifier
            .fillMaxSize()
            .background(AppTheme.colors.textPrimary.copy(alpha = 0.45f))
            .clickable(onClick = actions.onDismiss)
            .padding(AppTheme.spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.clickable(onClick = {})) {
            when (state.overlay) {
                CheckoutOverlay.Modifiers -> state.draft?.let { ModifierSheet(it, actions) }
                CheckoutOverlay.Tender -> TenderSheet(state, actions)
                CheckoutOverlay.GiftCard -> GiftCardSheet(state, actions)
                CheckoutOverlay.Address -> AddressSheet(state, actions)
                CheckoutOverlay.Draft -> DraftSheet(state, actions)
                CheckoutOverlay.Saved -> SavedSheet(state, actions.onBackToOrders)
                CheckoutOverlay.None -> Unit
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ModifierSheet(draft: ModifierDraft, actions: OverlayActions) {
    val unit = unitPrice(draft.item, draft.selected)
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.checkoutSheetWidth), scroll = false) {
        SheetHeader(draft.item.name, stringResource(R.string.checkout_base, formatMoney(draft.item.priceCents)), actions.onDismiss)
        Column(
            Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        ) {
            modifierGroups().forEach { group ->
                Text(stringResource(group.titleRes), style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                ) {
                    group.options.forEach { option ->
                        CheckoutChip(
                            label = stringResource(option.labelRes),
                            selected = option.id in draft.selected,
                            onClick = { actions.onToggleOption(group, option.id) },
                        )
                    }
                }
            }
            Text(stringResource(R.string.checkout_notes), style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
            NoteField(draft.note, actions.onDraftNote)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                Stepper(draft.quantity, actions.onDraftQuantity)
                PillButton(
                    stringResource(R.string.checkout_add_to_order, formatMoney(unit * draft.quantity)),
                    actions.onAddToOrder,
                    Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TenderSheet(state: CheckoutUiState, actions: OverlayActions) {
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)) {
        SheetHeader(
            stringResource(R.string.checkout_tender_title),
            stringResource(
                R.string.checkout_tender_context,
                state.orderNumber,
                stringResource(state.channel.labelRes),
                stringResource(state.fulfillment.labelRes),
            ),
            actions.onDismiss,
        )
        Text(stringResource(R.string.checkout_amount_due), style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
        Text(formatMoney(state.dueCents), style = AppTheme.typography.numericDisplay, color = AppTheme.colors.primary)
        Text(stringResource(R.string.checkout_settlement), style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            CheckoutChip(stringResource(R.string.checkout_pay_cash), state.settlement == Settlement.Cash) {
                actions.onSettlement(Settlement.Cash)
            }
            CheckoutChip(stringResource(R.string.checkout_pay_card), state.settlement == Settlement.Card) {
                actions.onSettlement(Settlement.Card)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.radius.sm))
                .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.sm))
                .padding(AppTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    stringResource(R.string.checkout_payment_link),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.textPrimary,
                )
                Text(stringResource(R.string.checkout_awaiting), style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
            }
            Text(stringResource(R.string.checkout_link_body), style = AppTheme.typography.bodySmall, color = AppTheme.colors.textMuted)
            Text(
                "${state.customerName}  ${state.customerPhone}  ${stringResource(R.string.checkout_edit)}",
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.textPrimary,
            )
        }
        PillButton(stringResource(R.string.checkout_redeem), actions.onRedeemGiftCard, Modifier.fillMaxWidth(), filled = false)
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(stringResource(R.string.checkout_back_to_cart), actions.onDismiss, Modifier.weight(1f), filled = false)
            PillButton(stringResource(R.string.checkout_send_link), actions.onSendPaymentLink, Modifier.weight(1f))
        }
    }
}

@Composable
private fun GiftCardSheet(state: CheckoutUiState, actions: OverlayActions) {
    val applied = minOf(GiftCardBalanceCents, state.totalCents)
    val remaining = (state.totalCents - applied).coerceAtLeast(0)
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.checkoutSheetWidth)) {
        SheetHeader(
            stringResource(R.string.checkout_redeem),
            stringResource(R.string.checkout_gift_context, state.orderNumber, formatMoney(state.totalCents)),
            actions.onDismiss,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl), modifier = Modifier.fillMaxWidth()) {
            val keyGap = AppTheme.spacing.md
            Column(
                Modifier.width(AppTheme.sizes.passcodeKeyWidth * 3 + keyGap * 2),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GiftField(stringResource(R.string.checkout_gift_number), state.giftNumber, state.giftField == GiftField.Number) {
                    actions.onGiftField(GiftField.Number)
                }
                GiftField(stringResource(R.string.checkout_gift_pin), state.giftPin, state.giftField == GiftField.Pin) {
                    actions.onGiftField(GiftField.Pin)
                }
                GiftKeypad(actions.onGiftDigit, actions.onGiftDelete)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AppTheme.radius.sm))
                        .border(
                            AppTheme.border.thin,
                            AppTheme.colors.textPrimary.copy(alpha = 0.14f),
                            RoundedCornerShape(AppTheme.radius.sm),
                        )
                        .padding(AppTheme.spacing.lg),
                ) {
                    Text(
                        stringResource(R.string.checkout_gift_balance),
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colors.textMuted,
                    )
                    Text(formatMoney(GiftCardBalanceCents), style = AppTheme.typography.headlineSmall, color = AppTheme.colors.textPrimary)
                    Text(
                        stringResource(R.string.checkout_available),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textMuted,
                    )
                }
                MoneyLine(stringResource(R.string.checkout_total), formatMoney(state.totalCents), false)
                MoneyLine(stringResource(R.string.checkout_gift_card), formatMoney(-applied), false)
                MoneyLine(stringResource(R.string.checkout_remaining), formatMoney(remaining), true)
                Text(
                    stringResource(R.string.checkout_gift_helper),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(
                stringResource(R.string.checkout_back),
                actions.onBackToTender,
                Modifier.width(AppTheme.sizes.heroHeight / 2),
                filled = false,
            )
            PillButton(stringResource(R.string.checkout_apply_amount, formatMoney(applied)), actions.onApplyGiftCard, Modifier.weight(1f))
        }
    }
}

@Composable
private fun AddressSheet(state: CheckoutUiState, actions: OverlayActions) {
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Text(
                stringResource(R.string.checkout_address_title),
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                stringResource(R.string.checkout_address_subtitle),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
            )
        }
        AddressInput(stringResource(R.string.checkout_street), state.street, state.addressField == AddressField.Street, actions.onStreet)
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            AddressInput(
                stringResource(R.string.checkout_apt),
                state.apt,
                state.addressField == AddressField.Apt,
                actions.onApt,
                Modifier.weight(1f),
            )
            AddressInput(
                stringResource(R.string.checkout_zip),
                state.zip,
                state.addressField == AddressField.Zip,
                actions.onZip,
                Modifier.width(AppTheme.sizes.passcodeKeyWidth + AppTheme.sizes.minTouchTarget),
            )
        }
        AddressInput(
            stringResource(R.string.checkout_delivery_notes),
            state.deliveryNotes,
            state.addressField == AddressField.Notes,
            actions.onDeliveryNotes,
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Box(Modifier.size(AppTheme.spacing.sm).clip(CircleShape).background(AppTheme.colors.success))
            Text(
                stringResource(R.string.checkout_radius),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.success,
                maxLines = 1,
                softWrap = false,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(stringResource(R.string.checkout_keep_pickup), actions.onKeepAsPickup, Modifier.weight(1f), filled = false)
            PillButton(stringResource(R.string.checkout_save_address), actions.onSaveAddress, Modifier.weight(1f))
        }
    }
}

@Composable
private fun DraftSheet(state: CheckoutUiState, actions: OverlayActions) {
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)) {
        SheetHeader(
            stringResource(R.string.checkout_draft_title),
            stringResource(
                R.string.checkout_tender_context,
                state.orderNumber,
                stringResource(state.channel.labelRes),
                stringResource(state.fulfillment.labelRes),
            ),
            actions.onDismiss,
        )
        Text(stringResource(R.string.checkout_amount_due), style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
        Text(formatMoney(state.dueCents), style = AppTheme.typography.numericDisplay, color = AppTheme.colors.primary)
        Text(stringResource(R.string.checkout_saved_body), style = AppTheme.typography.bodySmall, color = AppTheme.colors.textMuted)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.radius.sm))
                .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.sm))
                .padding(AppTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    stringResource(R.string.checkout_payment_link),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.textPrimary,
                )
                Text(
                    stringResource(R.string.checkout_awaiting),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textMuted,
                    maxLines = 1,
                    softWrap = false,
                )
            }
            Text(stringResource(R.string.checkout_link_body), style = AppTheme.typography.bodySmall, color = AppTheme.colors.textMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    state.customerName,
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    state.customerPhone,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                    softWrap = false,
                )
                Text(stringResource(R.string.checkout_edit), style = AppTheme.typography.labelMedium, color = AppTheme.colors.textMuted)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(stringResource(R.string.checkout_close_action), actions.onDismiss, Modifier.weight(1f), filled = false)
            PillButton(
                stringResource(R.string.checkout_send_link_to, phoneTail(state.customerPhone)),
                actions.onSendPaymentLink,
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun AddressInput(label: String, value: String, focused: Boolean, onValue: (String) -> Unit, modifier: Modifier = Modifier) {
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

@Composable
private fun SavedSheet(state: CheckoutUiState, onDone: () -> Unit) {
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                Modifier.size(AppTheme.sizes.logoMark).clip(CircleShape).background(AppTheme.colors.success),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = AppTheme.colors.card)
            }
        }
        Text(
            stringResource(R.string.checkout_payment_sent_title),
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppTheme.radius.sm))
                .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.sm))
                .padding(AppTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            RecapLine(stringResource(R.string.checkout_recap_channel), stringResource(state.channel.labelRes))
            RecapLine(
                stringResource(R.string.checkout_recap_fulfillment),
                stringResource(
                    if (state.fulfillment == Fulfillment.Pickup) R.string.checkout_ready_pickup else R.string.checkout_ready_delivery,
                ),
            )
            RecapLine(stringResource(R.string.checkout_recap_items), state.itemCount.toString())
            RecapLine(stringResource(R.string.checkout_total), formatMoney(state.totalCents))
            RecapLine(stringResource(R.string.checkout_recap_payment), stringResource(R.string.checkout_link_sent))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            PillButton(stringResource(R.string.checkout_print_ticket), {}, Modifier.weight(1f), filled = false)
            PillButton(stringResource(R.string.checkout_print_receipt), {}, Modifier.weight(1f), filled = false)
        }
        PillButton(stringResource(R.string.checkout_close_action), onDone, Modifier.fillMaxWidth())
    }
}

@Composable
private fun RecapLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
        Text(value, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textPrimary, maxLines = 1, softWrap = false)
    }
}

@Composable
private fun SheetCard(modifier: Modifier = Modifier, scroll: Boolean = true, content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints {
        Column(
            modifier = modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .heightIn(max = maxHeight)
                .clip(RoundedCornerShape(AppTheme.radius.md))
                .background(AppTheme.colors.card)
                .then(if (scroll) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(AppTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            content = content,
        )
    }
}

@Composable
private fun SheetHeader(title: String, subtitle: String, onClose: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                subtitle,
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .size(AppTheme.sizes.minTouchTarget)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.checkout_close),
                tint = AppTheme.colors.textMuted,
                modifier = Modifier.size(AppTheme.sizes.iconMd),
            )
        }
    }
}

@Composable
private fun NoteField(value: String, onValue: (String) -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    BasicTextField(
        value = value,
        onValueChange = onValue,
        textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.textPrimary),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.sizes.inputHeight)
            .clip(shape)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(AppTheme.spacing.md),
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(stringResource(R.string.checkout_note_hint), style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
            }
            inner()
        },
    )
}

@Composable
private fun GiftField(label: String, value: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Column(Modifier.fillMaxWidth().clickable(onClick = onClick), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
        Text(label, style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted)
        Text(
            text = value,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(
                    if (selected) AppTheme.border.focus else AppTheme.border.thin,
                    if (selected) AppTheme.colors.primary else AppTheme.colors.textPrimary.copy(alpha = 0.14f),
                    shape,
                )
                .padding(AppTheme.spacing.md),
        )
    }
}

@Composable
private fun GiftKeypad(onDigit: (String) -> Unit, onDelete: () -> Unit) {
    val rows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("", "0", "del"))
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                row.forEach { key ->
                    val shape = RoundedCornerShape(AppTheme.radius.sm)
                    Box(
                        modifier = Modifier
                            .width(AppTheme.sizes.passcodeKeyWidth)
                            .heightIn(min = AppTheme.sizes.minTouchTarget)
                            .then(
                                if (key.isEmpty()) {
                                    Modifier
                                } else {
                                    Modifier.clip(
                                        shape,
                                    ).border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape).clickable {
                                        if (key == "del") onDelete() else onDigit(key)
                                    }
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (key == "del") {
                            Icon(
                                Icons.AutoMirrored.Outlined.Backspace,
                                stringResource(R.string.checkout_delete),
                                tint = AppTheme.colors.textMuted,
                            )
                        } else if (key.isNotEmpty()) {
                            Text(key, style = AppTheme.typography.headlineSmall, color = AppTheme.colors.textPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoneyLine(label: String, value: String, accent: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (accent) AppTheme.typography.titleMedium else AppTheme.typography.bodyMedium,
            color = if (accent) AppTheme.colors.textPrimary else AppTheme.colors.textMuted,
        )
        Text(
            value,
            style = if (accent) AppTheme.typography.priceMedium else AppTheme.typography.bodyMedium,
            color = if (accent) AppTheme.colors.primary else AppTheme.colors.textPrimary,
        )
    }
}

data class OverlayActions(
    val onDismiss: () -> Unit,
    val onToggleOption: (ModifierGroup, String) -> Unit,
    val onDraftQuantity: (Int) -> Unit,
    val onDraftNote: (String) -> Unit,
    val onAddToOrder: () -> Unit,
    val onSettlement: (Settlement) -> Unit,
    val onRedeemGiftCard: () -> Unit,
    val onSendPaymentLink: () -> Unit,
    val onGiftField: (GiftField) -> Unit,
    val onGiftDigit: (String) -> Unit,
    val onGiftDelete: () -> Unit,
    val onApplyGiftCard: () -> Unit,
    val onBackToTender: () -> Unit,
    val onBackToOrders: () -> Unit,
    val onStreet: (String) -> Unit = {},
    val onApt: (String) -> Unit = {},
    val onZip: (String) -> Unit = {},
    val onDeliveryNotes: (String) -> Unit = {},
    val onKeepAsPickup: () -> Unit = {},
    val onSaveAddress: () -> Unit = {},
)

@ScreenPreviews
@Composable
private fun TenderPreview() {
    PreviewSurface {
        TenderSheet(
            CheckoutUiState(step = CheckoutStep.Building, lines = SampleCart, channel = OrderChannel.Phone),
            emptyOverlay(),
        )
    }
}

@ScreenPreviews
@Composable
private fun DraftPreview() {
    PreviewSurface {
        DraftSheet(
            CheckoutUiState(step = CheckoutStep.Building, lines = PhoneCart, channel = OrderChannel.Phone),
            emptyOverlay(),
        )
    }
}

@ScreenPreviews
@Composable
private fun AddressPreview() {
    PreviewSurface {
        AddressSheet(
            CheckoutUiState(
                step = CheckoutStep.Building,
                lines = PhoneCart,
                channel = OrderChannel.Phone,
                fulfillment = Fulfillment.Delivery,
            ),
            emptyOverlay(),
        )
    }
}

private fun emptyOverlay() = OverlayActions({}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
