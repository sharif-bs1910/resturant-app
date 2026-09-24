package com.noshitechinc.restaurant.core.designsystem.component.dialog

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.component.button.ButtonVariant
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun ConfirmationDialogLayout(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String = stringResource(R.string.action_cancel),
    destructive: Boolean = false,
    confirmLoading: Boolean = false,
) {
    AppModalLayout(
        title = title,
        modifier = modifier,
        actions = {
            AppButton(
                text = dismissLabel,
                onClick = onDismiss,
                variant = ButtonVariant.Outline,
                enabled = !confirmLoading,
            )
            AppButton(
                text = confirmLabel,
                onClick = onConfirm,
                variant = if (destructive) ButtonVariant.Destructive else ButtonVariant.Primary,
                loading = confirmLoading,
            )
        },
    ) {
        Text(
            text = message,
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.textSecondary,
            overflow = TextOverflow.Clip,
        )
    }
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String = stringResource(R.string.action_cancel),
    destructive: Boolean = false,
    confirmLoading: Boolean = false,
) {
    AppModal(
        title = title,
        onDismissRequest = onDismiss,
        modifier = modifier,
        dismissEnabled = !confirmLoading,
        actions = {
            AppButton(
                text = dismissLabel,
                onClick = onDismiss,
                variant = ButtonVariant.Outline,
                enabled = !confirmLoading,
            )
            AppButton(
                text = confirmLabel,
                onClick = onConfirm,
                variant = if (destructive) ButtonVariant.Destructive else ButtonVariant.Primary,
                loading = confirmLoading,
            )
        },
    ) {
        Text(
            text = message,
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.textSecondary,
            overflow = TextOverflow.Clip,
        )
    }
}

@ComponentPreviews
@Composable
private fun ConfirmationDialogNormalPreview() {
    PreviewSurface {
        ConfirmationDialogLayout(
            title = "Send order?",
            message = "This will send the ticket to the kitchen immediately.",
            confirmLabel = stringResource(R.string.action_confirm),
            onConfirm = {},
            onDismiss = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun ConfirmationDialogDestructivePreview() {
    PreviewSurface {
        ConfirmationDialogLayout(
            title = "Void item?",
            message = "This cannot be undone and will remove the item from the check.",
            confirmLabel = stringResource(R.string.action_confirm),
            onConfirm = {},
            onDismiss = {},
            destructive = true,
        )
    }
}

@ComponentPreviews
@Composable
private fun ConfirmationDialogLoadingPreview() {
    PreviewSurface {
        ConfirmationDialogLayout(
            title = "Send order?",
            message = "This will send the ticket to the kitchen immediately.",
            confirmLabel = stringResource(R.string.action_confirm),
            onConfirm = {},
            onDismiss = {},
            confirmLoading = true,
        )
    }
}

@ComponentPreviews
@Composable
private fun ConfirmationDialogLongTextPreview() {
    PreviewSurface {
        ConfirmationDialogLayout(
            title = PreviewData.LONG_NAME,
            message = PreviewData.LONG_DESCRIPTION,
            confirmLabel = PreviewData.TRANSLATED_LABEL,
            onConfirm = {},
            onDismiss = {},
        )
    }
}
