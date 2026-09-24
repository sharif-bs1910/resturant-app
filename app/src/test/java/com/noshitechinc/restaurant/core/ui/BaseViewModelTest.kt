package com.noshitechinc.restaurant.core.ui

import app.cash.turbine.test
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.testing.MainDispatcherRule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Rule

class BaseViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class TestViewModel : BaseViewModel() {
        var runs = 0

        fun submit(key: String = "save", gate: CompletableDeferred<Unit>): Job? = launchSubmit(key) {
            runs++
            gate.await()
        }

        fun submitFailing(): Job? = launchSubmit("save") { error("boom") }

        fun show(error: AppError) = presentError(error)
    }

    @Test
    fun `second submit with the same key is ignored while the first runs`() = runTest {
        val vm = TestViewModel()
        val gate = CompletableDeferred<Unit>()
        vm.submit(gate = gate)
        assertNull(vm.submit(gate = gate))
        assertEquals(1, vm.runs)
        assertTrue(vm.isSubmitting("save"))
        assertEquals(setOf("save"), vm.submittingKeys.value)
        gate.complete(Unit)
        assertFalse(vm.isSubmitting("save"))
    }

    @Test
    fun `different keys run concurrently`() = runTest {
        val vm = TestViewModel()
        val gate = CompletableDeferred<Unit>()
        vm.submit("save", gate)
        vm.submit("print", gate)
        assertEquals(setOf("save", "print"), vm.submittingKeys.value)
        gate.complete(Unit)
        assertEquals(emptySet(), vm.submittingKeys.value)
    }

    @Test
    fun `key is released when the job is cancelled`() = runTest {
        val vm = TestViewModel()
        val job = vm.submit(gate = CompletableDeferred())
        job?.cancel()
        assertFalse(vm.isSubmitting("save"))
    }

    @Test
    fun `failure releases the key and shows an error dialog`() = runTest {
        val vm = TestViewModel()
        vm.effects.test {
            vm.submitFailing()
            assertIs<AppError.Unknown>(assertIs<ShowErrorDialog>(awaitItem()).error)
        }
        assertFalse(vm.isSubmitting("save"))
    }

    @Test
    fun `connectivity errors are shown as messages`() = runTest {
        val vm = TestViewModel()
        vm.effects.test {
            vm.show(AppError.NoInternet)
            assertEquals(MessageTone.Error, assertIs<ShowMessage>(awaitItem()).tone)
        }
    }

    @Test
    fun `effects sent before collection are buffered`() = runTest {
        val vm = TestViewModel()
        vm.show(AppError.NotFound)
        vm.effects.test {
            assertEquals(ShowErrorDialog(AppError.NotFound), awaitItem())
        }
    }
}
