package com.noshitechinc.restaurant.feature.selfservice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.feature.checkout.MenuItem
import com.noshitechinc.restaurant.feature.checkout.PillButton
import com.noshitechinc.restaurant.feature.checkout.Stepper
import com.noshitechinc.restaurant.feature.checkout.formatMoney

@Composable
fun SelfServiceMenuContent(
    state: SelfServiceUiState,
    onSearch: (String) -> Unit,
    onMeal: (SelfServiceMeal) -> Unit,
    onCategory: (SelfServiceCategory) -> Unit,
    onOrderType: (SelfServiceOrderType) -> Unit,
    onOpenItem: (MenuItem) -> Unit,
    onLineQuantity: (String, Int) -> Unit,
    onRemoveLine: (String) -> Unit,
    onContinue: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize().padding(AppTheme.spacing.xl)) {
        val sideBySide = maxWidth >= AppTheme.sizes.checkoutCartWidth * 2.4f
        if (sideBySide) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxl)) {
                SelfServiceMenuPane(
                    state = state,
                    onSearch = onSearch,
                    onMeal = onMeal,
                    onCategory = onCategory,
                    onOpenItem = onOpenItem,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                SelfServiceCartPane(
                    state = state,
                    onOrderType = onOrderType,
                    onLineQuantity = onLineQuantity,
                    onRemoveLine = onRemoveLine,
                    onContinue = onContinue,
                    modifier = Modifier.width(AppTheme.sizes.checkoutCartWidth).fillMaxHeight(),
                )
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxl),
            ) {
                SelfServiceMenuPane(
                    state = state,
                    onSearch = onSearch,
                    onMeal = onMeal,
                    onCategory = onCategory,
                    onOpenItem = onOpenItem,
                    modifier = Modifier.fillMaxWidth().heightIn(min = AppTheme.sizes.heroHeight * 2),
                    scrollGrid = false,
                )
                SelfServiceCartPane(
                    state = state,
                    onOrderType = onOrderType,
                    onLineQuantity = onLineQuantity,
                    onRemoveLine = onRemoveLine,
                    onContinue = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    pinFooter = false,
                )
            }
        }
    }
}

