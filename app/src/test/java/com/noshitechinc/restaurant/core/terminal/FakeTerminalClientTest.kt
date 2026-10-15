package com.noshitechinc.restaurant.core.terminal

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.terminal.fake.FakeTerminalClient
import com.noshitechinc.restaurant.core.terminal.model.PaymentOutcome
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

class FakeTerminalClientTest {
    @Test
    fun `discover connect and pay succeed`() = runTest {
        val client = FakeTerminalClient(
            nextPaymentOutcome = PaymentOutcome.Succeeded("pi_ok"),
        )
        val discover = assertIs<ApiResult.Success<List<com.noshitechinc.restaurant.core.terminal.model.ReaderInfo>>>(
            client.discoverReaders(simulated = true),
        )
        assertEquals(1, discover.data.size)
        val connected = assertIs<ApiResult.Success<com.noshitechinc.restaurant.core.terminal.model.ReaderInfo>>(
            client.connect(discover.data.first(), "tml_x"),
        )
        assertEquals("tml_x", connected.data.locationId)
        val paid = assertIs<ApiResult.Success<PaymentOutcome>>(client.processPayment("secret"))
        assertIs<PaymentOutcome.Succeeded>(paid.data)
    }

    @Test
    fun `pay can decline`() = runTest {
        val client = FakeTerminalClient(
            nextPaymentOutcome = PaymentOutcome.Declined("pi_bad", "declined"),
        )
        val discover = assertIs<ApiResult.Success<List<com.noshitechinc.restaurant.core.terminal.model.ReaderInfo>>>(
            client.discoverReaders(true),
        )
        client.connect(discover.data.first(), "tml_x")
        val paid = assertIs<ApiResult.Success<PaymentOutcome>>(client.processPayment("secret"))
        assertIs<PaymentOutcome.Declined>(paid.data)
    }
}
