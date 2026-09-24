package com.noshitechinc.restaurant.core.common.market

import java.util.Locale

data class MarketConfig(val locale: Locale, val currencyCode: String, val phoneCountryCode: String, val phoneNationalDigits: Int) {
    companion object {
        val UnitedStates = MarketConfig(
            locale = Locale.US,
            currencyCode = "USD",
            phoneCountryCode = "+1",
            phoneNationalDigits = 10,
        )
    }
}
