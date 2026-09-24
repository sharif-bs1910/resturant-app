# AGENTS.md — Noshitech Restaurant (code repo)

Android app foundations for Noshitech Restaurant. This repo holds the application code; the project's shared memory (phase docs, specification, ADRs, PDRs, API registry, sprint board) lives in the sibling context repo `../resturant-app-architecture`. Business domain details are not defined yet — do not invent them. These rules apply to every AI tool (Cursor, Claude Code) and every human working with one.

## Load context first

Before any work, read the four Tier-1 files in this order:

1. `../resturant-app-architecture/AGENTS.md`
2. `../resturant-app-architecture/PROJECT-INDEX.md`
3. `../resturant-app-architecture/.ai/context/project-overview.md`
4. `../resturant-app-architecture/.ai/AI-ASSISTANT-RULES.md`

Then summarize the current phase, the task (with its ticket ID) and the constraints that apply, and wait for the human to confirm the summary before editing any file. One task per chat. If the summary is wrong, stop, reload and summarize again.

## Project facts

- Package and app ID: `com.noshitechinc.restaurant`; display name "Noshitech Restaurant".
- Single `:app` module with strict package layering (see the package map).
- Flavors (dimension `environment`): `dev`, `staging`, `prod`; build types `debug`, `release`.
- JDK 21 is selected by the Gradle daemon toolchain (`gradle/gradle-daemon-jvm.properties`). Never hardcode `JAVA_HOME` or a JDK path.
- Android SDK: run Gradle as `ANDROID_HOME=$HOME/Android/Sdk ./gradlew …`. Never read or write `local.properties`.
- English only, light mode only; every user-facing string lives in `app/src/main/res/values/strings.xml`.
- Non-secret environment values live in `config/env/{dev,staging,prod}.properties`; secrets never enter the repo.

## Commands

- Build: `./gradlew assembleDevDebug`
- Quality and unit tests: `./gradlew ktlintCheck detekt testDevDebugUnitTest`
- Instrumented tests: `./gradlew connectedDevDebugAndroidTest`
- Design lint: `scripts/ai-hooks/design-lint-check.sh app/src/main` (prints nothing when clean)
- Hook tests: `scripts/ai-hooks/tests/run-tests.sh`

## Package map

Packages under `com.noshitechinc.restaurant`:

```
app/                RestaurantApplication, MainActivity
navigation/         Destinations (@Serializable), AppNavHost
feature/<name>/     XxxRoute + XxxScreen, XxxViewModel, XxxUiState
domain/             model/, repository/ (interfaces), usecase/
data/               remote/api/, remote/dto/, mapper/, repository/ (…RepositoryImpl)
core/common/        ApiResult, AppError, UiText, MarketConfig, formatters/validators
core/ui/            BaseViewModel, LoadState, UiEffect, AppScaffold, ObserveEffects, error/, state/ (error-aware states)
core/network/       NetworkModule, interceptors, TokenAuthenticator, SafeApiCall, ErrorMapper, ErrorBodyParser, NetworkMonitor
core/auth/          AuthProvider, TokenStore, KeystoreTokenStore, SessionManager
core/logging/       Timber trees, CrashReporter, CrashlyticsCrashReporter, NoOpCrashReporter
core/designsystem/  theme/ (tokens, AppTheme), interaction/, component/<group>/, state/ (generic states), preview/
core/adaptive/      AdaptiveInfo, WidthClass, OrientationPolicy, adaptive containers
di/                 Hilt modules
```

Dependency direction: `feature` → `domain` ← `data`; `core/*` is usable by all; `domain` is pure Kotlin (no Android imports); `designsystem` never depends on `feature`, `data` or `domain`. Error-aware UI (`ErrorState`, `ServiceUnavailableState`, `OfflineBlockingState`, `LoadStateContent`, `AppScaffold`) lives in `core/ui` because `designsystem` must not depend on `AppError` mapping.

## Conventions

- [Session bootstrap](.cursor/rules/session-bootstrap.mdc): load the Tier-1 files, summarize, confirm, ask before implementing.
- [Impact analysis](.cursor/rules/impact-analysis.mdc): contracts, shared types, tokens, shared component APIs and build config need an approved impact analysis before any edit.
- [Architecture (MVVM)](.cursor/rules/architecture-mvvm.mdc): layering, `XxxRoute`/`XxxScreen` split, `XxxUiState` + `LoadState`, effects and `launchSubmit` through `BaseViewModel`.
- [UI design system](.cursor/rules/ui-design-system.mdc): `AppTheme.*` tokens only, reuse components, previews for every variant and state, long-content and adaptive rules.
- [Networking](.cursor/rules/networking.mdc): `safeApiCall { }` in repositories, DTO → domain mappers, `NO_AUTH_HEADER`, no token logging, API registry.
- [Testing](.cursor/rules/testing.mdc): JUnit4 + MockK + Turbine, `MainDispatcherRule`, fakes, MockWebServer, Compose tests by test tag.
- Previews go at the bottom of the file.
- No comments that narrate code; comments only for non-obvious constraints.

## Skills

Canonical skills live in `.cursor/skills/` (Claude Code reads them through the `.claude/skills` symlink).

- `scaffold-screen` — create a feature screen: UiState, ViewModel, Route + Screen, previews, navigation entry, ViewModel test.
- `build-ui-component` — build a shared design-system component with tokens, interaction visuals and a full preview matrix.
- `add-api-endpoint` — add an endpoint end to end: registry row, DTOs, Retrofit method, mapper, repository, binding, test.
- `impact-analysis` — write `IA-YYYY-MM-DD-<topic>.md` for a risky change and stop for approval.
- `review-change` — AI self-review checklist before handing a change to the human.
- `doc-sync` — update the context-repo docs affected by a change.

## Doc sync

Update the context-repo docs in the same change as the code, following [`../resturant-app-architecture/AGENTS.md#doc-sync-same-change`](../resturant-app-architecture/AGENTS.md#doc-sync-same-change).
