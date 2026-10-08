package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun IdleContent(state: CheckoutUiState, onSearch: (String) -> Unit, onNewOrder: () -> Unit, onOpenOrder: (OpenOrder) -> Unit) {
    val spacing = AppTheme.spacing
    Column(
        modifier = Modifier.fillMaxSize().padding(spacing.xl),
        verticalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        OpenOrdersHeader(state.visibleOrders.size, state.search, onSearch)
        OpenOrderCards(state.visibleOrders, onOpenOrder)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .widthIn(max = AppTheme.sizes.checkoutIdleCardWidth)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppTheme.radius.md))
                    .background(AppTheme.colors.card)
                    .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.md))
                    .padding(horizontal = AppTheme.spacing.xxxl, vertical = AppTheme.spacing.xxl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Text(
                    stringResource(R.string.checkout_start_title),
                    style = AppTheme.typography.headlineSmall,
                    color = AppTheme.colors.textPrimary,
                )
                Text(
                    text = stringResource(R.string.checkout_start_body),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = AppTheme.sizes.checkoutCartWidth),
                )
                PillButton(
                    stringResource(R.string.checkout_new_order),
                    onNewOrder,
                    Modifier.width(AppTheme.sizes.authPanelPadding * 3 + AppTheme.spacing.sm),
                )
            }
        }
    }
}

@Composable
private fun OpenOrdersHeader(count: Int, search: String, onSearch: (String) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val searchBeside = maxWidth >= AppTheme.sizes.checkoutSearchWidth + AppTheme.sizes.checkoutRailWidth
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                Text(
                    text = stringResource(R.string.checkout_open_orders),
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                    softWrap = false,
                )
                Text(
                    text = stringResource(R.string.checkout_active_count, count),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textMuted,
                    maxLines = 1,
                    softWrap = false,
                )
                if (searchBeside) {
                    Box(Modifier.weight(1f))
                    SearchField(search, onSearch, Modifier.width(AppTheme.sizes.checkoutSearchWidth))
                }
            }
            if (!searchBeside) SearchField(search, onSearch, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun OpenOrderCards(orders: List<OpenOrder>, onOpenOrder: (OpenOrder) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val minCard = AppTheme.sizes.checkoutCartWidth * 2 / 3
        val columns = when {
            maxWidth >= minCard * 3 -> 3
            maxWidth >= minCard * 2 -> 2
            else -> 1
        }
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
            orders.chunked(columns).forEach { row ->
                Row(
                    Modifier.fillMaxWidth().height(androidx.compose.foundation.layout.IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
                ) {
                    row.forEach { order ->
                        OpenOrderCard(order, onClick = { onOpenOrder(order) }, modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                    repeat(columns - row.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
fun BuilderContent(state: CheckoutUiState, actions: CheckoutActions) {
    BoxWithConstraints(Modifier.fillMaxSize().padding(AppTheme.spacing.xl)) {
        val sideBySide = maxWidth >= AppTheme.sizes.checkoutCartWidth * 2
        if (sideBySide) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)) {
                MenuPane(state, actions.onCategory, actions.onOpenItem, Modifier.weight(1f).fillMaxHeight(), scrollItems = true)
                CartPane(state, actions, Modifier.width(AppTheme.sizes.checkoutCartWidth).fillMaxHeight(), pinFooter = true)
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
            ) {
                MenuPane(state, actions.onCategory, actions.onOpenItem, Modifier.fillMaxWidth(), scrollItems = false)
                CartPane(state, actions, Modifier.fillMaxWidth(), pinFooter = false)
            }
        }
    }
}

@Composable
private fun MenuPane(
    state: CheckoutUiState,
    onCategory: (MenuCategory) -> Unit,
    onOpenItem: (MenuItem) -> Unit,
    modifier: Modifier,
    scrollItems: Boolean,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
        Text(stringResource(R.string.checkout_menu), style = AppTheme.typography.titleMedium, color = AppTheme.colors.textPrimary)
        CategoryChips(state.category, onCategory)
        if (scrollItems) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
                modifier = Modifier.weight(1f),
            ) {
                items(state.visibleMenu, key = { it.id }) { item ->
                    MenuItemCard(item, onClick = { onOpenItem(item) })
                }
            }
        } else {
            MenuRows(state.visibleMenu, onOpenItem)
        }
    }
}

@Composable
private fun CategoryChips(selected: MenuCategory, onCategory: (MenuCategory) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stretch = maxWidth >= AppTheme.sizes.passcodeKeyWidth * MenuCategory.entries.size
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (stretch) Modifier else Modifier.horizontalScroll(rememberScrollState())),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            MenuCategory.entries.forEach { category ->
                CheckoutChip(
                    label = stringResource(category.labelRes),
                    selected = selected == category,
                    onClick = { onCategory(category) },
                    modifier = if (stretch) Modifier.weight(1f) else Modifier,
                )
            }
        }
    }
}

