package com.noshitechinc.restaurant.feature.terminalharness

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.terminal.TerminalConfig
import com.noshitechinc.restaurant.core.terminal.fake.FakeTerminalClient
import com.noshitechinc.restaurant.core.terminal.model.PaymentIntentRef
import com.noshitechinc.restaurant.core.terminal.model.PaymentOutcome
import com.noshitechinc.restaurant.core.terminal.model.PaymentRequest
import com.noshitechinc.restaurant.domain.repository.TerminalPaymentRepository
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class TerminalHarnessViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `discover and pay happy path`() = runTest {
        val terminal = FakeTerminalClient(nextPaymentOutcome = PaymentOutcome.Succeeded("pi_ok"))
        val payments = object : TerminalPaymentRepository {
            override suspend fun createPaymentIntent(request: PaymentRequest): ApiResult<PaymentIntentRef> =
                ApiResult.Success(PaymentIntentRef(id = "pi_ok", clientSecret = "secret"))
        }
        val vm = TerminalHarnessViewModel(
            terminalClient = terminal,
            paymentRepository = payments,
            config = TerminalConfig(
                enabled = true,
                locationId = "tml_test",
                stubBaseUrl = "http://127.0.0.1:4242/",
                useSimulatedReader = true,
            ),
        )
        advanceUntilIdle()
        vm.onDiscover()
        advanceUntilIdle()
        assertEquals(TerminalHarnessPhase.ReadyToConnect, vm.uiState.value.phase)
        vm.onConnect()
        advanceUntilIdle()
        assertEquals(TerminalHarnessPhase.Connected, vm.uiState.value.phase)
        vm.onPay()
        advanceUntilIdle()
        assertEquals(TerminalHarnessPhase.Succeeded, vm.uiState.value.phase)
        assertEquals("pi_ok", vm.uiState.value.lastPaymentIntentId)
    }

    @Test
    fun `launchSubmit ignores duplicate discover`() = runTest {
        val terminal = FakeTerminalClient(discoverDelayMs = 1_000)
        val payments = object : TerminalPaymentRepository {
            override suspend fun createPaymentIntent(request: PaymentRequest): ApiResult<PaymentIntentRef> =
                ApiResult.Success(PaymentIntentRef("pi", "sec"))
        }
        val vm = TerminalHarnessViewModel(
            terminalClient = terminal,
            paymentRepository = payments,
            config = TerminalConfig(true, "tml", "http://127.0.0.1/", true),
        )
        advanceUntilIdle()
        vm.onDiscover()
        vm.onDiscover()
        assertEquals(1, terminal.discoverCount)
    }
}
