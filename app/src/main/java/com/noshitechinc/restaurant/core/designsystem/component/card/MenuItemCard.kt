package com.noshitechinc.restaurant.core.designsystem.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.button.AppIconButton
import com.noshitechinc.restaurant.core.designsystem.component.button.ButtonVariant
import com.noshitechinc.restaurant.core.designsystem.component.selection.StatusBadge
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.designsystem.theme.StatusTone

@Composable
fun MenuItemCard(
    name: String,
    price: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    imageUrl: String? = null,
    isAvailable: Boolean = true,
    onClick: (() -> Unit)? = null,
    onAdd: (() -> Unit)? = null,
) {
    val textColor = if (isAvailable) AppTheme.colors.textPrimary else AppTheme.colors.textDisabled
    val secondaryColor = if (isAvailable) AppTheme.colors.textSecondary else AppTheme.colors.textDisabled
    AppCard(
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            verticalAlignment = Alignment.Top,
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(AppTheme.sizes.cardImage)
                        .clip(RoundedCornerShape(AppTheme.radius.md))
                        .background(AppTheme.colors.surfaceVariant),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
            ) {
                Text(
                    text = name,
                    style = AppTheme.typography.titleMedium,
                    color = textColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = AppTheme.typography.bodyMedium,
                        color = secondaryColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (!isAvailable) {
                    StatusBadge(
                        text = stringResource(R.string.menu_item_unavailable),
                        tone = StatusTone.Neutral,
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            ) {
                Text(
                    text = price,
                    style = AppTheme.typography.priceMedium,
                    color = textColor,
                    softWrap = false,
                    maxLines = 1,
                )
                if (onAdd != null) {
                    AppIconButton(
                        icon = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.action_add),
                        onClick = onAdd,
                        variant = ButtonVariant.Primary,
                        enabled = isAvailable,
                    )
                }
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun MenuItemCardShortPreview() {
    PreviewSurface {
        MenuItemCard(
            name = PreviewData.SHORT_NAME,
            price = PreviewData.PRICE,
            description = PreviewData.DESCRIPTION,
            imageUrl = "https://example.com/latte.jpg",
            onClick = {},
            onAdd = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun MenuItemCardLongContentPreview() {
    PreviewSurface {
        MenuItemCard(
            name = PreviewData.LONG_NAME,
            price = PreviewData.LONG_PRICE,
            description = PreviewData.LONG_DESCRIPTION,
            imageUrl = "https://example.com/dish.jpg",
            onClick = {},
            onAdd = {},
            modifier = Modifier.width(360.dp),
        )
    }
}

@ComponentPreviews
@Composable
private fun MenuItemCardNoImagePreview() {
    PreviewSurface {
        MenuItemCard(
            name = PreviewData.SHORT_NAME,
            price = PreviewData.PRICE,
            description = PreviewData.DESCRIPTION,
            onClick = {},
            onAdd = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun MenuItemCardUnavailablePreview() {
    PreviewSurface {
        MenuItemCard(
            name = PreviewData.SHORT_NAME,
            price = PreviewData.PRICE,
            description = PreviewData.DESCRIPTION,
            imageUrl = "https://example.com/latte.jpg",
            isAvailable = false,
            onClick = {},
            onAdd = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun MenuItemCardPressedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Pressed) {
        MenuItemCard(
            name = PreviewData.SHORT_NAME,
            price = PreviewData.PRICE,
            onClick = {},
            onAdd = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun MenuItemCardFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        MenuItemCard(
            name = PreviewData.SHORT_NAME,
            price = PreviewData.PRICE,
            onClick = {},
            onAdd = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun MenuItemCardTabletGridPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                MenuItemCard(
                    name = PreviewData.SHORT_NAME,
                    price = PreviewData.PRICE,
                    description = PreviewData.DESCRIPTION,
                    onClick = {},
                    onAdd = {},
                    modifier = Modifier.weight(1f),
                )
                MenuItemCard(
                    name = "Cappuccino",
                    price = "$4.75",
                    description = PreviewData.DESCRIPTION,
                    onClick = {},
                    onAdd = {},
                    modifier = Modifier.weight(1f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                MenuItemCard(
                    name = "Espresso",
                    price = "$3.00",
                    onClick = {},
                    onAdd = {},
                    modifier = Modifier.weight(1f),
                )
                MenuItemCard(
                    name = "Mocha",
                    price = "$5.25",
                    isAvailable = false,
                    onClick = {},
                    onAdd = {},
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
