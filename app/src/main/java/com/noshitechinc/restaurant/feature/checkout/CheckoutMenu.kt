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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.selection.AppSwitch
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

data class MenuActions(
    val onFilter: (MenuListFilter) -> Unit,
    val onSearch: (String) -> Unit,
    val onSelect: (String) -> Unit,
    val onName: (String) -> Unit,
    val onDescription: (String) -> Unit,
    val onPrice: (String) -> Unit,
    val onStock: (String) -> Unit,
    val onStopTracking: () -> Unit,
    val onTrackInventory: () -> Unit,
    val onAvailability: (String, Boolean) -> Unit,
    val onUntil: (UnavailableUntil) -> Unit,
    val onConfirmUnavailable: () -> Unit,
    val onDismissUnavailable: () -> Unit,
    val onSave: () -> Unit,
    val onCancel: () -> Unit,
)

@Composable
fun MenuContent(state: CheckoutUiState, actions: MenuActions) {
    val item = state.menuItems.firstOrNull { it.id == state.menuDraft.id } ?: return
    BoxWithConstraints(Modifier.fillMaxSize().padding(AppTheme.spacing.xl)) {
        val sideBySide = maxWidth >= AppTheme.sizes.checkoutDetailWidth + AppTheme.sizes.checkoutCartWidth
        if (sideBySide) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                MenuList(state, actions, Modifier.weight(1f).fillMaxHeight(), scroll = true)
                MenuEditor(
                    item,
                    state.menuDraft,
                    actions,
                    Modifier.width(AppTheme.sizes.checkoutDetailWidth).fillMaxHeight(),
                    scroll = true,
                )
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                MenuList(state, actions, Modifier.fillMaxWidth(), scroll = false)
                MenuEditor(item, state.menuDraft, actions, Modifier.fillMaxWidth(), scroll = false)
            }
        }
    }
}

@Composable
private fun MenuList(state: CheckoutUiState, actions: MenuActions, modifier: Modifier, scroll: Boolean) {
    Column(
        modifier.then(if (scroll) Modifier.verticalScroll(rememberScrollState()) else Modifier),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            MenuListFilter.entries.forEach { filter ->
                CheckoutChip(stringResource(filter.labelRes), filter == state.menuFilter) { actions.onFilter(filter) }
            }
        }
        MenuSearch(state.menuQuery, actions.onSearch)
        val query = state.menuQuery.trim()
        if (query.isNotEmpty()) {
            Text(
                stringResource(R.string.menu_results, state.visibleKitchenMenu.size, query),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
            )
        }
        groupedMenu(state.visibleKitchenMenu).forEach { (category, items) ->
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                Text(
                    stringResource(category.labelRes).uppercase(),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.padding(horizontal = AppTheme.spacing.md),
                )
                MenuGroupCard(items, state.menuDraft.id, actions)
            }
        }
    }
}

@Composable
private fun MenuGroupCard(items: List<KitchenMenuItem>, selectedId: String, actions: MenuActions) {
    val shape = RoundedCornerShape(AppTheme.radius.lg)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape),
    ) {
        items.forEachIndexed { index, item ->
            MenuItemRow(item, item.id == selectedId, actions)
            if (index != items.lastIndex) {
                Box(
                    Modifier.fillMaxWidth().heightIn(
                        min = AppTheme.border.thin,
                    ).background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
                )
            }
        }
    }
}

@Composable
private fun MenuItemRow(item: KitchenMenuItem, selected: Boolean, actions: MenuActions) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) AppTheme.colors.surface else AppTheme.colors.card)
            .clickable { actions.onSelect(item.id) }
            .padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Image(
            painter = painterResource(item.photoRes),
            contentDescription = null,
            modifier = Modifier
                .size(AppTheme.sizes.minTouchTarget)
                .clip(RoundedCornerShape(AppTheme.radius.sm)),
            contentScale = ContentScale.Crop,
        )
        Column(Modifier.weight(1f)) {
            Text(
                item.name,
                style = AppTheme.typography.titleSmall,
                color = AppTheme.colors.textPrimary.copy(alpha = if (item.available) 1f else 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                if (item.available) {
                    formatMoney(
                        item.priceCents,
                    )
                } else {
                    stringResource(R.string.menu_price_eightysixed, formatMoney(item.priceCents))
                },
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
                maxLines = 1,
                softWrap = false,
            )
        }
        if (item.stock != null && item.available) {
            Text(
                stringResource(R.string.menu_stock_left, item.stock),
                style = AppTheme.typography.bodySmall,
                color = AppTheme.colors.textMuted,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .clip(RoundedCornerShape(AppTheme.radius.sm))
                    .background(AppTheme.colors.card)
                    .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), RoundedCornerShape(AppTheme.radius.sm))
                    .padding(horizontal = AppTheme.spacing.sm, vertical = AppTheme.spacing.xs),
            )
        }
        AppSwitch(checked = item.available, onCheckedChange = { actions.onAvailability(item.id, it) })
    }
}

