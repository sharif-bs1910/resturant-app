package com.noshitechinc.restaurant.core.terminal.stripe

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.common.coroutines.ApplicationScope
import com.noshitechinc.restaurant.domain.repository.TerminalTokenRepository
import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback
import com.stripe.stripeterminal.external.callable.ConnectionTokenProvider
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Singleton
class AppConnectionTokenProvider @Inject constructor(
    private val tokenRepository: TerminalTokenRepository,
    @ApplicationScope private val scope: CoroutineScope,
) : ConnectionTokenProvider {
    override fun fetchConnectionToken(callback: ConnectionTokenCallback) {
        scope.launch {
            when (val result = tokenRepository.fetchConnectionToken()) {
                is ApiResult.Success -> callback.onSuccess(result.data)

                is ApiResult.Failure -> callback.onFailure(
                    ConnectionTokenException(result.error.toTokenMessage(), null),
                )
            }
        }
    }

    private fun AppError.toTokenMessage(): String = when (this) {
        AppError.NoInternet ->
            "Stub unreachable at TERMINAL_STUB_BASE_URL. Start tools/stripe-terminal-stub. " +
                "On a physical tablet use your PC LAN IP, not 10.0.2.2."

        AppError.Timeout -> "Timed out reaching the Terminal stub"

        is AppError.Server -> "Stub/Stripe error HTTP $code: ${message ?: "unknown"}"

        is AppError.Validation -> message ?: "Invalid stub response"

        else -> "Failed to fetch connection token from stub"
    }
}
