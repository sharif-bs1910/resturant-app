package com.noshitechinc.restaurant.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.ui.error.toUiText
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {
    private val effectChannel = Channel<UiEffect>(Channel.BUFFERED)
    val effects: Flow<UiEffect> = effectChannel.receiveAsFlow()

    private val _submittingKeys = MutableStateFlow<Set<String>>(emptySet())
    val submittingKeys: StateFlow<Set<String>> = _submittingKeys.asStateFlow()

    fun isSubmitting(key: String): Boolean = key in _submittingKeys.value

    protected fun sendEffect(effect: UiEffect) {
        effectChannel.trySend(effect)
    }

    @Suppress("TooGenericExceptionCaught")
    protected fun launchSubmit(key: String, block: suspend CoroutineScope.() -> Unit): Job? {
        if (!tryAcquire(key)) return null
        return viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Submit '%s' failed", key)
                presentError(AppError.Unknown(e))
            } finally {
                _submittingKeys.update { it - key }
            }
        }
    }

    protected fun presentError(error: AppError) {
        when (error) {
            AppError.NoInternet, AppError.Timeout ->
                sendEffect(ShowMessage(error.toUiText(), MessageTone.Error))

            else -> sendEffect(ShowErrorDialog(error))
        }
    }

    private fun tryAcquire(key: String): Boolean {
        while (true) {
            val current = _submittingKeys.value
            if (key in current) return false
            if (_submittingKeys.compareAndSet(current, current + key)) return true
        }
    }
}
