package com.noshitechinc.restaurant.core.network.interceptor

import com.noshitechinc.restaurant.core.common.AppInfo
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor @Inject constructor(private val appInfo: AppInfo) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.proceed(
        chain.request().newBuilder()
            .header("Accept", "application/json")
            .header("Accept-Language", "en")
            .header("X-App-Version", appInfo.versionName)
            .header("X-Platform", "android")
            .build(),
    )
}
