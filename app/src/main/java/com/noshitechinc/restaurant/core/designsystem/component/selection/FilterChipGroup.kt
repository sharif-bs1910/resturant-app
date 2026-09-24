package com.noshitechinc.restaurant.core.designsystem.component.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

data class FilterOption(val id: String, val label: String, val count: Int? = null)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipGroup(
    options: List<FilterOption>,
    selectedIds: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val c = AppTheme.colors
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        options.forEach { option ->
            val selected = option.id in selectedIds
            val labelText = option.count?.let {
                stringResource(R.string.filter_option_with_count, option.label, it)
            } ?: option.label
            FilterChip(
                selected = selected,
                onClick = { onToggle(option.id) },
                enabled = enabled,
                label = {
                    Text(
                        text = labelText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                leadingIcon = if (selected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(AppTheme.sizes.iconSm),
                        )
                    }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = c.primaryContainer,
                    selectedLabelColor = c.onPrimaryContainer,
                    containerColor = c.surface,
                    labelColor = c.textPrimary,
                ),
                modifier = Modifier.heightIn(min = AppTheme.sizes.minTouchTarget),
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun FilterChipGroupNoneSelectedPreview() {
    PreviewSurface {
        FilterChipGroup(
            options = previewOptions(),
            selectedIds = emptySet(),
            onToggle = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun FilterChipGroupSomeSelectedPreview() {
    PreviewSurface {
        FilterChipGroup(
            options = previewOptions(),
            selectedIds = setOf("vegetarian", "spicy"),
            onToggle = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun FilterChipGroupWrappingPreview() {
    PreviewSurface {
        FilterChipGroup(
            options = listOf(
                FilterOption("a", "Vegetarian", 12),
                FilterOption("b", "Vegan", 8),
                FilterOption("c", "Gluten-free", 5),
                FilterOption("d", "Spicy", 21),
                FilterOption("e", "Nut-free", 3),
                FilterOption("f", "Dairy-free", 7),
                FilterOption("g", "Halal", 15),
                FilterOption("h", "Kids menu", 4),
            ),
            selectedIds = setOf("a", "d", "g"),
            onToggle = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun FilterChipGroupDisabledPreview() {
    PreviewSurface {
        FilterChipGroup(
            options = previewOptions(),
            selectedIds = setOf("vegetarian"),
            onToggle = {},
            enabled = false,
        )
    }
}

@ComponentPreviews
@Composable
private fun FilterChipGroupLongLabelPreview() {
    PreviewSurface {
        FilterChipGroup(
            options = listOf(
                FilterOption("long", PreviewData.TRANSLATED_LABEL, 42),
            ),
            selectedIds = setOf("long"),
            onToggle = {},
        )
    }
}

private fun previewOptions(): List<FilterOption> = listOf(
    FilterOption("vegetarian", "Vegetarian", 12),
    FilterOption("vegan", "Vegan", 8),
    FilterOption("spicy", "Spicy"),
)
