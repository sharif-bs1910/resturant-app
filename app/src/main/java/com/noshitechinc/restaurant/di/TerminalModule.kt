package com.noshitechinc.restaurant.di

import com.noshitechinc.restaurant.BuildConfig
import com.noshitechinc.restaurant.core.network.BaseClient
import com.noshitechinc.restaurant.core.terminal.TerminalClient
import com.noshitechinc.restaurant.core.terminal.TerminalConfig
import com.noshitechinc.restaurant.core.terminal.fake.FakeTerminalClient
import com.noshitechinc.restaurant.core.terminal.stripe.StripeTerminalClient
import com.noshitechinc.restaurant.data.remote.api.TerminalStubApi
import com.noshitechinc.restaurant.data.repository.TerminalPaymentRepositoryImpl
import com.noshitechinc.restaurant.data.repository.TerminalTokenRepositoryImpl
import com.noshitechinc.restaurant.domain.repository.TerminalPaymentRepository
import com.noshitechinc.restaurant.domain.repository.TerminalTokenRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
abstract class TerminalBindingsModule {
    @Binds
    @Singleton
    abstract fun terminalTokenRepository(impl: TerminalTokenRepositoryImpl): TerminalTokenRepository

    @Binds
    @Singleton
    abstract fun terminalPaymentRepository(impl: TerminalPaymentRepositoryImpl): TerminalPaymentRepository
}

@Module
@InstallIn(SingletonComponent::class)
object TerminalModule {
    @Provides
    @Singleton
    fun terminalConfig(): TerminalConfig = TerminalConfig(
        enabled = BuildConfig.TERMINAL_ENABLED,
        locationId = BuildConfig.TERMINAL_LOCATION_ID,
        stubBaseUrl = BuildConfig.TERMINAL_STUB_BASE_URL,
        useSimulatedReader = BuildConfig.TERMINAL_USE_SIMULATED_READER,
    )

    @Provides
    @Singleton
    fun terminalStubApi(@BaseClient client: OkHttpClient, json: Json, config: TerminalConfig): TerminalStubApi {
        val baseUrl = config.stubBaseUrl.ifBlank { "http://127.0.0.1/" }
            .let { if (it.endsWith("/")) it else "$it/" }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TerminalStubApi::class.java)
    }

    @Provides
    @Singleton
    fun terminalClient(config: TerminalConfig, stripe: StripeTerminalClient): TerminalClient =
        if (config.enabled) stripe else FakeTerminalClient(initialized = false)
}
