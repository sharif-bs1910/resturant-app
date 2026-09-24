package com.noshitechinc.restaurant.di

import com.noshitechinc.restaurant.core.auth.AuthProvider
import com.noshitechinc.restaurant.core.auth.KeystoreTokenCipher
import com.noshitechinc.restaurant.core.auth.KeystoreTokenStore
import com.noshitechinc.restaurant.core.auth.TokenCipher
import com.noshitechinc.restaurant.core.auth.TokenStore
import com.noshitechinc.restaurant.core.network.ConnectivityNetworkMonitor
import com.noshitechinc.restaurant.core.network.NetworkMonitor
import com.noshitechinc.restaurant.core.network.error.ErrorBodyParser
import com.noshitechinc.restaurant.core.network.error.JsonErrorBodyParser
import com.noshitechinc.restaurant.data.auth.RefreshTokenAuthProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {
    @Binds
    abstract fun tokenStore(impl: KeystoreTokenStore): TokenStore

    @Binds
    abstract fun tokenCipher(impl: KeystoreTokenCipher): TokenCipher

    @Binds
    abstract fun authProvider(impl: RefreshTokenAuthProvider): AuthProvider

    @Binds
    abstract fun errorBodyParser(impl: JsonErrorBodyParser): ErrorBodyParser

    @Binds
    abstract fun networkMonitor(impl: ConnectivityNetworkMonitor): NetworkMonitor
}
