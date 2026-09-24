package com.noshitechinc.restaurant.core.designsystem.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppModalLayout(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(AppTheme.radius.xl),
        color = AppTheme.colors.surface,
        modifier = modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth),
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        ) {
            Text(
                text = title,
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.textPrimary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = AppTheme.sizes.dialogMaxWidth)
                    .verticalScroll(rememberScrollState()),
                content = content,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm, Alignment.End),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                content = { actions() },
            )
        }
    }
}

@Composable
fun AppModal(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissEnabled: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = { if (dismissEnabled) onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = dismissEnabled,
            dismissOnClickOutside = dismissEnabled,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.spacing.xl),
            contentAlignment = Alignment.Center,
        ) {
            AppModalLayout(
                title = title,
                modifier = modifier,
                actions = actions,
                content = content,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun AppModalLayoutShortPreview() {
    PreviewSurface {
        AppModalLayout(
            title = "Edit guest count",
            actions = {
                AppButton(text = "Cancel", onClick = {})
                AppButton(text = "Save", onClick = {})
            },
        ) {
            Text(
                text = PreviewData.DESCRIPTION,
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun AppModalLayoutLongScrollPreview() {
    PreviewSurface {
        AppModalLayout(
            title = PreviewData.LONG_NAME,
            actions = {
                AppButton(text = "Cancel", onClick = {})
                AppButton(text = "Confirm", onClick = {})
            },
        ) {
            Text(
                text = List(6) { PreviewData.LONG_DESCRIPTION }.joinToString("\n\n"),
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun AppModalLayoutWrappingActionsPreview() {
    PreviewSurface {
        AppModalLayout(
            title = "Choose an action",
            actions = {
                AppButton(text = PreviewData.TRANSLATED_LABEL, onClick = {})
                AppButton(text = "Secondary action", onClick = {})
                AppButton(text = "Primary action", onClick = {})
            },
        ) {
            Text(
                text = PreviewData.DESCRIPTION,
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}
