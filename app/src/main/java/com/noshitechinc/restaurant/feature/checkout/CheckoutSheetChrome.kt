package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.outlined.Backspace
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
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun SheetCard(modifier: Modifier = Modifier, scroll: Boolean = true, content: @Composable ColumnScope.() -> Unit) {
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
fun SheetHeader(title: String, subtitle: String, onClose: () -> Unit) {
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
fun NoteField(value: String, onValue: (String) -> Unit) {
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
fun GiftField(label: String, value: String, selected: Boolean, onClick: () -> Unit) {
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
fun GiftKeypad(onDigit: (String) -> Unit, onDelete: () -> Unit) {
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
                                    Modifier.clip(shape)
                                        .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
                                        .clickable {
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

@ComponentPreviews
@Composable
private fun SheetChromePreview() {
    PreviewSurface {
        SheetCard(Modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth)) {
            SheetHeader("Draft saved", "Order #1043", onClose = {})
            MoneyRow("Amount due", "$35.89", accentValue = true)
        }
    }
}
