package com.noshitechinc.restaurant.core.designsystem.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.state_empty_title),
    message: String? = stringResource(R.string.state_empty_message),
    icon: ImageVector = Icons.Outlined.Inbox,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    StatusMessage(
        icon = icon,
        title = title,
        modifier = modifier,
        message = message,
        action = if (actionLabel != null && onAction != null) {
            {
                AppButton(text = actionLabel, onClick = onAction)
            }
        } else {
            null
        },
    )
}

@Composable
fun NoSearchResultsState(query: String, modifier: Modifier = Modifier, onClearSearch: (() -> Unit)? = null) {
    StatusMessage(
        icon = Icons.Outlined.SearchOff,
        title = stringResource(R.string.state_no_results_title, query),
        modifier = modifier,
        message = stringResource(R.string.state_no_results_message),
        titleMaxLines = 3,
        action = onClearSearch?.let { clear ->
            {
                AppButton(
                    text = stringResource(R.string.action_clear_search),
                    onClick = clear,
                )
            }
        },
    )
}

@ComponentPreviews
@Composable
private fun EmptyStateDefaultPreview() {
    PreviewSurface {
        EmptyState()
    }
}

@ComponentPreviews
@Composable
private fun EmptyStateCustomWithActionPreview() {
    PreviewSurface {
        EmptyState(
            title = "No open orders",
            message = "New tickets will show up here as guests place orders.",
            actionLabel = "Refresh",
            onAction = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun NoSearchResultsStatePreview() {
    PreviewSurface {
        NoSearchResultsState(
            query = PreviewData.SEARCH_QUERY,
            onClearSearch = {},
        )
    }
}
