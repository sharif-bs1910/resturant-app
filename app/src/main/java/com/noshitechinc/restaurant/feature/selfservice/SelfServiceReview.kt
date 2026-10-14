package com.noshitechinc.restaurant.feature.selfservice

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.format.UsPhoneFormatter
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.feature.checkout.CartLine
import com.noshitechinc.restaurant.feature.checkout.PillButton
import com.noshitechinc.restaurant.feature.checkout.formatMoney
import com.noshitechinc.restaurant.feature.checkout.modifierGroups

@Composable
fun SelfServiceReviewContent(
    state: SelfServiceUiState,
    onToggleLine: (String) -> Unit,
    onMakePayment: () -> Unit,
    onRedeemGift: () -> Unit,
    onBack: () -> Unit,
) {
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .padding(horizontal = AppTheme.spacing.xxxl + AppTheme.spacing.xxl, vertical = AppTheme.spacing.xxxl),
    ) {
        val sideBySide = maxWidth >= AppTheme.sizes.checkoutCartWidth * 2
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl + AppTheme.spacing.sm),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                Text(
                    stringResource(R.string.self_review_title),
                    style = AppTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = AppTheme.colors.textPrimary,
                )
                Text(
                    stringResource(R.string.self_guest_body),
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.textMuted,
                )
            }
            if (sideBySide) {
                Row(
                    Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl - AppTheme.spacing.xs),
                    verticalAlignment = Alignment.Top,
                ) {
                    ReviewLeftColumn(
                        state = state,
                        onToggleLine = onToggleLine,
                        onEditDetails = onBack,
                        onEditItems = onBack,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                    )
                    ReviewSummaryCard(
                        state = state,
                        onMakePayment = onMakePayment,
                        onRedeemGift = onRedeemGift,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                Column(
                    Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl - AppTheme.spacing.xs),
                ) {
                    ReviewLeftColumn(state, onToggleLine, onBack, onBack, Modifier.fillMaxWidth())
                    ReviewSummaryCard(state, onMakePayment, onRedeemGift, Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun ReviewLeftColumn(
    state: SelfServiceUiState,
    onToggleLine: (String) -> Unit,
    onEditDetails: () -> Unit,
    onEditItems: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl - AppTheme.spacing.xs),
    ) {
        ReviewPanel {
            SectionHeader(stringResource(R.string.self_your_details), onEdit = onEditDetails)
            RecapBox {
                DetailRow(stringResource(R.string.checkout_customer_name), state.guestName)
                DetailRow(
                    stringResource(R.string.checkout_customer_phone),
                    UsPhoneFormatter.format(state.guestPhoneDigits),
                )
                DetailRow(
                    stringResource(R.string.checkout_table_number),
                    stringResource(R.string.self_table_value, state.tableNumber),
                )
            }
        }
        ReviewPanel {
            SectionHeader(
                stringResource(R.string.self_items_count, state.lines.sumOf { it.quantity }),
                onEdit = onEditItems,
            )
            RecapBox {
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                    state.lines.forEach { line ->
                        ReviewLine(
                            line = line,
                            expanded = line.id in state.expandedLineIds,
                            onToggle = { onToggleLine(line.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewSummaryCard(state: SelfServiceUiState, onMakePayment: () -> Unit, onRedeemGift: () -> Unit, modifier: Modifier) {
    ReviewPanel(modifier = modifier, padded = false) {
        Text(
            stringResource(R.string.self_order_summary),
            style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AppTheme.colors.textPrimary,
        )
        RecapBox {
            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                MoneyRow(stringResource(R.string.checkout_subtotal), formatMoney(state.subtotalCents), bold = false)
                MoneyRow(stringResource(R.string.checkout_tax), formatMoney(state.taxCents), bold = false)
                if (state.giftAppliedCents > 0) {
                    MoneyRow(
                        stringResource(R.string.checkout_gift_card),
                        formatMoney(-state.giftAppliedCents),
                        bold = false,
                    )
                }
                MoneyRow(stringResource(R.string.checkout_total), formatMoney(state.dueCents), bold = true)
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
            PillButton(
                stringResource(R.string.self_make_payment),
                onMakePayment,
                Modifier.fillMaxWidth().height(AppTheme.sizes.buttonMedium + AppTheme.spacing.xxs),
            )
            PillButton(
                stringResource(R.string.checkout_redeem),
                onRedeemGift,
                Modifier.fillMaxWidth().height(AppTheme.sizes.buttonMedium + AppTheme.spacing.xxs),
                filled = false,
            )
        }
    }
}

@Composable
private fun ReviewPanel(modifier: Modifier = Modifier, padded: Boolean = true, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val pad = if (padded) AppTheme.spacing.xl else AppTheme.spacing.xxl
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppTheme.colors.card)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(pad),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        content()
    }
}

@Composable
private fun SectionHeader(title: String, onEdit: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            title,
            style = AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
            modifier = Modifier.clickable(onClick = onEdit),
        ) {
            Text(
                stringResource(R.string.self_edit),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.textMuted,
            )
            Icon(
                Icons.Filled.Edit,
                contentDescription = null,
                tint = AppTheme.colors.textMuted,
                modifier = Modifier.size(AppTheme.sizes.iconSm),
            )
        }
    }
}

@Composable
private fun RecapBox(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(AppTheme.radius.sm + AppTheme.spacing.xxs)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(AppTheme.spacing.lg + AppTheme.spacing.xxs),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm + AppTheme.spacing.xxs),
    ) {
        content()
    }
}

@Composable
private fun ReviewLine(line: CartLine, expanded: Boolean, onToggle: () -> Unit) {
    val details = lineDetailLabels(line)
    Column(
        Modifier.fillMaxWidth().clickable(onClick = onToggle),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            Text(
                stringResource(R.string.self_line_qty_name, line.quantity, line.name),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!expanded && details.isNotEmpty()) {
                Text(
                    stringResource(R.string.self_modifier_count, details.size),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.textMuted,
                    maxLines = 1,
                    softWrap = false,
                )
            }
            Icon(
                if (expanded) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = AppTheme.colors.textMuted,
                modifier = Modifier.size(AppTheme.sizes.iconSm),
            )
            Spacer(Modifier.weight(1f))
            Text(
                formatMoney(line.unitCents * line.quantity),
                style = AppTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                softWrap = false,
            )
        }
        if (expanded && details.isNotEmpty()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            ) {
                Box(
                    Modifier
                        .width(AppTheme.border.thick)
                        .height(AppTheme.spacing.xxxl + AppTheme.spacing.lg)
                        .background(AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
                )
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs + AppTheme.spacing.xxs)) {
                    details.forEachIndexed { index, label ->
                        val isNote = index == details.lastIndex && line.note.isNotBlank()
                        Text(
                            label,
                            style = AppTheme.typography.bodySmall,
                            color = if (isNote) AppTheme.colors.textMuted else AppTheme.colors.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun lineDetailLabels(line: CartLine): List<String> {
    val labels = buildList {
        modifierGroups().forEach { group ->
            group.options.filter { it.id in line.selectedModifierIds }.forEach { option ->
                add(
                    stringResource(
                        R.string.self_modifier_pair,
                        stringResource(group.titleRes),
                        stringResource(option.labelRes),
                    ),
                )
            }
        }
        if (line.note.isNotBlank()) {
            add(stringResource(R.string.self_line_note, line.note))
        }
    }
    return labels
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textMuted)
        Text(
            value,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MoneyRow(label: String, value: String, bold: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (bold) {
                AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            } else {
                AppTheme.typography.bodyMedium
            },
            color = if (bold) AppTheme.colors.textPrimary else AppTheme.colors.textMuted,
        )
        Text(
            value,
            style = if (bold) {
                AppTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            } else {
                AppTheme.typography.bodyMedium
            },
            color = AppTheme.colors.textPrimary,
        )
    }
}

@ScreenPreviews
@Composable
private fun SelfServiceReviewPreview() {
    PreviewSurface {
        SelfServiceReviewContent(
            state = SelfServiceUiState(
                step = SelfServiceStep.Review,
                lines = listOf(
                    CartLine(
                        id = "1",
                        name = "Fire-Roasted Pepperoni",
                        unitCents = 1_550,
                        quantity = 3,
                        selectedModifierIds = setOf("size-medium", "crust-thin"),
                        note = "No green onions",
                    ),
                    CartLine("2", "Truffle Mushroom", 1_625, 1, selectedModifierIds = setOf("size-small")),
                    CartLine("3", "Spicy BBQ Chicken", 1_600, 1, selectedModifierIds = setOf("crust-thin")),
                ),
                expandedLineIds = setOf("1"),
                guestName = "Jhon smith",
                guestPhoneDigits = "1231255487",
                tableNumber = "12",
            ),
            onToggleLine = {},
            onMakePayment = {},
            onRedeemGift = {},
            onBack = {},
        )
    }
}
