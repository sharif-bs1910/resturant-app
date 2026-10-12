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
internal fun OpenOrdersHeader(count: Int, search: String, onSearch: (String) -> Unit) {
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
                    SearchField(
                        search,
                        onSearch,
                        Modifier.width(AppTheme.sizes.checkoutSearchWidth),
                        placeholderRes = R.string.checkout_search,
                    )
                }
            }
            if (!searchBeside) {
                SearchField(search, onSearch, Modifier.fillMaxWidth(), placeholderRes = R.string.checkout_search)
            }
        }
    }
}

@Composable
internal fun OpenOrderCards(orders: List<OpenOrder>, onOpenOrder: (OpenOrder) -> Unit) {
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
internal fun OpenOrderCard(order: OpenOrder, onClick: () -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val badge = when (order.status) {
        OpenOrderStatus.New -> AppTheme.colors.primary
        OpenOrderStatus.Cooking -> AppTheme.colors.warning
        OpenOrderStatus.Ready -> AppTheme.colors.success
        OpenOrderStatus.Completed -> AppTheme.colors.textMuted
    }
    val badgeText = when (order.status) {
        OpenOrderStatus.Ready -> AppTheme.colors.background
        else -> AppTheme.colors.textPrimary
    }
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

@Composable
internal fun SearchField(
    value: String,
    onValue: (String) -> Unit,
    modifier: Modifier,
    @androidx.annotation.StringRes placeholderRes: Int = R.string.checkout_search,
) {
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
                    Text(stringResource(placeholderRes), style = AppTheme.typography.bodySmall, color = AppTheme.colors.textMuted)
                }
                inner()
            },
        )
    }
}

@ScreenPreviews
@Composable
private fun IdleContentPreview() {
    PreviewSurface {
        IdleContent(CheckoutUiState(), {}, {}, {})
    }
}
