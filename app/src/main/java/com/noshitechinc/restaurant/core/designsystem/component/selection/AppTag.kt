package com.noshitechinc.restaurant.core.designsystem.component.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

enum class TagVariant { Mono, Brand, Inverse }

@Immutable
private data class TagColors(val container: Color, val content: Color)

@Composable
private fun TagVariant.colors(): TagColors {
    val c = AppTheme.colors
    return when (this) {
        TagVariant.Mono -> TagColors(c.surface, c.textPrimary)
        TagVariant.Brand -> TagColors(c.primary, c.onPrimary)
        TagVariant.Inverse -> TagColors(c.textPrimary, c.background)
    }
}

@Composable
fun AppTag(text: String, modifier: Modifier = Modifier, variant: TagVariant = TagVariant.Mono) {
    val colors = variant.colors()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AppTheme.radius.pill),
        color = colors.container,
        contentColor = colors.content,
    ) {
        Text(
            text = text.uppercase(),
            style = AppTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.md,
                vertical = AppTheme.sizes.tagPaddingVertical,
            ),
        )
    }
}

@ComponentPreviews
@Composable
private fun AppTagVariantsPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            TagVariant.entries.forEach { variant ->
                AppTag(text = variant.name, variant = variant)
            }
        }
    }
}
