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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.adaptive.rememberAdaptiveInfo
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun BuilderContent(state: CheckoutUiState, actions: CheckoutActions) {
    BoxWithConstraints(Modifier.fillMaxSize().padding(AppTheme.spacing.xl)) {
        val sideBySide = maxWidth >= AppTheme.sizes.checkoutCartWidth * 2
        if (sideBySide) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)) {
                MenuPane(state, actions, Modifier.weight(1f).fillMaxHeight(), scrollItems = true)
                CartPane(state, actions, Modifier.width(AppTheme.sizes.checkoutCartWidth).fillMaxHeight(), pinFooter = true)
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl),
            ) {
                MenuPane(state, actions, Modifier.fillMaxWidth(), scrollItems = false)
                CartPane(state, actions, Modifier.fillMaxWidth(), pinFooter = false)
            }
        }
    }
}

@Composable
internal fun MenuPane(state: CheckoutUiState, actions: CheckoutActions, modifier: Modifier, scrollItems: Boolean) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
        SearchField(
            state.builderQuery,
            actions.onBuilderSearch,
            Modifier.fillMaxWidth(),
            placeholderRes = R.string.checkout_search_menu,
        )
        CategoryChips(state.category, actions.onCategory)
        if (scrollItems) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
                modifier = Modifier.weight(1f),
            ) {
                items(state.visibleMenu, key = { it.id }) { item ->
                    MenuItemCard(item, onClick = { actions.onOpenItem(item) })
                }
            }
        } else {
            MenuRows(state.visibleMenu, actions.onOpenItem)
        }
    }
}

@Composable
internal fun CategoryChips(selected: MenuCategory, onCategory: (MenuCategory) -> Unit) {
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
internal fun MenuRows(items: List<MenuItem>, onOpenItem: (MenuItem) -> Unit) {
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
internal fun MenuItemCard(item: MenuItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
internal fun CartPane(state: CheckoutUiState, actions: CheckoutActions, modifier: Modifier, pinFooter: Boolean) {
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
internal fun CartDetails(state: CheckoutUiState, actions: CheckoutActions) {
    Text(stringResource(R.string.checkout_current_order), style = AppTheme.typography.titleMedium, color = AppTheme.colors.textPrimary)
    ChoiceRow(stringResource(R.string.checkout_channel), OrderChannel.entries, state.channel, actions.onChannel)
    ChoiceRow(stringResource(R.string.checkout_fulfillment), Fulfillment.entries, state.fulfillment, actions.onFulfillment)
    InfoRow(
        text = if (state.hasCustomer) {
            listOf(state.customerName, state.customerPhone).filter { it.isNotBlank() }.joinToString("  ")
        } else {
            stringResource(R.string.checkout_add_customer)
        },
        action = if (state.hasCustomer) stringResource(R.string.checkout_edit) else "",
        onClick = actions.onEditCustomer,
    )
    if (state.fulfillment == Fulfillment.Delivery) {
        val hasAddress = state.street.isNotBlank()
        if (hasAddress) {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)) {
                Text(
                    stringResource(R.string.checkout_delivery_address),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textMuted,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AppTheme.radius.sm))
                        .border(AppTheme.border.thin, AppTheme.colors.textPrimary, RoundedCornerShape(AppTheme.radius.sm))
                        .clickable(onClick = actions.onEditAddress)
                        .padding(AppTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs)) {
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
                    Text(
                        stringResource(R.string.checkout_edit),
                        style = AppTheme.typography.labelMedium,
                        color = AppTheme.colors.textMuted,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
            }
        } else {
            InfoRow(
                text = stringResource(R.string.checkout_add_delivery_address),
                action = "",
                onClick = actions.onEditAddress,
            )
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        state.lines.forEach { line -> CartLineRow(line, actions) }
    }
}

@Composable
internal fun CartFooter(state: CheckoutUiState, actions: CheckoutActions) {
    Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)))
    TotalRow(stringResource(R.string.checkout_subtotal), formatMoney(state.subtotalCents), emphasize = false)
    TotalRow(stringResource(R.string.checkout_tax), formatMoney(state.taxCents), emphasize = false)
    TotalRow(stringResource(R.string.checkout_total), formatMoney(state.totalCents), emphasize = true)
    PillButton(
        stringResource(R.string.checkout_confirm_order),
        actions.onPay,
        Modifier.fillMaxWidth(),
        enabled = state.lines.isNotEmpty(),
    )
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
internal fun <T> ChoiceRow(label: String, options: List<T>, selected: T, onSelect: (T) -> Unit) where T : Enum<T> {
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
internal fun CartLineRow(line: CartLine, actions: CheckoutActions) {
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
internal fun StepperKey(icon: androidx.compose.ui.graphics.vector.ImageVector, description: String, onClick: () -> Unit) {
    Box(
        Modifier.size(AppTheme.sizes.minTouchTarget).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = description, tint = AppTheme.colors.textPrimary)
    }
}

@Composable
internal fun InfoRow(text: String, action: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.sm))
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary, RoundedCornerShape(AppTheme.radius.sm))
            .clickable(onClick = onClick)
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
internal fun TotalRow(label: String, value: String, emphasize: Boolean) {
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

@ScreenPreviews
@Composable
private fun BuilderContentPreview() {
    PreviewSurface {
        BuilderContent(
            CheckoutUiState(step = CheckoutStep.Building, lines = PhoneCart, channel = OrderChannel.Phone),
            CheckoutActions({}, {}, {}, {}, { _, _ -> }, {}, {}, {}, {}),
        )
    }
}
