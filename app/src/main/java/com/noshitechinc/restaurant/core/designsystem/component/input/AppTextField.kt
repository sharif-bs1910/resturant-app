package com.noshitechinc.restaurant.core.designsystem.component.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.interaction.LocalForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 4,
    leadingIcon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    prefix: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val c = AppTheme.colors
    val forcedFocused = LocalForcedInteraction.current == ForcedInteraction.Focused
    val shape = RoundedCornerShape(AppTheme.radius.md)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AppTheme.sizes.inputHeight),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = AppTheme.typography.bodyLarge,
        label = label?.let {
            {
                Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        placeholder = placeholder?.let {
            {
                Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(imageVector = it, contentDescription = null)
            }
        },
        trailingIcon = trailingContent,
        prefix = prefix?.let {
            {
                Text(it)
            }
        },
        supportingText = when {
            errorText != null -> {
                {
                    Text(
                        text = errorText,
                        color = c.destructive,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            helperText != null -> {
                {
                    Text(
                        text = helperText,
                        color = c.textSecondary,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            else -> null
        },
        isError = errorText != null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        interactionSource = source,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = c.primary,
            unfocusedBorderColor = if (forcedFocused) c.primary else c.outline,
            disabledBorderColor = c.outlineVariant,
            errorBorderColor = c.destructive,
            focusedLabelColor = c.primary,
            unfocusedLabelColor = if (forcedFocused) c.primary else c.textSecondary,
            errorLabelColor = c.destructive,
            cursorColor = c.primary,
            focusedContainerColor = c.surface,
            unfocusedContainerColor = c.surface,
            disabledContainerColor = c.disabledContainer,
            disabledTextColor = c.textDisabled,
        ),
    )
}

@ComponentPreviews
@Composable
private fun AppTextFieldEmptyPreview() {
    PreviewSurface {
        AppTextField(value = "", onValueChange = {}, label = "Name")
    }
}

@ComponentPreviews
@Composable
private fun AppTextFieldFilledPreview() {
    PreviewSurface {
        AppTextField(value = PreviewData.SHORT_NAME, onValueChange = {}, label = "Name")
    }
}

@ComponentPreviews
@Composable
private fun AppTextFieldPlaceholderHelperPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            AppTextField(
                value = "",
                onValueChange = {},
                label = "Name",
                placeholder = "Enter a name",
            )
            AppTextField(
                value = PreviewData.SHORT_NAME,
                onValueChange = {},
                label = "Name",
                helperText = "Shown on the receipt",
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun AppTextFieldErrorDisabledReadOnlyPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            AppTextField(
                value = "",
                onValueChange = {},
                label = "Name",
                errorText = "Required",
            )
            AppTextField(
                value = PreviewData.SHORT_NAME,
                onValueChange = {},
                label = "Name",
                enabled = false,
            )
            AppTextField(
                value = PreviewData.SHORT_NAME,
                onValueChange = {},
                label = "Name",
                readOnly = true,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun AppTextFieldFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        AppTextField(value = PreviewData.SHORT_NAME, onValueChange = {}, label = "Name")
    }
}

@ComponentPreviews
@Composable
private fun AppTextFieldMultilineAndLongLabelPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            AppTextField(
                value = PreviewData.ADDRESS_LONG,
                onValueChange = {},
                label = "Notes",
                singleLine = false,
                maxLines = 3,
            )
            AppTextField(
                value = "",
                onValueChange = {},
                label = PreviewData.TRANSLATED_LABEL,
                leadingIcon = Icons.Filled.Person,
            )
        }
    }
}
