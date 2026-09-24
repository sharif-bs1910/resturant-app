---
name: add-api-endpoint
description: Adds a backend endpoint end to end in the Noshitech Restaurant Android app (API registry row, DTOs, Retrofit method, mapper, domain repository, safeApiCall implementation, Hilt binding, test, doc sync). Use when a feature needs to call a new or changed backend endpoint.
---

# Add an API endpoint

Follows `.cursor/rules/networking.mdc` and `architecture-mvvm.mdc`. Existing reference: `data/remote/api/AuthApi.kt`, `data/remote/dto/AuthDtos.kt`, `data/mapper/AuthMappers.kt`.

Paths: `SRC/` = `app/src/main/java/com/noshitechinc/restaurant/`, `TEST/` = `app/src/test/java/com/noshitechinc/restaurant/`, `CTX/` = `../resturant-app-architecture/`.
Names: `Xxx` = domain in PascalCase (`Menu`), `xxx` = domain in lower case (`menu`). Code skeletons for every step are in [skeletons.md](skeletons.md).

## 1. Confirm the contract

1. Look the endpoint up in `CTX/docs/03-context/API-REGISTRY.md` and the ticket or FR.
2. If method, path, auth, request or response is unknown: do not invent it. Add a PDR row to `CTX/docs/03-context/PENDING-DECISIONS.md` (next free `PDR-NNN`, question, safe default, owner role, needed by), add or update the registry row with status `Placeholder (PDR-NNN)`, tell the human and stop.
3. A brand-new endpoint (new Retrofit method, new DTOs) is additive: it needs the registry row and doc sync, but no impact analysis. Changing an existing endpoint or DTO contract (path, payload, status codes, auth) or a shared type (`ApiResult`, `AppError`, …) requires the `impact-analysis` skill and human approval before any edit.

## 2. Register the endpoint

Add or update the row in `CTX/docs/03-context/API-REGISTRY.md` in the same change:

| ID | Method | Path | Auth | Request | Response | Errors | Status | Consumers |
|---|---|---|---|---|---|---|---|---|
| api:xxx:list-items | GET | xxx/items | bearer | — | [{id, name}] | common mapping | Draft | XxxApi → XxxRepositoryImpl |

IDs are permanent; a replaced endpoint keeps its row as `Deprecated` and the new one gets a new ID.

## 3. Implement (in this order)

1. **DTOs** — `SRC/data/remote/dto/XxxDtos.kt`: `@Serializable data class …Dto`; `@SerialName` for wire names that differ; optional fields nullable with defaults. DTOs never leave `data`.
2. **Retrofit method** — `SRC/data/remote/api/XxxApi.kt`: `suspend fun` returning the DTO (or `Unit`). Unauthenticated endpoints get `@Headers("$NO_AUTH_HEADER: true")` (import `com.noshitechinc.restaurant.core.network.NO_AUTH_HEADER`). Never add `Authorization` headers by hand. Paths are relative, no leading `/`, never a full URL.
3. **Provide the service** (new `XxxApi` only) — add `@Provides @Singleton fun xxxApi(retrofit: Retrofit): XxxApi = retrofit.create(XxxApi::class.java)` to `SRC/di/NetworkModule.kt`.
4. **Domain model** — `SRC/domain/model/`: pure Kotlin, no Android, Retrofit or serialization imports.
5. **Mapper** — `SRC/data/mapper/XxxMappers.kt`: `fun XxxItemDto.toDomain(): XxxItem`.
6. **Repository interface** — `SRC/domain/repository/XxxRepository.kt`: `suspend fun …(): ApiResult<DomainModel>`.
7. **Repository implementation** — `SRC/data/repository/XxxRepositoryImpl.kt`: `class XxxRepositoryImpl @Inject constructor(private val api: XxxApi, private val safeApiCall: SafeApiCall) : XxxRepository`; every call is `safeApiCall { api.foo() }.map { it.toDomain() }`. Never return DTOs, never throw, never catch `CancellationException`.
8. **Binding** — add `@Binds abstract fun xxxRepository(impl: XxxRepositoryImpl): XxxRepository` to the existing `abstract class BindingsModule` in `SRC/di/BindingsModule.kt` (do not create a separate module).
9. **Test** — `TEST/data/repository/XxxRepositoryImplTest.kt` with MockWebServer and the real `SafeApiCall(ErrorMapper(JsonErrorBodyParser(json)))`: success maps to the domain model, request path/method/body are correct, and at least one error status maps to the expected `AppError` (401 → `SessionExpired`, 403 → `Forbidden`, 404 → `NotFound`, 502/503/504 → `ServiceUnavailable`, other 4xx → `Validation`, 5xx → `Server`). ViewModel tests use a `FakeXxxRepository` in `TEST/fakes/` instead.

## Checklist

- [ ] Contract confirmed (or PDR recorded and work stopped).
- [ ] Changes to an existing contract or shared type have an impact analysis approved by a human (not needed for a brand-new endpoint).
- [ ] Registry row added/updated with the right status and consumers.
- [ ] `safeApiCall { }` wraps every call; repository returns `ApiResult<DomainModel>`.
- [ ] Only the repository calls the Retrofit service; ViewModels call the repository.
- [ ] No tokens, `Authorization`/cookie headers or personal data logged; no hardcoded URLs.
- [ ] Unauthenticated endpoints marked with `NO_AUTH_HEADER`.
- [ ] Tests added and passing; no real network.

## Verify

```bash
ANDROID_HOME=$HOME/Android/Sdk ./gradlew ktlintCheck detekt testDevDebugUnitTest
ANDROID_HOME=$HOME/Android/Sdk ./gradlew assembleDevDebug
```

Then run the `doc-sync` skill (registry, PDRs, ticket status, traceability) and the `review-change` skill.
