package com.noshitechinc.restaurant.core.designsystem.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun StatusMessage(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    iconTint: Color = AppTheme.colors.textSecondary,
    titleMaxLines: Int = Int.MAX_VALUE,
    scrollable: Boolean = true,
    action: (@Composable () -> Unit)? = null,
) {
    val baseModifier = modifier
        .widthIn(max = AppTheme.sizes.formMaxWidth)
        .fillMaxWidth()
        .then(
            if (scrollable) {
                Modifier
                    .heightIn(max = AppTheme.sizes.contentMaxWidth)
                    .verticalScroll(rememberScrollState())
            } else {
                Modifier
            },
        )
        .padding(AppTheme.spacing.xl)
    Column(
        modifier = baseModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(AppTheme.sizes.illustration),
        )
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = titleMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (message != null) {
            Text(
                text = message,
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
        action?.invoke()
    }
}

@ComponentPreviews
@Composable
private fun StatusMessageTitleOnlyPreview() {
    PreviewSurface {
        StatusMessage(
            icon = Icons.Outlined.Inbox,
            title = "Nothing here yet",
        )
    }
}

@ComponentPreviews
@Composable
private fun StatusMessageWithMessageAndActionPreview() {
    PreviewSurface {
        StatusMessage(
            icon = Icons.Outlined.Inbox,
            title = "Nothing here yet",
            message = "Items will appear here once they are added.",
            action = { AppButton(text = "Add item", onClick = {}) },
        )
    }
}

@ComponentPreviews
@Composable
private fun StatusMessageLongMessagePreview() {
    PreviewSurface {
        StatusMessage(
            icon = Icons.Outlined.Inbox,
            title = PreviewData.LONG_NAME,
            message = PreviewData.LONG_DESCRIPTION,
            action = { AppButton(text = PreviewData.TRANSLATED_LABEL, onClick = {}) },
        )
    }
}
