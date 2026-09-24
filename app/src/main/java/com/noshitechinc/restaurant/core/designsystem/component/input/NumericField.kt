package com.noshitechinc.restaurant.core.designsystem.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun NumericField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    maxDigits: Int = 6,
    helperText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
) {
    AppTextField(
        value = value,
        onValueChange = { onValueChange(it.filter(Char::isDigit).take(maxDigits)) },
        modifier = modifier,
        label = label,
        helperText = helperText,
        errorText = errorText,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@ComponentPreviews
@Composable
private fun NumericFieldStatesPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            NumericField(value = "", onValueChange = {}, label = "Quantity")
            NumericField(value = "12", onValueChange = {}, label = "Quantity")
            NumericField(value = "12", onValueChange = {}, label = "Quantity", errorText = "Too high")
            NumericField(value = "12", onValueChange = {}, label = "Quantity", enabled = false)
            NumericField(value = "999999", onValueChange = {}, label = "Quantity", helperText = "Max 6 digits")
        }
    }
}

@ComponentPreviews
@Composable
private fun NumericFieldFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        NumericField(value = "42", onValueChange = {}, label = "Quantity")
    }
}