@Composable
private fun MenuRows(items: List<MenuItem>, onOpenItem: (MenuItem) -> Unit) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns = if (maxWidth >= AppTheme.sizes.cardImage * 4) 2 else 1
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
            items.chunked(columns).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                    row.forEach { item ->
                        MenuItemCard(item, onClick = { onOpenItem(item) }, modifier = Modifier.weight(1f))
                    }
                    repeat(columns - row.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun MenuItemCard(item: MenuItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .clickable(onClick = onClick)
            .padding(AppTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        Image(
            painter = painterResource(R.drawable.checkout_menu_photo),
            contentDescription = null,
            modifier = Modifier.size(AppTheme.sizes.cardImage).clip(RoundedCornerShape(AppTheme.radius.sm)),
            contentScale = ContentScale.Crop,
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs)) {
            Text(
                item.name,
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                stringResource(R.string.checkout_item_blurb),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                formatMoney(item.priceCents),
                style = AppTheme.typography.priceSmall,
                color = AppTheme.colors.primary,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@Composable
private fun CartPane(state: CheckoutUiState, actions: CheckoutActions, modifier: Modifier, pinFooter: Boolean) {
    val shape = RoundedCornerShape(AppTheme.radius.lg)
    val cardModifier = modifier
        .clip(shape)
        .background(AppTheme.colors.card)
        .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
        .padding(AppTheme.spacing.lg)
    if (pinFooter) {
        Column(cardModifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            ) {
                CartDetails(state, actions)
            }
            CartFooter(state, actions)
        }
    } else {
        Column(cardModifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            CartDetails(state, actions)
            CartFooter(state, actions)
        }
    }
}

@Composable
private fun CartDetails(state: CheckoutUiState, actions: CheckoutActions) {
    Text(stringResource(R.string.checkout_current_order), style = AppTheme.typography.titleMedium, color = AppTheme.colors.textPrimary)
    ChoiceRow(stringResource(R.string.checkout_channel), OrderChannel.entries, state.channel, actions.onChannel)
    ChoiceRow(stringResource(R.string.checkout_fulfillment), Fulfillment.entries, state.fulfillment, actions.onFulfillment)
    InfoRow("${state.customerName}  ${state.customerPhone}", stringResource(R.string.checkout_edit))
    if (state.fulfillment == Fulfillment.Delivery) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
            Text(
                stringResource(R.string.checkout_delivery_address),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppTheme.radius.sm))
                    .border(AppTheme.border.thin, AppTheme.colors.textPrimary, RoundedCornerShape(AppTheme.radius.sm))
                    .clickable(onClick = actions.onEditAddress)
                    .padding(AppTheme.spacing.md),
            ) {
                Text(
                    state.addressLine,
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    state.addressDetail,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        state.lines.forEach { line -> CartLineRow(line, actions) }
    }
}

@Composable
private fun CartFooter(state: CheckoutUiState, actions: CheckoutActions) {
    Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)))
    TotalRow(stringResource(R.string.checkout_subtotal), formatMoney(state.subtotalCents), emphasize = false)
    TotalRow(stringResource(R.string.checkout_tax), formatMoney(state.taxCents), emphasize = false)
    TotalRow(stringResource(R.string.checkout_total), formatMoney(state.totalCents), emphasize = true)
    PillButton(stringResource(R.string.checkout_pay_now), actions.onPay, Modifier.fillMaxWidth(), enabled = state.lines.isNotEmpty())
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clip(RoundedCornerShape(AppTheme.radius.pill))
            .background(AppTheme.colors.surface)
            .clickable(enabled = state.lines.isNotEmpty(), onClick = actions.onSaveDraft),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(R.string.checkout_save_draft), style = AppTheme.typography.labelLarge, color = AppTheme.colors.textPrimary)
    }
}

@Composable
private fun <T> ChoiceRow(label: String, options: List<T>, selected: T, onSelect: (T) -> Unit) where T : Enum<T> {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(label, style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted, modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            options.forEach { option ->
                val text = when (option) {
                    is OrderChannel -> stringResource(option.labelRes)
                    is Fulfillment -> stringResource(option.labelRes)
                    else -> option.name
                }
                CheckoutChip(text, option == selected) { onSelect(option) }
            }
        }
    }
}

@Composable
private fun CartLineRow(line: CartLine, actions: CheckoutActions) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                line.name,
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(formatMoney(line.unitCents * line.quantity), style = AppTheme.typography.priceSmall, color = AppTheme.colors.textPrimary)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Stepper(line.quantity, onChange = { actions.onLineQuantity(line.id, it) })
            Text(
                text = stringResource(R.string.checkout_remove),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
                modifier = Modifier.clickable { actions.onRemoveLine(line.id) },
            )
        }
    }
}

@Composable
fun Stepper(quantity: Int, onChange: (Int) -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Row(
        modifier = Modifier
            .clip(shape)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperKey(Icons.Filled.Remove, stringResource(R.string.checkout_decrease)) { onChange(quantity - 1) }
        Text(
            text = quantity.toString(),
            style = AppTheme.typography.titleSmall,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.width(AppTheme.sizes.stepperValueMinWidth),
            textAlign = TextAlign.Center,
        )
        StepperKey(Icons.Filled.Add, stringResource(R.string.checkout_increase)) { onChange(quantity + 1) }
    }
}

