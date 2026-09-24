# add-api-endpoint — code skeletons

Replace `Xxx` / `xxx`. Example endpoint: `api:xxx:list-items`, `GET xxx/items` → `[{ "id": "1", "name": "Latte" }]`.

## `SRC/data/remote/dto/XxxDtos.kt`

```kotlin
package com.noshitechinc.restaurant.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class XxxItemDto(val id: String, val name: String)
```

## `SRC/data/remote/api/XxxApi.kt`

```kotlin
package com.noshitechinc.restaurant.data.remote.api

import com.noshitechinc.restaurant.data.remote.dto.XxxItemDto
import retrofit2.http.GET

interface XxxApi {
    @GET("xxx/items")
    suspend fun getItems(): List<XxxItemDto>
}
```

Unauthenticated variant: add `@Headers("$NO_AUTH_HEADER: true")` above the HTTP annotation (see `AuthApi.refresh`).

## `SRC/di/NetworkModule.kt` (add to `object NetworkModule`)

Also add `import com.noshitechinc.restaurant.data.remote.api.XxxApi` (`Retrofit`, `Provides` and `Singleton` are already imported there).

```kotlin
    @Provides
    @Singleton
    fun xxxApi(retrofit: Retrofit): XxxApi = retrofit.create(XxxApi::class.java)
```

## `SRC/domain/model/XxxItem.kt`

```kotlin
package com.noshitechinc.restaurant.domain.model

data class XxxItem(val id: String, val name: String)
```

## `SRC/data/mapper/XxxMappers.kt`

```kotlin
package com.noshitechinc.restaurant.data.mapper

import com.noshitechinc.restaurant.data.remote.dto.XxxItemDto
import com.noshitechinc.restaurant.domain.model.XxxItem

fun XxxItemDto.toDomain(): XxxItem = XxxItem(id = id, name = name)
```

## `SRC/domain/repository/XxxRepository.kt`

```kotlin
package com.noshitechinc.restaurant.domain.repository

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.domain.model.XxxItem

interface XxxRepository {
    suspend fun getItems(): ApiResult<List<XxxItem>>
}
```

## `SRC/data/repository/XxxRepositoryImpl.kt`

```kotlin
package com.noshitechinc.restaurant.data.repository

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.map
import com.noshitechinc.restaurant.core.network.SafeApiCall
import com.noshitechinc.restaurant.data.mapper.toDomain
import com.noshitechinc.restaurant.data.remote.api.XxxApi
import com.noshitechinc.restaurant.domain.model.XxxItem
import com.noshitechinc.restaurant.domain.repository.XxxRepository
import javax.inject.Inject

class XxxRepositoryImpl @Inject constructor(
    private val api: XxxApi,
    private val safeApiCall: SafeApiCall,
) : XxxRepository {
    override suspend fun getItems(): ApiResult<List<XxxItem>> =
        safeApiCall { api.getItems() }.map { items -> items.map { it.toDomain() } }
}
```

## `SRC/di/BindingsModule.kt` (add to `abstract class BindingsModule`)

```kotlin
    @Binds
    abstract fun xxxRepository(impl: XxxRepositoryImpl): XxxRepository
```

## `TEST/data/repository/XxxRepositoryImplTest.kt`

```kotlin
package com.noshitechinc.restaurant.data.repository

import com.noshitechinc.restaurant.core.common.ApiResult
import com.noshitechinc.restaurant.core.common.AppError
import com.noshitechinc.restaurant.core.network.SafeApiCall
import com.noshitechinc.restaurant.core.network.error.ErrorMapper
import com.noshitechinc.restaurant.core.network.error.JsonErrorBodyParser
import com.noshitechinc.restaurant.data.remote.api.XxxApi
import com.noshitechinc.restaurant.domain.model.XxxItem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class XxxRepositoryImplTest {
    private val server = MockWebServer().apply { start() }
    private val json = Json { ignoreUnknownKeys = true }
    private val api = Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(XxxApi::class.java)
    private val safeApiCall = SafeApiCall(ErrorMapper(JsonErrorBodyParser(json)))
    private val repository = XxxRepositoryImpl(api, safeApiCall)

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `a successful response is mapped to the domain model`() = runTest {
        server.enqueue(MockResponse().setBody("""[{"id":"1","name":"Latte","unknown":true}]"""))
        assertEquals(ApiResult.Success(listOf(XxxItem("1", "Latte"))), repository.getItems())
        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/xxx/items", request.path)
    }

    @Test
    fun `a 404 becomes NotFound`() = runTest {
        server.enqueue(MockResponse().setResponseCode(404))
        assertEquals(ApiResult.Failure(AppError.NotFound), repository.getItems())
    }
}
```
