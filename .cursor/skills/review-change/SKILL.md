---
name: review-change
description: Runs the AI self-review checklist on a finished change in the Noshitech Restaurant Android app (acceptance criteria, MVVM, tokens/strings/previews, states, duplicate-submit protection, long content, tests, doc sync) and reports findings as P0–P3 before handing over to the human. Use after implementing a change and before asking for human review.
---

# Review a change

The AI self-review step of the plan-before-act flow (`../resturant-app-architecture/AGENTS.md`). It finds problems; it does not approve the change. The human reviews after this.

## 1. Collect the scope

1. List the changed files and the ticket / FR IDs the change claims to satisfy.
2. Re-read the FR acceptance criteria in `../resturant-app-architecture/docs/02-specification/SPECIFICATION.md` and, if one exists, the approved impact analysis in `../resturant-app-architecture/docs/06-development/impact-analyses/`.

## 2. Run the checks

Run from the code repo root and keep the output for the report:

```bash
scripts/ai-hooks/design-lint-check.sh app/src/main
ANDROID_HOME=$HOME/Android/Sdk ./gradlew ktlintCheck detekt testDevDebugUnitTest
ANDROID_HOME=$HOME/Android/Sdk ./gradlew assembleDevDebug
```

The design lint must print nothing. For UI-test changes also run `ANDROID_HOME=$HOME/Android/Sdk ./gradlew assembleDevDebugAndroidTest` (and `connectedDevDebugAndroidTest` when a device is available).

## 3. Checklist

**Spec and scope**
- [ ] Every acceptance criterion of the FR is met; nothing outside the approved plan or impact analysis was changed.
- [ ] Impact-analysis triggers touched only with an approved analysis.

**Architecture (MVVM)**
- [ ] Packages and dependency direction respected (`feature` → `domain` ← `data`; `domain` pure Kotlin; `designsystem` imports no `feature`/`data`/`domain`).
- [ ] `@HiltViewModel … : BaseViewModel()`; `uiState: StateFlow<XxxUiState>` with immutable `copy` updates; no `Context`, `Resources`, Compose types or `runBlocking` in ViewModels.
- [ ] `XxxRoute` / stateless `XxxScreen` split; Route collects with `collectAsStateWithLifecycle()` and wraps in `AppScaffold`.
- [ ] Repositories wrap calls in `safeApiCall { }`, return `ApiResult<DomainModel>`, never expose DTOs; only repositories call Retrofit.

**UI, tokens, strings, previews**
- [ ] `AppTheme.*` tokens only; no `Color(0x…)`, raw `N.dp`/`N.sp` or user-facing string literals outside previews.
- [ ] Every new user-facing string is in `app/src/main/res/values/strings.xml`.
- [ ] Shared components reused; new public composables have `private` previews at the bottom (`@ComponentPreviews` / `@ScreenPreviews` + `PreviewSurface`), covering every variant and state, pressed/focused via `ForcedInteraction`.

**States**
- [ ] Each loaded section has a `LoadState` and renders Loading, Empty, Error (including `ServiceUnavailable`) and Content, usually via `LoadStateContent`.
- [ ] Offline handled: `AppScaffold(requiresNetwork = true, onRetryConnection = …)` for screens that cannot work offline.
- [ ] Mutation failures use `presentError`; load failures stay in `LoadState.Error`.

**Duplicate submit**
- [ ] Every mutation runs through `launchSubmit(key)`; buttons show `loading = key in submitting` (collected `submittingKeys`); no separate `isSaving` flags; never read `isSubmitting(key)` in composables (not reactive).

**Long content and adaptive**
- [ ] Names ellipsize, prices never truncate, addresses wrap up to 3 lines, buttons/chips grow in height.
- [ ] Checked at font scale 2.0 (previews) and in tablet landscape and tablet portrait; widths capped with `AppTheme.sizes.*MaxWidth`; two-pane only via `rememberAdaptiveInfo()`.

**Security and logging**
- [ ] No tokens, `Authorization`/cookie headers or personal data logged; no secrets read or committed; no hardcoded URLs.

**Tests**
- [ ] New ViewModels have tests modelled on `HomeViewModelTest` (`MainDispatcherRule`, `LoadState` transitions, effects via Turbine, duplicate-tap protection).
- [ ] New repositories/endpoints have MockWebServer or fake-based tests; components with behaviour have Compose UI tests found by test tag.
- [ ] All commands in step 2 pass.

**Docs**
- [ ] Context-repo docs updated in the same change (run the `doc-sync` skill): registry, ADR/PDR, sprint board status, FOUND traceability, `PROJECT-INDEX.md`.

## 4. Report

List findings by severity, each with file:line, what is wrong and the proposed fix:

| Level | Meaning |
|---|---|
| P0 | Blocks merge: build or tests fail, acceptance criterion not met, security/secret issue, crash, data loss. |
| P1 | Must fix before handover: rule violation (layering, tokens, strings, `launchSubmit`, `safeApiCall`), missing state, missing tests or docs. |
| P2 | Should fix: missing preview variant, weak test, long-content or adaptive gap. |
| P3 | Nice to have: naming, readability, small cleanup. |

Include the command results from step 2 (pass/fail, design lint output). If there are no findings, say so and state what was checked.

Then ask the human which findings to fix. Do not fix anything, and do not mark the change as approved, until the human answers.
