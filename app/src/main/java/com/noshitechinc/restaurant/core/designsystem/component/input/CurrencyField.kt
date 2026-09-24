package com.noshitechinc.restaurant.core.designsystem.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.format.CurrencyFormatter
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun CurrencyField(
    cents: Long,
    onCentsChange: (Long) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    formatter: CurrencyFormatter = remember { CurrencyFormatter() },
    helperText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
) {
    val raw = if (cents == 0L) "" else cents.toString()
    val transformation = remember(formatter) { CurrencyVisualTransformation(formatter) }
    AppTextField(
        value = raw,
        onValueChange = { onCentsChange(CurrencyFormatter.centsFromDigits(it)) },
        modifier = modifier,
        label = label,
        placeholder = stringResource(R.string.input_amount_placeholder),
        helperText = helperText,
        errorText = errorText,
        enabled = enabled,
        visualTransformation = transformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}

@ComponentPreviews
@Composable
private fun CurrencyFieldStatesPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            CurrencyField(cents = 0L, onCentsChange = {}, label = "Amount")
            CurrencyField(cents = 123_456L, onCentsChange = {}, label = "Amount")
            CurrencyField(cents = 1_234_567_890L, onCentsChange = {}, label = "Amount")
            CurrencyField(cents = 500L, onCentsChange = {}, label = "Amount", errorText = "Required")
            CurrencyField(cents = 500L, onCentsChange = {}, label = "Amount", enabled = false)
        }
    }
}

@ComponentPreviews
@Composable
private fun CurrencyFieldFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        CurrencyField(cents = 1_250L, onCentsChange = {}, label = "Amount")
    }
}
