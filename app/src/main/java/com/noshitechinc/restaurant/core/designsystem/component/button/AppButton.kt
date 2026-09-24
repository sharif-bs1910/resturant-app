package com.noshitechinc.restaurant.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.interaction.focusRing
import com.noshitechinc.restaurant.core.designsystem.interaction.rememberInteractionVisuals
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

enum class ButtonVariant { Primary, Secondary, Outline, Destructive }

enum class ButtonSize { Small, Medium, Large }

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val visuals = rememberInteractionVisuals(source)
    val colors = buttonColors(variant, enabled, visuals.pressed)
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val loadingDescription = stringResource(R.string.a11y_loading)
    Surface(
        onClick = { if (!loading) onClick() },
        enabled = enabled,
        shape = shape,
        color = colors.container,
        contentColor = colors.content,
        border = colors.border?.let { BorderStroke(AppTheme.border.thin, it) },
        interactionSource = source,
        modifier = modifier
            .heightIn(min = size.minHeight())
            .focusRing(visuals.focused, AppTheme.colors.focusRing, AppTheme.border.focus, shape)
            .semantics { if (loading) stateDescription = loadingDescription },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = size.horizontalPadding(), vertical = AppTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AppTheme.sizes.progressSmall),
                    color = colors.content,
                    strokeWidth = AppTheme.border.thick,
                )
            } else if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(AppTheme.sizes.iconMd),
                )
            }
            Text(
                text = text,
                style = size.textStyle(),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Immutable
internal data class ButtonColors(val container: Color, val content: Color, val border: Color?)

@Composable
internal fun buttonColors(variant: ButtonVariant, enabled: Boolean, pressed: Boolean): ButtonColors {
    val c = AppTheme.colors
    if (!enabled) {
        return if (variant == ButtonVariant.Outline) {
            ButtonColors(Color.Transparent, c.textDisabled, c.outlineVariant)
        } else {
            ButtonColors(c.disabledContainer, c.textDisabled, null)
        }
    }
    return when (variant) {
        ButtonVariant.Primary ->
            ButtonColors(if (pressed) c.primaryPressed else c.primary, c.onPrimary, null)

        ButtonVariant.Secondary ->
            ButtonColors(if (pressed) c.secondaryPressed else c.secondary, c.onSecondary, null)

        ButtonVariant.Outline ->
            ButtonColors(if (pressed) c.surfacePressed else Color.Transparent, c.primary, c.outline)

        ButtonVariant.Destructive ->
            ButtonColors(if (pressed) c.destructivePressed else c.destructive, c.onDestructive, null)
    }
}

@Composable
private fun ButtonSize.minHeight(): Dp = when (this) {
    ButtonSize.Small -> AppTheme.sizes.buttonSmall
    ButtonSize.Medium -> AppTheme.sizes.buttonMedium
    ButtonSize.Large -> AppTheme.sizes.buttonLarge
}

@Composable
private fun ButtonSize.horizontalPadding(): Dp = when (this) {
    ButtonSize.Small -> AppTheme.spacing.md
    ButtonSize.Medium -> AppTheme.spacing.lg
    ButtonSize.Large -> AppTheme.spacing.xl
}

@Composable
private fun ButtonSize.textStyle(): TextStyle = when (this) {
    ButtonSize.Small -> AppTheme.typography.labelMedium
    ButtonSize.Medium, ButtonSize.Large -> AppTheme.typography.labelLarge
}

@ComponentPreviews
@Composable
private fun AppButtonVariantsPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            ButtonVariant.entries.forEach { variant ->
                AppButton(text = variant.name, onClick = {}, variant = variant, leadingIcon = Icons.Filled.Add)
                AppButton(text = "${variant.name} disabled", onClick = {}, variant = variant, enabled = false)
                AppButton(text = "${variant.name} loading", onClick = {}, variant = variant, loading = true)
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun AppButtonPressedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Pressed) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            ButtonVariant.entries.forEach { AppButton(text = "${it.name} pressed", onClick = {}, variant = it) }
        }
    }
}

@ComponentPreviews
@Composable
private fun AppButtonFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            ButtonVariant.entries.forEach { AppButton(text = "${it.name} focused", onClick = {}, variant = it) }
        }
    }
}

@ComponentPreviews
@Composable
private fun AppButtonSizesAndLongLabelPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            ButtonSize.entries.forEach { AppButton(text = "Size ${it.name}", onClick = {}, size = it) }
            AppButton(
                text = PreviewData.TRANSLATED_LABEL,
                onClick = {},
                size = ButtonSize.Large,
                modifier = Modifier.width(220.dp),
            )
        }
    }
}
