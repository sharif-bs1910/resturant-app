---
name: figma-ui-review
description: >-
  After building or polishing a Jetpack Compose screen/component from Figma,
  runs five review passes against cached Figma MCP contexts under
  .ai/figma-cache/ (structure → color/backgrounds → typography → components →
  polish), fixes gaps between passes, and only force-refreshes live Figma MCP
  when cache is missing. Use when finishing Figma-driven UI, Self
  Service/checkout polish, or when the user asks for a Figma parity /
  smooth-UI review.
---

# Figma UI review (5 passes, cache-first)

Goal: ship Compose UI that matches Figma closely enough to feel smooth — same **positions**, **spacing**, **backgrounds** (page + each card), **colors**, **typography sizes**, **styles**, **shared components**, and **states** — **without re-calling Figma MCP on every check**.

Prefer the local cache from the `figma-cache` hooks. Live MCP is a last resort.

## When to run

Run this skill **after** the UI build/polish is implemented (and ideally after `assembleDevDebug` / design-lint are green), before handing to the human. Do **not** skip passes or collapse them into one glance.

Required: **exactly 5 passes** (see below). Do not stop after three. Add a 6th only if Pass 5 still has open P0/P1 visual gaps and the human wants another round.

## 0. Resolve Figma sources (cache-first)

1. Identify `fileKey` + `nodeId`(s) for the screen(s) just built (from the chat, Figma URL, or sprint notes).
2. List cache entries:

   ```bash
   scripts/ai-hooks/figma-cache-lookup.sh <fileKey> [nodeId]
   ```

3. For each required tool (`get_design_context`, `get_variable_defs`, `get_screenshot` when present), **Read** the cached JSON under `.ai/figma-cache/…` (use `payload` when present).
4. Cache miss or `incomplete: true` in the sibling `*.meta.json`:
   - Call Figma MCP **once** with `"forceRefresh": true` (and a valid `sessionId` from `create_session` or `.ai/figma-cache/<fileKey>/session.json`).
   - Confirm the hook wrote a complete cache file, then continue from cache only.
5. Never burn MCP quota to re-validate the same node mid-pass when a complete cache exists.

Also load project rules that constrain fixes: `.cursor/rules/ui-design-system.mdc`, `.cursor/rules/compose-reuse.mdc` (tokens only — no raw `Color(0x…)`, raw `N.dp`/`N.sp`, or hardcoded user strings).

## Pass loop (repeat 5 times)

For **each** pass:

1. Re-read the relevant cached Figma payload (+ screenshot asset files next to the cache if listed in `*.meta.json` `assets`).
2. Diff against the Compose implementation (and Preview / device screenshot if available).
3. Log findings with severity (table below) and **file:line**.
4. **Fix all P0/P1** from that pass before starting the next pass.
5. Re-run a quick check after fixes:

   ```bash
   scripts/ai-hooks/design-lint-check.sh <touched.kt paths or app/src/main>
   ANDROID_HOME=$HOME/Android/Sdk ./gradlew :app:compileDevDebugKotlin
   ```

Do not start Pass *N+1* until Pass *N* P0/P1 are closed (or explicitly deferred by the human).

### Pass 1 — Structure, layout & positions

Focus: composition, panes, alignment, spacing rhythm, sizing, scroll, **element positions**.

Use [checklist.md](checklist.md#pass-1--structure-layout--positions).

Typical gaps: wrong pane widths, missing dividers, search not fixed-width/right-aligned, chips not equal-width, empty states not centered, categories rail height/scroll, content padding vs Figma `spacing/*`, sheets that stretch with empty bottom space instead of wrapping content.

### Pass 2 — Colors & backgrounds

Focus: **whole-screen background**, **every card/panel fill**, nested surfaces, borders, scrims, selected/inactive fills.

Use [checklist.md](checklist.md#pass-2--colors--backgrounds).

Map Figma variables → `AppTheme.colors` (Background/Default / Oat Milk → `colors.background`, card/white → `colors.card`, Text/Muted → `colors.textMuted`, Surface/Primary → `colors.primary`, elevated cream → `colors.surface` only when Figma uses elevated). Top bar and page shell must match Figma (cream page vs white bar). No accidental `surface` / `surfaceVariant` where Figma shows oat-milk or white.

### Pass 3 — Typography sizes & text styles

Focus: **font family**, **size**, **weight**, **letter-spacing**, color role, truncation.

Use [checklist.md](checklist.md#pass-3--typography-sizes--styles).

Map Figma type (e.g. Inter 22 Bold, JetBrains Mono 12 tracking) → nearest `AppTheme.typography.*` (+ `copy(fontWeight=…)` only when needed). Labels that are mono + caps in Figma must not use body Inter. Prices use price/body styles that never truncate.

### Pass 4 — Common components & reuse

Focus: **design-system / shared feature components** vs one-off duplicates; Figma component mapping.

Use [checklist.md](checklist.md#pass-4--common-components--reuse).

Search `core/designsystem/component/` and existing feature helpers (`PillButton`, `CheckoutChip`, `SheetCard`, `NoshiLockup`, `AppButton`, steppers, etc.) before inventing new UI. Sheets/modals shared across Checkout + Self Service must stay one implementation. Chips, CTAs, fields, and logos should match the same visual treatment as other Figma-aligned screens.

### Pass 5 — Polish, states & adaptive

Focus: empty/filled/loading, long content, interaction, overlays/modals, adaptive, screenshot spot-check.

Use [checklist.md](checklist.md#pass-5--polish-states--adaptive).

Compare against cached screenshot assets when present. Check inactive primary buttons (faded primary, not grey), chip selected/unselected, modal footer wrap, ellipsis vs never-truncate prices.

### Optional Pass 6 — Residual pixel / copy

Only if Pass 5 still has open visual P0/P1. Re-read cache + screenshot assets; fix remaining deltas; stop when P0/P1 are clear.

## Severity

| Level | Meaning |
|---|---|
| P0 | Clear Figma mismatch users will notice (wrong page/card bg, broken layout, missing section, unreadable type). |
| P1 | Spec delta that breaks parity (spacing/type size/chip/cart structure, wrong selected styles, inactive button, wrong shared component). |
| P2 | Minor polish (1–2px-equivalent token rounding, preview-only gap). |
| P3 | Nice-to-have naming/readability; no visual impact. |

## Report template

After all five required passes, report:

```markdown
## Figma UI review

- fileKey / nodeId(s): …
- cache paths used: …
- live MCP refresh: none | <tools refreshed>

### Pass 1 — Structure & positions
- Findings (P0–P3) + fixes applied

### Pass 2 — Colors & backgrounds
- Findings + fixes

### Pass 3 — Typography
- Findings + fixes

### Pass 4 — Components & reuse
- Findings + fixes

### Pass 5 — Polish & states
- Findings + fixes

### Remaining open
- … or “none”

### Verify
- design-lint: clean | <output>
- compileDevDebugKotlin: pass | fail
```

Then ask the human which remaining P2/P3 to take. Do not claim “pixel-perfect” unless Pass 5 has no open P0/P1 against the cache.

## Anti-patterns

- One vague “looks fine” review instead of **five** passes.
- Collapsing color + type + components into a single pass.
- Re-calling `get_design_context` / `get_screenshot` when a complete cache file already exists.
- “Fixing” Figma gaps with raw hex/dp instead of `AppTheme` tokens.
- Skipping page vs card background audit (Pass 2) or shared-component search (Pass 4).
- Skipping empty/disabled/overlay states in Pass 5.
- Marking the change approved — humans approve; this skill only finds and fixes parity gaps.
