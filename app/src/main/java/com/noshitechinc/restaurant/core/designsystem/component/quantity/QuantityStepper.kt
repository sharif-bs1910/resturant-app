package com.noshitechinc.restaurant.core.designsystem.component.quantity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.component.button.AppIconButton
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

object QuantityStepperTags {
    const val DECREMENT = "stepper_decrement"
    const val INCREMENT = "stepper_increment"
    const val VALUE = "stepper_value"
}

@Composable
fun QuantityStepper(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 0,
    max: Int = 99,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        AppIconButton(
            icon = Icons.Filled.Remove,
            contentDescription = stringResource(R.string.a11y_decrease),
            onClick = { onQuantityChange(quantity - 1) },
            enabled = enabled && quantity > min,
            modifier = Modifier.testTag(QuantityStepperTags.DECREMENT),
        )
        Text(
            text = quantity.toString(),
            style = AppTheme.typography.priceMedium,
            textAlign = TextAlign.Center,
            color = if (enabled) AppTheme.colors.textPrimary else AppTheme.colors.textDisabled,
            modifier = Modifier
                .widthIn(min = AppTheme.sizes.stepperValueMinWidth)
                .testTag(QuantityStepperTags.VALUE),
        )
        AppIconButton(
            icon = Icons.Filled.Add,
            contentDescription = stringResource(R.string.a11y_increase),
            onClick = { onQuantityChange(quantity + 1) },
            enabled = enabled && quantity < max,
            modifier = Modifier.testTag(QuantityStepperTags.INCREMENT),
        )
    }
}

@ComponentPreviews
@Composable
private fun QuantityStepperAtMinPreview() {
    PreviewSurface {
        QuantityStepper(quantity = 0, onQuantityChange = {})
    }
}

@ComponentPreviews
@Composable
private fun QuantityStepperMiddlePreview() {
    PreviewSurface {
        QuantityStepper(quantity = 5, onQuantityChange = {})
    }
}

@ComponentPreviews
@Composable
private fun QuantityStepperAtMaxPreview() {
    PreviewSurface {
        QuantityStepper(quantity = 99, onQuantityChange = {})
    }
}

@ComponentPreviews
@Composable
private fun QuantityStepperDisabledPreview() {
    PreviewSurface {
        QuantityStepper(quantity = 3, onQuantityChange = {}, enabled = false)
    }
}

@ComponentPreviews
@Composable
private fun QuantityStepperLongValuePreview() {
    PreviewSurface {
        QuantityStepper(quantity = 999, onQuantityChange = {}, max = 999)
    }
}

@ComponentPreviews
@Composable
private fun QuantityStepperStatesColumnPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            QuantityStepper(quantity = 0, onQuantityChange = {})
            QuantityStepper(quantity = 5, onQuantityChange = {})
            QuantityStepper(quantity = 99, onQuantityChange = {})
            QuantityStepper(quantity = 3, onQuantityChange = {}, enabled = false)
            QuantityStepper(quantity = 999, onQuantityChange = {}, max = 999)
        }
    }
}
