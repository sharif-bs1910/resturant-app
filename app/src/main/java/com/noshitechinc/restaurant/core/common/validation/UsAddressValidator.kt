package com.noshitechinc.restaurant.core.common.validation

import com.noshitechinc.restaurant.core.common.model.PostalAddress

enum class AddressField { Street, City, State, Zip }

object UsAddressValidator {
    private val zipRegex = Regex("^\\d{5}(-\\d{4})?$")
    private val stateRegex = Regex("^[A-Za-z]{2}$")

    fun invalidFields(address: PostalAddress): Set<AddressField> = buildSet {
        if (address.street.isBlank()) add(AddressField.Street)
        if (address.city.isBlank()) add(AddressField.City)
        if (!stateRegex.matches(address.state.trim())) add(AddressField.State)
        if (!zipRegex.matches(address.zip.trim())) add(AddressField.Zip)
    }
}
