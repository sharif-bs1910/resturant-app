package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
internal fun DialogLabeledField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fill: Boolean = true,
    onValue: (String) -> Unit,
) {
    Column(
        modifier.then(if (fill) Modifier.fillMaxWidth() else Modifier),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        Text(label, style = AppTheme.typography.labelSmall, color = AppTheme.colors.textMuted, maxLines = 1, softWrap = false)
        DialogField(value, onValue, Modifier.fillMaxWidth(), enabled)
    }
}

@Composable
internal fun DialogField(value: String, onValue: (String) -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    BasicTextField(
        value = value,
        onValueChange = onValue,
        enabled = enabled,
        singleLine = true,
        textStyle = AppTheme.typography.bodyMedium.copy(
            color = if (enabled) AppTheme.colors.textPrimary else AppTheme.colors.textMuted,
        ),
        modifier = modifier
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .clip(shape)
            .background(if (enabled) AppTheme.colors.card else AppTheme.colors.surface)
            .border(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f), shape)
            .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.sm),
    )
}

@Composable
internal fun SettingsDialogCard(wide: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .widthIn(max = if (wide) AppTheme.sizes.dialogMaxWidth else AppTheme.sizes.checkoutDetailWidth)
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppTheme.radius.lg))
            .background(AppTheme.colors.card)
            .padding(AppTheme.spacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        content = content,
    )
}

@Composable
internal fun DialogActions(confirm: String, onCancel: () -> Unit, centered: Boolean = false, fill: Boolean = true, onConfirm: () -> Unit) {
    Row(
        if (fill || centered) Modifier.fillMaxWidth() else Modifier,
        horizontalArrangement = Arrangement.spacedBy(
            AppTheme.spacing.md,
            if (centered) Alignment.CenterHorizontally else Alignment.End,
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PillButton(stringResource(R.string.checkout_dialog_cancel), onCancel, filled = false)
        PillButton(confirm, onConfirm)
    }
}
