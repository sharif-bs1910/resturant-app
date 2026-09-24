---
name: impact-analysis
description: Writes a blast-radius impact analysis (IA-YYYY-MM-DD-<topic>.md) from the context-repo template, with consumer searches, risk level, ordered plan and rollback, then stops for human approval. Use before changing API contracts or DTOs, ApiResult/AppError/UiText/LoadState/UiEffect/BaseViewModel, design tokens, public parameters of shared components, Gradle flavors/env config/version catalog, or the Android manifest.
---

# Impact analysis

Playbook rule: *schema / API / shared-interface changes get a blast-radius analysis and an approved plan before any edit.* Follows `.cursor/rules/impact-analysis.mdc`.

`CTX/` = `../resturant-app-architecture/`.

## Triggers

Run this skill before editing any of:

| Trigger | Where |
|---|---|
| Existing API contracts or DTOs (path, payload, status codes, auth) | `data/remote/api/`, `data/remote/dto/` — a brand-new endpoint is additive and needs only the registry row and doc sync |
| Shared MVVM and result types | `ApiResult`, `AppError`, `UiText` (`core/common`); `LoadState`, `UiEffect`, `BaseViewModel` (`core/ui`) |
| Design tokens | `core/designsystem/theme/` |
| Public parameters of shared components | `core/designsystem/**`, `core/ui/**` |
| Build configuration | Gradle flavors, `config/env/*.properties`, `gradle/libs.versions.toml` |
| Manifest | `app/src/main/AndroidManifest.xml` |

If unsure whether a change is covered, treat it as covered.

## Steps

1. **Create the file.** Copy `CTX/templates/IMPACT-ANALYSIS-template.md` to `CTX/docs/06-development/impact-analyses/IA-YYYY-MM-DD-<topic>.md` (today's date, short kebab-case topic; add `-2`, `-3` if the name exists). Never rename or delete an existing analysis.
2. **Change summary.** What changes and why, with the ticket and FR IDs.
3. **Trigger.** Name the trigger from the table above.
4. **Find consumers.** Run searches from the code repo root and paste each command with its result (match count and file list) into the table. Start with:

   ```bash
   rg -n "<Symbol>" app/src
   rg -n "<Symbol>\(" app/src/main app/src/test app/src/androidTest
   rg -n "<Symbol>" ../resturant-app-architecture/docs
   ```

   Add the searches that fit the trigger:

   | Change | Extra searches |
   |---|---|
   | Sealed type (`AppError`, `LoadState`, `UiText`, `ApiResult`) | `rg -n "when \(" app/src` near usages, `rg -n "is AppError\." app/src`, `rg -n "AppError\.<Case>" app/src` — every exhaustive `when` must be updated |
   | `BaseViewModel` / `UiEffect` | `rg -n ": BaseViewModel\(\)" app/src`, `rg -n -e launchSubmit -e presentError -e sendEffect -e submittingKeys app/src`, `rg -n "UiEffect" app/src` |
   | DTO or endpoint | `rg -n "<Name>Dto" app/src`, `rg -n "<Name>Api" app/src`, `rg -n "api:<domain>:<slug>" ../resturant-app-architecture` |
   | Token | `rg -n "AppTheme\.<group>\.<name>\b" app/src` |
   | Component parameter | `rg -n "<Component>\(" app/src` (include previews and androidTest) |
   | Build config | `rg -n "<flavor or key>" app/build.gradle.kts config/env gradle/libs.versions.toml`, `rg -n "BuildConfig\.<KEY>" app/src` |
   | Manifest | `rg -n "<permission, activity or property>" app/src` |

5. **Blast radius.** List affected files, screens, tests, previews, docs and endpoints, direct and indirect (for example a new `AppError` case reaches `AppErrorText.kt`, `ErrorState` previews, `ErrorMapper` and `BaseViewModel.presentError`).
6. **Risk.** One level with the reason:
   - **Low** — additive, no existing call site changes, or 1–2 call sites fully covered by tests.
   - **Medium** — several call sites or previews change, behaviour is preserved, tests cover the main paths.
   - **High** — behaviour or contract changes for existing callers, exhaustive `when`s break, build/flavor/manifest changes, or weak test coverage.
7. **Plan**, ordered `API → clients → tests → docs`:
   1. API — the contract, DTO, shared type, token or component signature.
   2. Clients — repositories, ViewModels, screens, components and previews that use it.
   3. Tests — tests to add or update and the command that proves them (`ANDROID_HOME=$HOME/Android/Sdk ./gradlew ktlintCheck detekt testDevDebugUnitTest`, plus `scripts/ai-hooks/design-lint-check.sh app/src/main` for UI changes).
   4. Docs — `API-REGISTRY.md`, ADRs, `PENDING-DECISIONS.md`, `SPECIFICATION.md`, `docs/05-breakdown/modules/FOUND.md`, `docs/05-breakdown/sprints/sprint-0.md`, `PROJECT-INDEX.md` as applicable (see the `doc-sync` skill).
8. **Rollback.** How to undo safely: files to revert, config values to restore, whether data, stored tokens or the backend need anything, and how to verify the rollback (same commands as the plan).
9. **Approval.** Leave the Approval table empty; only a human fills it in.

## Stop

Show the human the path of the analysis and a three-line summary (trigger, risk, plan), then **stop and wait**. Do not edit any file the analysis covers until a human has filled in the Approval section. The AI never approves its own analysis.

After approval, implement only what the analysis covers. If the scope grows (new consumers, extra files, changed contract), update the analysis and ask for approval again.