@Composable
private fun MenuSearch(value: String, onValue: (String) -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Row(
        Modifier
            .fillMaxWidth()
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
            textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.textPrimary),
            singleLine = true,
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(stringResource(R.string.menu_search), style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
                }
                inner()
            },
        )
        if (value.isNotEmpty()) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(R.string.menu_clear_search),
                tint = AppTheme.colors.textMuted,
                modifier = Modifier.size(AppTheme.sizes.iconSm).clickable { onValue("") },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MenuEditor(item: KitchenMenuItem, draft: MenuDraft, actions: MenuActions, modifier: Modifier, scroll: Boolean) {
    val shape = RoundedCornerShape(AppTheme.radius.lg)
    Column(
        modifier
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .then(if (scroll) Modifier.verticalScroll(rememberScrollState()) else Modifier)
            .padding(AppTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Text(stringResource(R.string.menu_edit_item), style = AppTheme.typography.titleMedium, color = AppTheme.colors.textPrimary)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            Image(
                painter = painterResource(item.photoRes),
                contentDescription = null,
                modifier = Modifier.size(AppTheme.sizes.logoMark).clip(RoundedCornerShape(AppTheme.radius.sm)),
                contentScale = ContentScale.Crop,
            )
            EditorField(stringResource(R.string.menu_item_name), draft.name, actions.onName, Modifier.weight(1f))
        }
        EditorField(stringResource(R.string.menu_description), draft.description, actions.onDescription)
        EditorField(stringResource(R.string.menu_price), draft.priceText, actions.onPrice)
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Text(
                stringResource(R.string.menu_category),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
                maxLines = 1,
                softWrap = false,
            )
            ReadOnlyValue(stringResource(item.category.labelRes))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
            if (draft.trackStock) {
                Column(
                    Modifier.width(AppTheme.sizes.checkoutSearchWidth / 2),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            stringResource(R.string.menu_in_stock),
                            style = AppTheme.typography.labelSmall,
                            color = AppTheme.colors.textMuted,
                            maxLines = 1,
                            softWrap = false,
                        )
                        Text(
                            stringResource(R.string.menu_stop_tracking),
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.primary,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.clickable(onClick = actions.onStopTracking),
                        )
                    }
                    EditorValue(draft.stockText, actions.onStock)
                }
            } else {
                Text(
                    stringResource(R.string.menu_track_inventory),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.primary,
                    modifier = Modifier.clickable(onClick = actions.onTrackInventory),
                )
            }
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.menu_available),
                        style = AppTheme.typography.titleSmall,
                        color = AppTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        stringResource(R.string.menu_show_on_menu),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted,
                        maxLines = 1,
                        softWrap = false,
                    )
                }
                AppSwitch(checked = draft.available, onCheckedChange = { actions.onAvailability(draft.id, it) })
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            Text(
                stringResource(R.string.menu_modifier_groups),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
                maxLines = 1,
                softWrap = false,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                item.modifierGroups.forEach { group ->
                    Text(
                        stringResource(group),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textPrimary,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier
                            .clip(RoundedCornerShape(AppTheme.radius.sm))
                            .background(AppTheme.colors.surface)
                            .padding(horizontal = AppTheme.spacing.sm, vertical = AppTheme.spacing.xs),
                    )
                }
            }
            Text(stringResource(R.string.menu_modifiers_note), style = AppTheme.typography.bodySmall, color = AppTheme.colors.textMuted)
        }
        PillButton(stringResource(R.string.menu_save), actions.onSave, Modifier.fillMaxWidth())
        PillButton(stringResource(R.string.menu_cancel), actions.onCancel, Modifier.fillMaxWidth(), filled = false)
    }
}

@Composable
private fun EditorField(label: String, value: String, onValue: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        Text(label, style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted, maxLines = 1, softWrap = false)
        EditorValue(value, onValue)
    }
}

@Composable
private fun EditorValue(value: String, onValue: (String) -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    BasicTextField(
        value = value,
        onValueChange = onValue,
        textStyle = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.textPrimary),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clip(shape)
            .background(AppTheme.colors.textPrimary.copy(alpha = 0.03f))
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(AppTheme.spacing.md),
    )
}

@Composable
private fun ReadOnlyValue(value: String) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clip(shape)
            .background(AppTheme.colors.textPrimary.copy(alpha = 0.03f))
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(AppTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            value,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(stringResource(R.string.menu_chevron), style = AppTheme.typography.titleSmall, color = AppTheme.colors.textMuted)
    }
}

@Composable
internal fun UnavailableDialog(name: String, until: UnavailableUntil, actions: MenuActions) {
    Box(
        Modifier
            .fillMaxSize()
            .background(AppTheme.colors.textPrimary.copy(alpha = 0.45f))
            .clickable(onClick = actions.onDismissUnavailable)
            .padding(AppTheme.spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        val shape = RoundedCornerShape(AppTheme.radius.lg)
        Column(
            Modifier
                .widthIn(max = AppTheme.sizes.checkoutDetailWidth + AppTheme.sizes.minTouchTarget)
                .fillMaxWidth()
                .clip(shape)
                .background(AppTheme.colors.card)
                .clickable(onClick = {})
                .padding(AppTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        ) {
            Text(
                stringResource(R.string.menu_mark_unavailable, name),
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                stringResource(R.string.menu_mark_unavailable_body),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textMuted,
            )
            Text(
                stringResource(R.string.menu_unavailable_until),
                style = AppTheme.typography.labelSmall,
                color = AppTheme.colors.textMuted,
                maxLines = 1,
                softWrap = false,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                UnavailableUntil.entries.forEach { option ->
                    CheckoutChip(stringResource(option.labelRes), option == until) { actions.onUntil(option) }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                PillButton(stringResource(R.string.menu_cancel), actions.onDismissUnavailable, Modifier.weight(1f), filled = false)
                PillButton(stringResource(R.string.menu_confirm), actions.onConfirmUnavailable, Modifier.weight(1f))
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun MenuPreview() {
    PreviewSurface {
        MenuContent(CheckoutUiState(section = KitchenSection.Menu), emptyMenuActions())
    }
}

internal fun emptyMenuActions() = MenuActions(
    {},
    {},
    {},
    {},
    {},
    {},
    {},
    {},
    {},
    { _, _ -> },
    {},
    {},
    {},
    {},
    {},
)
