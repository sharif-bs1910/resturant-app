package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.input.CurrencyField
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun CustomerSheet(state: CheckoutUiState, actions: OverlayActions) {
    val updating = state.hasCustomer
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)) {
        SheetHeader(
            stringResource(
                if (updating) R.string.checkout_customer_update_title else R.string.checkout_customer_title,
            ),
            stringResource(R.string.checkout_customer_subtitle),
            actions.onDismiss,
        )
        AddressInput(
            stringResource(R.string.checkout_customer_phone),
            state.customerDraftPhone,
            focused = true,
            onValue = actions.onCustomerPhone,
            placeholder = stringResource(R.string.checkout_customer_phone_hint),
        )
        AddressInput(
            stringResource(R.string.checkout_customer_name),
            state.customerDraftName,
            focused = false,
            onValue = actions.onCustomerName,
            placeholder = stringResource(R.string.checkout_customer_name_hint),
        )
        PillButton(
            stringResource(
                if (updating) R.string.checkout_customer_save else R.string.checkout_customer_continue,
            ),
            actions.onSaveCustomer,
            Modifier.fillMaxWidth(),
            enabled = state.customerDraftPhone.isNotBlank() || state.customerDraftName.isNotBlank(),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ModifierSheet(draft: ModifierDraft, actions: OverlayActions) {
    val unit = unitPrice(draft.item, draft.selected)
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val sheetMaxWidth = AppTheme.sizes.passcodeKeyWidth * 11 + AppTheme.spacing.xl
    val groupGap = AppTheme.spacing.lg + AppTheme.spacing.xs
    BoxWithConstraints {
        val sideBySide = maxWidth >= AppTheme.sizes.checkoutSheetWidth
        val sheetMaxHeight = this.maxHeight
        Box(
            modifier = Modifier
                .widthIn(max = sheetMaxWidth)
                .fillMaxWidth()
                .heightIn(max = sheetMaxHeight)
                .shadow(elevation = AppTheme.spacing.xl, shape = shape)
                .clip(shape)
                .background(AppTheme.colors.card),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .heightIn(max = sheetMaxHeight),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxl),
            ) {
                if (sideBySide) {
                    Box(
                        modifier = Modifier
                            .width(AppTheme.sizes.checkoutCartWidth)
                            .fillMaxHeight(),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.checkout_menu_photo),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                        )
                    }
                }
                Column(
                    Modifier
                        .weight(1f)
                        .heightIn(max = sheetMaxHeight)
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = if (sideBySide) AppTheme.spacing.none else AppTheme.spacing.xl,
                            end = AppTheme.spacing.xl,
                            top = AppTheme.spacing.xl,
                            bottom = AppTheme.spacing.xl,
                        ),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg + AppTheme.spacing.xs),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                        Text(
                            draft.item.name,
                            style = AppTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = AppTheme.colors.textPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            stringResource(R.string.checkout_item_blurb_spicy),
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            stringResource(R.string.checkout_base, formatMoney(draft.item.priceCents)),
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textPrimary,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(groupGap)) {
                        modifierGroups().forEach { group ->
                            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm + AppTheme.spacing.xxs)) {
                                Text(
                                    stringResource(group.titleRes),
                                    style = AppTheme.typography.labelSmall,
                                    color = AppTheme.colors.textMuted,
                                )
                                if (group.single) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                                    ) {
                                        group.options.forEach { option ->
                                            CheckoutChip(
                                                label = stringResource(option.labelRes),
                                                selected = option.id in draft.selected,
                                                sheet = true,
                                                onClick = { actions.onToggleOption(group, option.id) },
                                                modifier = Modifier.weight(1f),
                                            )
                                        }
                                    }
                                } else {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                                    ) {
                                        group.options.forEach { option ->
                                            CheckoutChip(
                                                label = stringResource(option.labelRes),
                                                selected = option.id in draft.selected,
                                                sheet = true,
                                                hug = true,
                                                onClick = { actions.onToggleOption(group, option.id) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm + AppTheme.spacing.xxs)) {
                            Text(
                                stringResource(R.string.checkout_notes),
                                style = AppTheme.typography.labelSmall,
                                color = AppTheme.colors.textMuted,
                            )
                            NoteField(draft.note, actions.onDraftNote)
                        }
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(AppTheme.border.thin)
                            .background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Stepper(draft.quantity, actions.onDraftQuantity)
                        PillButton(
                            stringResource(R.string.checkout_add_to_order, formatMoney(unit * draft.quantity)),
                            actions.onAddToOrder,
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = AppTheme.spacing.sm, end = AppTheme.spacing.sm)
                    .size(AppTheme.sizes.minTouchTarget)
                    .clickable(onClick = actions.onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.checkout_close),
                    tint = AppTheme.colors.textPrimary,
                    modifier = Modifier.size(AppTheme.sizes.iconMd),
                )
            }
        }
    }
}

