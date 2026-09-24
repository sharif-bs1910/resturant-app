package com.noshitechinc.restaurant.di

import com.noshitechinc.restaurant.BuildConfig
import com.noshitechinc.restaurant.core.common.AppEnvironment
import com.noshitechinc.restaurant.core.common.AppInfo
import com.noshitechinc.restaurant.core.common.format.CurrencyFormatter
import com.noshitechinc.restaurant.core.common.market.MarketConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun appInfo(): AppInfo = AppInfo(
        environment = AppEnvironment.from(BuildConfig.ENVIRONMENT),
        versionName = BuildConfig.VERSION_NAME,
        versionCode = BuildConfig.VERSION_CODE,
    )

    @Provides
    fun marketConfig(): MarketConfig = MarketConfig.UnitedStates

    @Provides
    @Singleton
    fun currencyFormatter(market: MarketConfig): CurrencyFormatter = CurrencyFormatter(market)
}