@Composable
private fun StepperKey(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, onClick: () -> Unit) {
    Box(
        Modifier.size(AppTheme.sizes.minTouchTarget).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = description, tint = AppTheme.colors.textPrimary)
    }
}

@Composable
fun ConfirmationContent(state: CheckoutUiState, onNewOrder: () -> Unit) {
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
                Modifier.size(
                    AppTheme.sizes.logoMark,
                ).clip(androidx.compose.foundation.shape.CircleShape).background(AppTheme.colors.success),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = AppTheme.colors.card)
            }
            Text(
                stringResource(R.string.checkout_confirmed_heading, state.orderNumber),
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.checkout_confirmed_body),
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
                RecapRow(stringResource(R.string.checkout_recap_channel), stringResource(state.channel.labelRes))
                RecapRow(
                    stringResource(R.string.checkout_recap_fulfillment),
                    stringResource(
                        if (state.fulfillment ==
                            Fulfillment.Pickup
                        ) {
                            R.string.checkout_ready_pickup
                        } else {
                            R.string.checkout_ready_delivery
                        },
                    ),
                )
                RecapRow(stringResource(R.string.checkout_recap_items), state.itemCount.toString())
                RecapRow(stringResource(R.string.checkout_total), formatMoney(state.totalCents))
                RecapRow(stringResource(R.string.checkout_recap_payment), stringResource(R.string.checkout_payment_sent))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md), modifier = Modifier.fillMaxWidth()) {
                PillButton(stringResource(R.string.checkout_print_ticket), {}, Modifier.weight(1f), filled = false)
                PillButton(stringResource(R.string.checkout_print_receipt), {}, Modifier.weight(1f), filled = false)
            }
            PillButton(stringResource(R.string.checkout_new_order), onNewOrder, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun RecapRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
        Text(value, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textPrimary)
    }
}

@Composable
private fun TotalRow(label: String, value: String, emphasize: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (emphasize) AppTheme.typography.titleMedium else AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
        )
        Text(
            value,
            style = if (emphasize) AppTheme.typography.priceMedium else AppTheme.typography.bodyMedium,
            color = if (emphasize) AppTheme.colors.primary else AppTheme.colors.textPrimary,
        )
    }
}

@Composable
private fun InfoRow(text: String, action: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.sm))
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary, RoundedCornerShape(AppTheme.radius.sm))
            .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text,
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            action,
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.textMuted,
        )
    }
}

@Composable
private fun SearchField(value: String, onValue: (String) -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Row(
        modifier = modifier
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(horizontal = AppTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = AppTheme.colors.textMuted,
            modifier = Modifier.size(AppTheme.sizes.iconSm),
        )
        BasicTextField(
            value = value,
            onValueChange = onValue,
            textStyle = AppTheme.typography.bodySmall.copy(color = AppTheme.colors.textPrimary),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(stringResource(R.string.checkout_search), style = AppTheme.typography.bodySmall, color = AppTheme.colors.textMuted)
                }
                inner()
            },
        )
    }
}

@Composable
private fun OpenOrderCard(order: OpenOrder, onClick: () -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val badge = when (order.status) {
        OpenOrderStatus.New -> AppTheme.colors.primary
        OpenOrderStatus.Cooking -> AppTheme.colors.warning
        OpenOrderStatus.Ready -> AppTheme.colors.success
        OpenOrderStatus.Completed -> AppTheme.colors.textMuted
    }
    val badgeText = if (order.status == OpenOrderStatus.Ready) AppTheme.colors.background else AppTheme.colors.textPrimary
    Column(
        modifier = modifier
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .clickable(onClick = onClick)
            .padding(AppTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "#${order.number} · ${order.guest}",
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                stringResource(order.status.labelRes),
                style = AppTheme.typography.labelSmall,
                color = badgeText,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .clip(RoundedCornerShape(AppTheme.radius.xs))
                    .background(badge)
                    .padding(horizontal = AppTheme.spacing.xs, vertical = AppTheme.spacing.xxs),
            )
        }
        Text(
            order.summary,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                order.meta,
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                formatMoney(order.totalCents),
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

data class CheckoutActions(
    val onCategory: (MenuCategory) -> Unit,
    val onChannel: (OrderChannel) -> Unit,
    val onFulfillment: (Fulfillment) -> Unit,
    val onOpenItem: (MenuItem) -> Unit,
    val onLineQuantity: (String, Int) -> Unit,
    val onRemoveLine: (String) -> Unit,
    val onPay: () -> Unit,
    val onSaveDraft: () -> Unit,
    val onEditAddress: () -> Unit,
)

@ScreenPreviews
@Composable
private fun IdleContentPreview() {
    PreviewSurface {
        IdleContent(CheckoutUiState(), onSearch = {}, onNewOrder = {}, onOpenOrder = {})
    }
}
