package com.noshitechinc.restaurant.core.designsystem.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.input_search_placeholder),
    enabled: Boolean = true,
    onSearch: () -> Unit = {},
) {
    AppTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        label = null,
        placeholder = placeholder,
        enabled = enabled,
        leadingIcon = Icons.Filled.Search,
        trailingContent = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }, enabled = enabled) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.a11y_clear_text),
                    )
                }
            }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
    )
}

@ComponentPreviews
@Composable
private fun SearchFieldStatesPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            SearchField(query = "", onQueryChange = {})
            SearchField(query = PreviewData.SEARCH_QUERY, onQueryChange = {})
            SearchField(query = PreviewData.SEARCH_QUERY, onQueryChange = {}, enabled = false)
        }
    }
}

@ComponentPreviews
@Composable
private fun SearchFieldFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        SearchField(query = PreviewData.SHORT_NAME, onQueryChange = {})
    }
}
