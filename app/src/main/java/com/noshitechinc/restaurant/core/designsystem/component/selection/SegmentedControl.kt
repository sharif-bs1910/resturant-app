package com.noshitechinc.restaurant.core.designsystem.component.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val c = AppTheme.colors
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                enabled = enabled,
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = c.primaryContainer,
                    activeContentColor = c.onPrimaryContainer,
                    inactiveContainerColor = c.surface,
                    inactiveContentColor = c.textPrimary,
                    activeBorderColor = c.primary,
                    inactiveBorderColor = c.outline,
                ),
                modifier = Modifier.heightIn(min = AppTheme.sizes.minTouchTarget),
            ) {
                Text(
                    text = option,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun SegmentedControlTwoOptionsPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            SegmentedControl(
                options = listOf("Dine in", "Takeout"),
                selectedIndex = 0,
                onSelect = {},
            )
            SegmentedControl(
                options = listOf("Dine in", "Takeout"),
                selectedIndex = 1,
                onSelect = {},
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun SegmentedControlThreeOptionsPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            listOf(0, 1, 2).forEach { selected ->
                SegmentedControl(
                    options = listOf("Open", "Preparing", "Ready"),
                    selectedIndex = selected,
                    onSelect = {},
                )
            }
            SegmentedControl(
                options = listOf("Open", "Preparing", "Ready"),
                selectedIndex = 0,
                onSelect = {},
                enabled = false,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun SegmentedControlLongLabelsPreview() {
    PreviewSurface {
        SegmentedControl(
            options = listOf(PreviewData.TRANSLATED_LABEL, PreviewData.TRANSLATED_LABEL),
            selectedIndex = 0,
            onSelect = {},
        )
    }
}
