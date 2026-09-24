package com.noshitechinc.restaurant.core.designsystem.component.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.designsystem.theme.StatusTone
import com.noshitechinc.restaurant.core.designsystem.theme.containerColor
import com.noshitechinc.restaurant.core.designsystem.theme.contentColor

@Composable
fun StatusBadge(text: String, tone: StatusTone, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AppTheme.radius.pill),
        color = tone.containerColor(),
        contentColor = tone.contentColor(),
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = AppTheme.sizes.badgeMinHeight)
                .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.sizes.iconSm),
                )
            }
            Text(
                text = text,
                style = AppTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun StatusBadgeTonesPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            StatusTone.entries.forEach { tone ->
                StatusBadge(text = tone.name, tone = tone)
                StatusBadge(text = tone.name, tone = tone, icon = Icons.Filled.CheckCircle)
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun StatusBadgeLongTextPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            StatusTone.entries.forEach { tone ->
                StatusBadge(
                    text = PreviewData.TRANSLATED_LABEL,
                    tone = tone,
                    modifier = Modifier.width(140.dp),
                )
                StatusBadge(
                    text = PreviewData.TRANSLATED_LABEL,
                    tone = tone,
                    icon = Icons.Filled.CheckCircle,
                    modifier = Modifier.width(140.dp),
                )
            }
        }
    }
}
