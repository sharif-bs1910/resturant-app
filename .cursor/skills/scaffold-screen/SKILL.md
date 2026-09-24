---
name: scaffold-screen
description: Scaffolds a new MVVM feature screen (UiState, ViewModel, Route + Screen, previews, navigation entry, ViewModel test) in the Noshitech Restaurant Android app. Use when adding a new screen or feature under feature/<name>.
---

# Scaffold a feature screen

Creates a screen that follows `.cursor/rules/architecture-mvvm.mdc`, `ui-design-system.mdc` and `testing.mdc`. The reference implementation is `feature/home` (`HomeUiState`, `HomeViewModel`, `HomeScreen`, `HomeViewModelTest`); copy its shape.

Paths: `SRC/` = `app/src/main/java/com/noshitechinc/restaurant/`, `TEST/` = `app/src/test/java/com/noshitechinc/restaurant/`.
Names: `Xxx` = PascalCase feature name (`Orders`), `xxx` = package segment and string prefix (`orders`).

## Before you start

1. Confirm the ticket (`FOUND-NNN` or later module ticket) and the FR acceptance criteria. Ask if anything is ambiguous.
2. Search for reuse: `rg -n "fun [A-Z]" app/src/main/java/com/noshitechinc/restaurant/core/designsystem app/src/main/java/com/noshitechinc/restaurant/core/ui`.
3. If the screen needs data, the repository must exist first (`domain/repository/XxxRepository` + `data/repository/XxxRepositoryImpl`). If it does not, run the `add-api-endpoint` skill first.
4. State the files you will create and get the plan approved.

## Files

Full skeletons for every file are in [skeletons.md](skeletons.md). Copy them, then rename `Xxx`/`xxx`.

1. `SRC/feature/xxx/XxxUiState.kt` — immutable `data class XxxUiState`; one `LoadState<T>` per independently loaded section, default `LoadState.Idle`. No `isSaving` flags.
2. `SRC/feature/xxx/XxxViewModel.kt` — `@HiltViewModel class XxxViewModel @Inject constructor(...) : BaseViewModel()`:
   - `private val _uiState = MutableStateFlow(XxxUiState())`, `val uiState: StateFlow<XxxUiState> = _uiState.asStateFlow()`.
   - Loads set `LoadState.Loading`, call the repository in `viewModelScope.launch`, then store `result.toLoadState()`.
   - Mutations run in `launchSubmit(KEY) { }`; failures go to `presentError(result.error)`; success feedback via `sendEffect(ShowMessage(UiText.Resource(R.string.…), MessageTone.Success))`.
   - Submit keys are `const val` in the companion object so the Route and tests share them.
   - No `Context`, `Resources`, Compose types or `runBlocking`.
3. `SRC/feature/xxx/XxxScreen.kt` — one file with:
   - `XxxRoute(viewModel: XxxViewModel = hiltViewModel(), onNavigate…)`: `val state by viewModel.uiState.collectAsStateWithLifecycle()`, `val submitting by viewModel.submittingKeys.collectAsStateWithLifecycle()`, wrapped in `AppScaffold(effects = viewModel.effects, requiresNetwork = …, onRetryConnection = …) { padding -> }`.
   - Stateless `XxxScreen(state, <flags>, onAction…, modifier: Modifier = Modifier)`: no ViewModel, no flows. Loaded sections render through `LoadStateContent(state.section, onRetry, empty = { EmptyState(...) }) { data -> }`.
   - `private` previews at the bottom of the file.
4. `SRC/navigation/Destinations.kt` — add `@Serializable data object XxxDestination` (use a `data class` only when the screen takes arguments).
5. `SRC/navigation/AppNavHost.kt` — add `composable<XxxDestination> { XxxRoute() }` inside the `NavHost`; navigation from other screens goes through Route callbacks that call `navController.navigate(XxxDestination)`.
6. `app/src/main/res/values/strings.xml` — every user-facing string, prefixed `xxx_`.
7. `TEST/feature/xxx/XxxViewModelTest.kt` — modelled on `TEST/feature/home/HomeViewModelTest.kt`, with `@get:Rule val mainDispatcherRule = MainDispatcherRule()`.
8. `TEST/fakes/FakeXxxRepository.kt` — a fake implementing the domain repository interface (prefer fakes over mocks).

## Screen checklist

- [ ] Every independently loaded section is a `LoadState<T>` in `XxxUiState`, rendered with `LoadStateContent` (Loading, Empty, Error, Content all handled; `ServiceUnavailable` is handled by `LoadStateContent`).
- [ ] Every mutation goes through `launchSubmit(key)`; buttons use `loading = KEY in submitting` (from the collected `submittingKeys`); no separate loading flags; never read `isSubmitting(key)` in composables (not reactive).
- [ ] Mutation failures use `presentError(error)`; load failures stay in the section's `LoadState.Error`.
- [ ] `AppScaffold(requiresNetwork = true, onRetryConnection = …)` when the screen cannot work offline; `false` (default) otherwise.
- [ ] Feature-only effects are `data class`/`data object` implementing `UiEffect`, handled in the Route through `AppScaffold(onEffect = { … })`.
- [ ] Tokens only (`AppTheme.colors/typography/spacing/radius/border/sizes`); no `Color(0x…)`, raw `N.dp`/`N.sp` or string literals outside previews.
- [ ] Shared components reused (`AppButton`, `ListRow`, `EmptyState`, …); new shared components are built with the `build-ui-component` skill.
- [ ] Long content: names use `maxLines` + `TextOverflow.Ellipsis`, prices are never truncated, buttons grow in height.
- [ ] Adaptive: `rememberAdaptiveInfo()` for two-pane decisions, widths capped with `widthIn(max = AppTheme.sizes.contentMaxWidth)` (or `formMaxWidth`); works in tablet portrait and multi-window.
- [ ] Previews: one `@ScreenPreviews` function per state — Loading, Content (with `PreviewData.LONG_NAME` samples), Empty, Error (and any submitting state). `@ScreenPreviews` covers tablet landscape, tablet portrait and font scale 2.0.
- [ ] Destination registered in `Destinations.kt` and `AppNavHost.kt`.

## Test checklist

- [ ] `LoadState` transitions: success → `Content`, empty list → `Empty`, failure → `Error(error)`.
- [ ] Effects with Turbine: `vm.effects.test { vm.onXxx(); assertEquals(expected, awaitItem()) }`.
- [ ] Duplicate-tap protection: second call while the first runs is ignored; `submittingKeys` holds the key, then is empty.
- [ ] Test names are backticked sentences; assertions from `kotlin.test`; no `Thread.sleep` or `runBlocking`.

## Verify

Run from the code repo root; all must pass before handing over:

```bash
scripts/ai-hooks/design-lint-check.sh app/src/main
ANDROID_HOME=$HOME/Android/Sdk ./gradlew ktlintCheck detekt testDevDebugUnitTest
ANDROID_HOME=$HOME/Android/Sdk ./gradlew assembleDevDebug
```

The design lint must print nothing. Then run the `review-change` skill and the `doc-sync` skill (ticket status, FOUND/module traceability).
