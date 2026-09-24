package com.noshitechinc.restaurant.core.designsystem.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun SuccessDialogLayout(title: String, message: String, actionLabel: String, onAction: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(AppTheme.radius.xl),
        color = AppTheme.colors.surface,
        modifier = modifier.widthIn(max = AppTheme.sizes.dialogMaxWidth),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        ) {
            Box(
                modifier = Modifier
                    .size(AppTheme.sizes.illustration)
                    .background(AppTheme.colors.successContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.a11y_success),
                    tint = AppTheme.colors.success,
                    modifier = Modifier.size(AppTheme.sizes.illustration),
                )
            }
            Text(
                text = title,
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = message,
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
            AppButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun SuccessDialog(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    onDismissRequest: () -> Unit = onAction,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.spacing.xl),
            contentAlignment = Alignment.Center,
        ) {
            SuccessDialogLayout(
                title = title,
                message = message,
                actionLabel = actionLabel,
                onAction = onAction,
                modifier = modifier,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun SuccessDialogNormalPreview() {
    PreviewSurface {
        SuccessDialogLayout(
            title = "Order sent",
            message = "The kitchen has received the ticket.",
            actionLabel = stringResource(R.string.action_done),
            onAction = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun SuccessDialogLongMessagePreview() {
    PreviewSurface {
        SuccessDialogLayout(
            title = PreviewData.LONG_NAME,
            message = PreviewData.LONG_DESCRIPTION,
            actionLabel = stringResource(R.string.action_ok),
            onAction = {},
        )
    }
}
