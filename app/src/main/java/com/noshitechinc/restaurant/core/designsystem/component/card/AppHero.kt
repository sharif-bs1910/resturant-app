package com.noshitechinc.restaurant.core.designsystem.component.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.component.button.ButtonVariant
import com.noshitechinc.restaurant.core.designsystem.component.selection.AppTag
import com.noshitechinc.restaurant.core.designsystem.component.selection.TagVariant
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun AppHero(eyebrow: String, title: String, accentWord: String, actionLabel: String, onAction: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .widthIn(max = AppTheme.sizes.dialogMaxWidth)
            .heightIn(min = AppTheme.sizes.heroHeight),
        shape = RoundedCornerShape(AppTheme.radius.lg),
        color = AppTheme.colors.primary,
        contentColor = AppTheme.colors.onPrimary,
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
        ) {
            AppTag(text = eyebrow, variant = TagVariant.Inverse)
            Text(text = titleWithAccent(title, accentWord), style = AppTheme.typography.headlineMedium)
            AppButton(
                text = actionLabel,
                onClick = onAction,
                variant = ButtonVariant.Secondary,
            )
        }
    }
}

@Composable
private fun titleWithAccent(title: String, accentWord: String) = buildAnnotatedString {
    val start = title.indexOf(accentWord)
    if (start < 0) {
        append(title)
        return@buildAnnotatedString
    }
    append(title.substring(0, start))
    withStyle(SpanStyle(color = AppTheme.colors.secondary, fontStyle = FontStyle.Italic)) {
        append(accentWord)
    }
    append(title.substring(start + accentWord.length))
}

@ComponentPreviews
@Composable
private fun AppHeroPreview() {
    PreviewSurface {
        AppHero(
            eyebrow = "Hot and ready",
            title = "Dinner that tastes like a Friday.",
            accentWord = "Friday",
            actionLabel = "Order now",
            onAction = {},
        )
    }
}
