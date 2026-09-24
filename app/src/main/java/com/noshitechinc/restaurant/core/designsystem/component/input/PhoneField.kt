package com.noshitechinc.restaurant.core.designsystem.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.noshitechinc.restaurant.core.common.format.UsPhoneFormatter
import com.noshitechinc.restaurant.core.common.market.MarketConfig
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun PhoneField(
    digits: String,
    onDigitsChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    enabled: Boolean = true,
) {
    AppTextField(
        value = digits,
        onValueChange = { onDigitsChange(UsPhoneFormatter.digitsOnly(it)) },
        modifier = modifier,
        label = label,
        errorText = errorText,
        enabled = enabled,
        prefix = "${MarketConfig.UnitedStates.phoneCountryCode} ",
        visualTransformation = UsPhoneVisualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
    )
}

@ComponentPreviews
@Composable
private fun PhoneFieldStatesPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            PhoneField(digits = "", onDigitsChange = {}, label = "Phone")
            PhoneField(digits = "5551234567", onDigitsChange = {}, label = "Phone")
            PhoneField(digits = "555", onDigitsChange = {}, label = "Phone", errorText = "Incomplete")
            PhoneField(digits = "5551234567", onDigitsChange = {}, label = "Phone", enabled = false)
        }
    }
}

@ComponentPreviews
@Composable
private fun PhoneFieldFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        PhoneField(digits = "5551234567", onDigitsChange = {}, label = "Phone")
    }
}
