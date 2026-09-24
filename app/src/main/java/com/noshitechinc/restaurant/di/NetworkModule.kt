package com.noshitechinc.restaurant.di

import com.noshitechinc.restaurant.BuildConfig
import com.noshitechinc.restaurant.core.network.AUTHORIZATION_HEADER
import com.noshitechinc.restaurant.core.network.AuthenticatedClient
import com.noshitechinc.restaurant.core.network.BaseClient
import com.noshitechinc.restaurant.core.network.TokenAuthenticator
import com.noshitechinc.restaurant.core.network.interceptor.AuthInterceptor
import com.noshitechinc.restaurant.core.network.interceptor.HeaderInterceptor
import com.noshitechinc.restaurant.data.remote.api.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val jsonMediaType = "application/json".toMediaType()

    @Provides
    @Singleton
    fun json(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun loggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor { Timber.tag("HTTP").d(it) }.apply {
        level = HttpLoggingInterceptor.Level.valueOf(BuildConfig.HTTP_LOG_LEVEL)
        redactHeader(AUTHORIZATION_HEADER)
        redactHeader("Cookie")
        redactHeader("Set-Cookie")
    }

    @Provides
    @Singleton
    @BaseClient
    fun baseClient(headerInterceptor: HeaderInterceptor, logging: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(BuildConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(BuildConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(BuildConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
        .addInterceptor(logging)
        .build()

    @Provides
    @Singleton
    @AuthenticatedClient
    fun authenticatedClient(
        @BaseClient base: OkHttpClient,
        authInterceptor: AuthInterceptor,
        authenticator: TokenAuthenticator,
    ): OkHttpClient = base.newBuilder()
        .apply { interceptors().add(1, authInterceptor) }
        .authenticator(authenticator)
        .build()

    @Provides
    @Singleton
    fun retrofit(@AuthenticatedClient client: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory(jsonMediaType))
        .build()

    @Provides
    @Singleton
    fun authApi(@BaseClient client: OkHttpClient, json: Json): AuthApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory(jsonMediaType))
        .build()
        .create(AuthApi::class.java)
}
