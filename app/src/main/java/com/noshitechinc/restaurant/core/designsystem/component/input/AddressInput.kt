package com.noshitechinc.restaurant.core.designsystem.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.adaptive.rememberAdaptiveInfo
import com.noshitechinc.restaurant.core.common.model.PostalAddress
import com.noshitechinc.restaurant.core.common.validation.AddressField
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun AddressInput(
    address: PostalAddress,
    onAddressChange: (PostalAddress) -> Unit,
    modifier: Modifier = Modifier,
    invalidFields: Set<AddressField> = emptySet(),
    enabled: Boolean = true,
) {
    val adaptive = rememberAdaptiveInfo()
    val streetError = if (AddressField.Street in invalidFields) {
        stringResource(R.string.error_address_street)
    } else {
        null
    }
    val cityError = if (AddressField.City in invalidFields) {
        stringResource(R.string.error_address_city)
    } else {
        null
    }
    val stateError = if (AddressField.State in invalidFields) {
        stringResource(R.string.error_address_state)
    } else {
        null
    }
    val zipError = if (AddressField.Zip in invalidFields) {
        stringResource(R.string.error_address_zip)
    } else {
        null
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        AppTextField(
            value = address.street,
            onValueChange = { onAddressChange(address.copy(street = it)) },
            label = stringResource(R.string.address_street),
            errorText = streetError,
            enabled = enabled,
            singleLine = false,
            maxLines = 3,
        )
        if (adaptive.usesTwoPane) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                AppTextField(
                    value = address.unit,
                    onValueChange = { onAddressChange(address.copy(unit = it)) },
                    label = stringResource(R.string.address_unit),
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
                AppTextField(
                    value = address.city,
                    onValueChange = { onAddressChange(address.copy(city = it)) },
                    label = stringResource(R.string.address_city),
                    errorText = cityError,
                    enabled = enabled,
                    modifier = Modifier.weight(2f),
                )
                AppTextField(
                    value = address.state,
                    onValueChange = { onAddressChange(address.copy(state = it.uppercase().take(2))) },
                    label = stringResource(R.string.address_state),
                    errorText = stateError,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                )
                AppTextField(
                    value = address.zip,
                    onValueChange = {
                        onAddressChange(
                            address.copy(zip = it.filter { c -> c.isDigit() || c == '-' }.take(10)),
                        )
                    },
                    label = stringResource(R.string.address_zip),
                    errorText = zipError,
                    enabled = enabled,
                    modifier = Modifier.weight(1.4f),
                )
            }
        } else {
            AppTextField(
                value = address.unit,
                onValueChange = { onAddressChange(address.copy(unit = it)) },
                label = stringResource(R.string.address_unit),
                enabled = enabled,
            )
            AppTextField(
                value = address.city,
                onValueChange = { onAddressChange(address.copy(city = it)) },
                label = stringResource(R.string.address_city),
                errorText = cityError,
                enabled = enabled,
            )
            AppTextField(
                value = address.state,
                onValueChange = { onAddressChange(address.copy(state = it.uppercase().take(2))) },
                label = stringResource(R.string.address_state),
                errorText = stateError,
                enabled = enabled,
            )
            AppTextField(
                value = address.zip,
                onValueChange = {
                    onAddressChange(
                        address.copy(zip = it.filter { c -> c.isDigit() || c == '-' }.take(10)),
                    )
                },
                label = stringResource(R.string.address_zip),
                errorText = zipError,
                enabled = enabled,
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun AddressInputEmptyPreview() {
    PreviewSurface {
        AddressInput(address = PostalAddress(), onAddressChange = {})
    }
}

@ComponentPreviews
@Composable
private fun AddressInputFilledPreview() {
    PreviewSurface {
        AddressInput(
            address = PostalAddress(
                street = PreviewData.ADDRESS_LONG,
                unit = "1400",
                city = "Austin",
                state = "TX",
                zip = "78753-1234",
            ),
            onAddressChange = {},
        )
    }
}

@ComponentPreviews
@Composable
private fun AddressInputInvalidPreview() {
    PreviewSurface {
        AddressInput(
            address = PostalAddress(),
            onAddressChange = {},
            invalidFields = AddressField.entries.toSet(),
        )
    }
}

@ComponentPreviews
@Composable
private fun AddressInputDisabledPreview() {
    PreviewSurface {
        AddressInput(
            address = PostalAddress(
                street = PreviewData.ADDRESS_LONG,
                city = "Austin",
                state = "TX",
                zip = "78753",
            ),
            onAddressChange = {},
            enabled = false,
        )
    }
}
