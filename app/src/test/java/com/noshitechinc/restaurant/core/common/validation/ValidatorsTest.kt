package com.noshitechinc.restaurant.core.common.validation

import com.noshitechinc.restaurant.core.common.model.PostalAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidatorsTest {
    @Test
    fun `accepts valid US phone numbers`() {
        assertTrue(UsPhoneValidator.isValid("5552345678"))
    }

    @Test
    fun `rejects short numbers and invalid area or exchange codes`() {
        assertFalse(UsPhoneValidator.isValid("555234567"))
        assertFalse(UsPhoneValidator.isValid("1552345678"))
        assertFalse(UsPhoneValidator.isValid("5551345678"))
    }

    @Test
    fun `valid address has no invalid fields`() {
        val address = PostalAddress(street = "1 Main St", city = "Austin", state = "TX", zip = "73301")
        assertEquals(emptySet(), UsAddressValidator.invalidFields(address))
    }

    @Test
    fun `reports each invalid field`() {
        val address = PostalAddress(street = " ", city = "", state = "Texas", zip = "7330")
        assertEquals(AddressField.entries.toSet(), UsAddressValidator.invalidFields(address))
    }

    @Test
    fun `accepts ZIP plus four`() {
        val address = PostalAddress(street = "1 Main St", city = "Austin", state = "tx", zip = "73301-1234")
        assertEquals(emptySet(), UsAddressValidator.invalidFields(address))
    }
}