@Composable
internal fun AddressSheet(state: CheckoutUiState, actions: OverlayActions) {
    SheetCard(Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)) {
        SheetHeader(
            stringResource(R.string.checkout_address_title),
            stringResource(R.string.checkout_address_subtitle),
            actions.onDismiss,
        )
        AddressInput(
            stringResource(R.string.checkout_street),
            state.street,
            state.addressField == AddressField.Street,
            actions.onStreet,
            placeholder = stringResource(R.string.checkout_street_hint),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            AddressInput(
                stringResource(R.string.checkout_apt),
                state.apt,
                state.addressField == AddressField.Apt,
                actions.onApt,
                Modifier.weight(1f),
                placeholder = stringResource(R.string.checkout_apt_hint),
            )
            AddressInput(
                stringResource(R.string.checkout_city),
                state.city,
                state.addressField == AddressField.City,
                actions.onCity,
                Modifier.weight(1f),
                placeholder = stringResource(R.string.checkout_city_hint),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            AddressInput(
                stringResource(R.string.checkout_region),
                state.region,
                state.addressField == AddressField.Region,
                actions.onRegion,
                Modifier.weight(1f),
                placeholder = stringResource(R.string.checkout_region_hint),
            )
            AddressInput(
                stringResource(R.string.checkout_zip),
                state.zip,
                state.addressField == AddressField.Zip,
                actions.onZip,
                Modifier.weight(1f),
                placeholder = stringResource(R.string.checkout_zip_hint),
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
        PillButton(
            stringResource(R.string.checkout_save_address),
            actions.onSaveAddress,
            Modifier.fillMaxWidth(),
            enabled = state.street.isNotBlank(),
        )
    }
}

@Composable
internal fun AddressInput(
    label: String,
    value: String,
    focused: Boolean,
    onValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        Text(
            label,
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.textPrimary.copy(alpha = 0.78f),
            maxLines = 1,
            softWrap = false,
        )
        BasicTextField(
            value = value,
            onValueChange = onValue,
            textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.textPrimary),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = AppTheme.sizes.minTouchTarget)
                .clip(shape)
                .background(AppTheme.colors.background)
                .border(
                    if (focused) AppTheme.border.medium else AppTheme.border.thin,
                    if (focused) AppTheme.colors.primary else AppTheme.colors.textPrimary.copy(alpha = 0.12f),
                    shape,
                )
                .padding(AppTheme.spacing.md),
            decorationBox = { inner ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(placeholder, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
                        }
                        inner()
                    }
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
internal fun DraftSheet(state: CheckoutUiState, actions: OverlayActions) {
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
        PaidProgressBar(state.paidProgress)
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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    listOf(state.customerName, state.customerPhone).filter { it.isNotBlank() }.joinToString("  "),
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    stringResource(R.string.checkout_edit),
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textMuted,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.clickable(onClick = actions.onEditPaymentLink),
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
            PillButton(stringResource(R.string.checkout_close_action), actions.onDismiss, Modifier.weight(1f), filled = false)
            PillButton(
                stringResource(R.string.checkout_send_link_to, phoneTail(state.customerPhone)),
                actions.onOpenPaymentLink,
                Modifier.weight(1f),
            )
        }
    }
}