@Composable
private fun SelfServiceMenuPane(
    state: SelfServiceUiState,
    onSearch: (String) -> Unit,
    onMeal: (SelfServiceMeal) -> Unit,
    onCategory: (SelfServiceCategory) -> Unit,
    onOpenItem: (MenuItem) -> Unit,
    modifier: Modifier,
    scrollGrid: Boolean = true,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxl)) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl - AppTheme.spacing.xs),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.self_menu_title),
                    style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.textPrimary,
                    maxLines = 1,
                )
                Spacer(Modifier.weight(1f))
                MenuSearchField(
                    value = state.query,
                    onValue = onSearch,
                    modifier = Modifier.width(AppTheme.sizes.checkoutSearchWidth),
                )
            }
            MealChips(selected = state.meal, onMeal = onMeal)
        }
        Row(
            Modifier.weight(1f, fill = scrollGrid).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl - AppTheme.spacing.xs),
            verticalAlignment = Alignment.Top,
        ) {
            val categoryWidth = AppTheme.sizes.passcodeKeyWidth * 2 + AppTheme.spacing.lg
            BoxWithConstraints(
                Modifier
                    .width(categoryWidth)
                    .then(if (scrollGrid) Modifier.fillMaxHeight() else Modifier),
            ) {
                CategoryRail(
                    selected = state.category,
                    onCategory = onCategory,
                    modifier = Modifier
                        .width(categoryWidth)
                        .heightIn(max = maxHeight)
                        .align(Alignment.TopStart),
                )
            }
            if (scrollGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                ) {
                    items(state.visibleMenu, key = { it.id }) { item ->
                        SelfServiceMenuItemCard(item, onClick = { onOpenItem(item) })
                    }
                }
            } else {
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                ) {
                    state.visibleMenu.chunked(2).forEach { row ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                        ) {
                            row.forEach { item ->
                                SelfServiceMenuItemCard(
                                    item,
                                    onClick = { onOpenItem(item) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            repeat(2 - row.size) { Box(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuSearchField(value: String, onValue: (String) -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    Row(
        modifier = modifier
            .height(AppTheme.sizes.buttonSmall)
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(horizontal = AppTheme.spacing.md + AppTheme.spacing.xxs),
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
            singleLine = true,
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        stringResource(R.string.checkout_search_menu),
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                inner()
            },
        )
    }
}

@Composable
private fun MealChips(selected: SelfServiceMeal, onMeal: (SelfServiceMeal) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        SelfServiceMeal.entries.forEach { meal ->
            MealChip(
                label = stringResource(R.string.self_meal_chip, stringResource(meal.labelRes), meal.count),
                selected = meal == selected,
                onClick = { onMeal(meal) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MealChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.pill)
    val colors = AppTheme.colors
    Box(
        modifier = modifier
            .height(AppTheme.sizes.buttonSmall - AppTheme.spacing.xxs)
            .clip(shape)
            .background(if (selected) colors.textPrimary else colors.card)
            .then(
                if (selected) {
                    Modifier
                } else {
                    Modifier.border(AppTheme.border.thin, colors.textPrimary, shape)
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = AppTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = if (selected) colors.background else colors.textPrimary,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Composable
private fun CategoryRail(selected: SelfServiceCategory, onCategory: (SelfServiceCategory) -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Column(
        modifier = modifier
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
    ) {
        Text(
            stringResource(R.string.self_categories),
            style = AppTheme.typography.labelSmall,
            color = AppTheme.colors.textPrimary.copy(alpha = 0.55f),
            modifier = Modifier.fillMaxWidth(),
        )
        SelfServiceCategory.entries.forEach { category ->
            val active = category == selected
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppTheme.sizes.buttonSmall)
                    .clip(RoundedCornerShape(AppTheme.radius.sm))
                    .background(if (active) AppTheme.colors.background else AppTheme.colors.card)
                    .clickable { onCategory(category) }
                    .padding(horizontal = AppTheme.spacing.sm + AppTheme.spacing.xxs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(category.labelRes),
                    style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (active) {
                        AppTheme.colors.textPrimary
                    } else {
                        AppTheme.colors.textPrimary.copy(alpha = 0.8f)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    category.count.toString(),
                    style = AppTheme.typography.labelSmall,
                    color = AppTheme.colors.textPrimary.copy(alpha = 0.55f),
                )
            }
        }
    }
}

@Composable
private fun SelfServiceMenuItemCard(item: MenuItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val imageSize = AppTheme.sizes.cardImage - AppTheme.spacing.xs
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .clickable(onClick = onClick)
            .padding(AppTheme.spacing.md + AppTheme.spacing.xxs),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Image(
            painter = painterResource(R.drawable.checkout_menu_photo),
            contentDescription = null,
            modifier = Modifier
                .size(imageSize)
                .clip(RoundedCornerShape(AppTheme.radius.sm + AppTheme.spacing.xxs))
                .background(AppTheme.colors.background),
            contentScale = ContentScale.Crop,
        )
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                Text(
                    item.name,
                    style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
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
                    modifier = Modifier.heightIn(min = AppTheme.spacing.xxl),
                )
            }
            Text(
                formatMoney(item.priceCents),
                style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = AppTheme.colors.primary,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

@Composable
private fun SelfServiceCartPane(
    state: SelfServiceUiState,
    onOrderType: (SelfServiceOrderType) -> Unit,
    onLineQuantity: (String, Int) -> Unit,
    onRemoveLine: (String) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier,
    pinFooter: Boolean = true,
) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    Column(
        modifier = modifier
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(horizontal = AppTheme.spacing.xl - AppTheme.spacing.xs, vertical = AppTheme.spacing.xl - AppTheme.spacing.xs)
            .then(if (pinFooter) Modifier.fillMaxHeight() else Modifier),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl - AppTheme.spacing.xs),
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            Text(
                stringResource(R.string.checkout_current_order),
                style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = AppTheme.colors.textPrimary,
            )
            Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)))
        }
        OrderTypeRow(selected = state.orderType, onOrderType = onOrderType)
        if (state.lines.isEmpty()) {
            Box(
                Modifier
                    .weight(1f, fill = pinFooter)
                    .fillMaxWidth()
                    .then(if (!pinFooter) Modifier.heightIn(min = AppTheme.sizes.heroHeight) else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    Modifier.fillMaxWidth(0.75f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                ) {
                    Text(
                        stringResource(R.string.self_cart_empty_title),
                        style = AppTheme.typography.labelSmall.copy(
                            fontSize = AppTheme.typography.bodyMedium.fontSize,
                            lineHeight = AppTheme.typography.bodyMedium.lineHeight,
                            letterSpacing = 0.08.em,
                        ),
                        color = AppTheme.colors.textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        stringResource(R.string.self_cart_empty_body),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        } else {
            Column(
                Modifier
                    .weight(1f, fill = pinFooter)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            ) {
                state.lines.forEach { line ->
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm + AppTheme.spacing.xxs),
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                        ) {
                            Text(
                                line.name,
                                style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = AppTheme.colors.textPrimary,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                formatMoney(line.unitCents * line.quantity),
                                style = AppTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = AppTheme.colors.textPrimary,
                                maxLines = 1,
                                softWrap = false,
                            )
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Stepper(line.quantity, onChange = { onLineQuantity(line.id, it) })
                            Text(
                                text = stringResource(R.string.checkout_remove),
                                style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = AppTheme.colors.textMuted,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier
                                    .widthIn(min = AppTheme.sizes.passcodeKeyWidth - AppTheme.spacing.sm)
                                    .clickable { onRemoveLine(line.id) },
                            )
                        }
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(AppTheme.border.thin).background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)))
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            CartMoneyRow(
                stringResource(R.string.checkout_subtotal),
                formatMoney(state.subtotalCents),
                emphasize = false,
            )
            CartMoneyRow(
                stringResource(R.string.checkout_tax),
                formatMoney(state.taxCents),
                emphasize = false,
            )
            CartMoneyRow(
                stringResource(R.string.checkout_total),
                formatMoney(state.totalCents),
                emphasize = true,
            )
        }
        PillButton(
            stringResource(R.string.checkout_customer_continue),
            onContinue,
            Modifier
                .fillMaxWidth()
                .height(AppTheme.sizes.buttonMedium),
            enabled = state.lines.isNotEmpty(),
        )
    }
}

@Composable
private fun CartMoneyRow(label: String, value: String, emphasize: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (emphasize) {
                AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            } else {
                AppTheme.typography.bodyMedium
            },
            color = if (emphasize) AppTheme.colors.textPrimary else AppTheme.colors.textMuted,
        )
        Text(
            value,
            style = if (emphasize) {
                AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            } else {
                AppTheme.typography.bodyMedium
            },
            color = if (emphasize) AppTheme.colors.primary else AppTheme.colors.textPrimary,
        )
    }
}

@Composable
private fun OrderTypeRow(selected: SelfServiceOrderType, onOrderType: (SelfServiceOrderType) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            stringResource(R.string.self_order_type),
            style = AppTheme.typography.labelSmall.copy(
                fontSize = AppTheme.typography.bodyMedium.fontSize,
                lineHeight = AppTheme.typography.bodyMedium.lineHeight,
                letterSpacing = 0.08.em,
            ),
            color = AppTheme.colors.textMuted,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            SelfServiceOrderType.entries.forEach { type ->
                OrderTypeChip(
                    label = stringResource(type.labelRes),
                    selected = type == selected,
                    onClick = { onOrderType(type) },
                )
            }
        }
    }
}

@Composable
private fun OrderTypeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.pill)
    val colors = AppTheme.colors
    Box(
        modifier = Modifier
            .height(AppTheme.sizes.buttonSmall - AppTheme.spacing.xxs)
            .clip(shape)
            .background(if (selected) colors.textPrimary else colors.card)
            .then(
                if (selected) {
                    Modifier
                } else {
                    Modifier.border(AppTheme.border.thin, colors.textPrimary, shape)
                },
            )
            .clickable(onClick = onClick)
            .padding(horizontal = AppTheme.spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = AppTheme.typography.labelMedium,
            color = if (selected) colors.background else colors.textPrimary,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@ScreenPreviews
@Composable
private fun SelfServiceMenuEmptyPreview() {
    PreviewSurface {
        SelfServiceMenuContent(
            state = SelfServiceUiState(),
            onSearch = {},
            onMeal = {},
            onCategory = {},
            onOrderType = {},
            onOpenItem = {},
            onLineQuantity = { _, _ -> },
            onRemoveLine = {},
            onContinue = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun SelfServiceMenuFilledPreview() {
    PreviewSurface {
        SelfServiceMenuContent(
            state = SelfServiceUiState(lines = SelfServiceDemoCart),
            onSearch = {},
            onMeal = {},
            onCategory = {},
            onOrderType = {},
            onOpenItem = {},
            onLineQuantity = { _, _ -> },
            onRemoveLine = {},
            onContinue = {},
        )
    }
}
