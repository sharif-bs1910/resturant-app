# Figma UI review checklists

Use with cached payloads from `.ai/figma-cache/{fileKey}/{nodeId}/`. Prefer `payload` in each `*.json`; read `*.meta.json` for `incomplete`, `assets`, and input fingerprint.

---

## Pass 1 — Structure, layout & positions

- [ ] Screen shell matches Figma frame (top nav + content padding + safe areas).
- [ ] Multi-pane layouts use the same relative widths / fixed rail or cart width as Figma (`AppTheme.sizes.*` composition, not magic layout).
- [ ] Element **positions**: title/body/CTA stacking order, left/right columns, sticky footers, absolute close (X) placement.
- [ ] Vertical/horizontal gaps match Figma spacing tokens (`spacing/12`, `16`, `20`, `24`, `32` → nearest `AppTheme.spacing.*`).
- [ ] Title + search / filter rows: alignment and field width (e.g. search 300×40 when Figma says so).
- [ ] Chip / segmented rows: equal flex vs hug; height ≈ Figma chip height.
- [ ] Side rails (categories, cart): wrap height vs fill; scroll when content overflows; no double scroll traps.
- [ ] Cards/modals **wrap content** — no large empty white region below the last control unless Figma shows a tall fixed frame.
- [ ] Dividers present where Figma shows hairlines (under titles, above totals).
- [ ] Empty regions centered (or positioned) as in Figma; filled lists scroll independently of chrome.
- [ ] Modals/sheets: side-by-side image + form when width allows; image height tracks form; footer actions placement matches.

---

## Pass 2 — Colors & backgrounds

Audit **every** fill against Figma variables / screenshot — page, chrome, and each card.

- [ ] **Whole screen** fill = Figma Background/Default / Oat Milk → `AppTheme.colors.background` (not `card`, not `surface` unless Figma elevated).
- [ ] **Top bar / status strip**: Self Service / kiosk frames use white (`card`) + `border-b` espresso-alpha when Figma shows `bg-white border-b`; do not paint the bar cream unless Figma does.
- [ ] **Each card / panel / sheet / modal** = white → `AppTheme.colors.card` (+ thin espresso-alpha border when Figma shows it). Never use page cream as a card fill.
- [ ] Nested inputs / oat-milk wells → `background`; elevated cream tiles → `surface`; light gray tiles → `surfaceVariant`; bordered recap wells with no fill stay transparent on the white card.
- [ ] Selected category / quiet fills = oat-milk → `background`, not `surface`, unless Figma uses elevated cream.
- [ ] Text colors: default / muted / inverse / accent → `textPrimary` / `textMuted` / `background` on inverse / `primary`.
- [ ] Primary / destructive / success fills map to `primary` / `statusDanger` / `success` (or design-system status tokens).
- [ ] Scrim on overlays ≈ Figma alpha (e.g. espresso @ 0.45), not opaque black.
- [ ] Inactive primary CTA: primary @ ~0.35 + white/cream label (not grey/`disabledContainer`) when Figma shows faded primary.
- [ ] Borders: thin espresso outline on unselected chips; selected chips fill inverse without double border.
- [ ] No leftover debug fills or mismatched preview backgrounds.

---

## Pass 3 — Typography sizes & styles

- [ ] Headlines / titles: Figma px size + weight map to `headline*` / `title*` (e.g. 22 Bold → `titleLarge` + Bold if needed).
- [ ] Body / supporting: 13–14 Regular muted → `bodyMedium` + `textMuted`; 12 Regular → `bodySmall`.
- [ ] Mono caps labels (SIZE, NOTES, EXTRA TOPPINGS): JetBrains Mono + tracking → `labelSmall` (not Inter body).
- [ ] Buttons / chips: Figma Medium/Bold 12–14 → `labelLarge` / `bodySmall`+Medium / `labelMedium` as closest token.
- [ ] Prices / money: tabular price styles (`price*` / body SemiBold); **never** truncate amounts.
- [ ] Letter-spacing and line-height: use token styles; only `copy(...)` for weight/spacing when Figma clearly differs.
- [ ] Long names: `maxLines` + `TextOverflow.Ellipsis`; addresses wrap up to Figma’s line count.
- [ ] All user-facing copy from `strings.xml` — no hardcoded UI strings; wording matches Figma where specified.

---

## Pass 4 — Common components & reuse

- [ ] Searched `core/designsystem/component/` and existing feature chrome before adding one-off composables (`compose-reuse` rule).
- [ ] Logo uses `NoshiLockup` / design-system logo — not placeholder shapes.
- [ ] Primary/secondary CTAs reuse `AppButton` or shared `PillButton` with the same height/padding as other Figma screens.
- [ ] Chips reuse shared chip helpers (`CheckoutChip` / design-system tags) with sheet vs nav variants — not a third private chip.
- [ ] Sheets/modals: shared `SheetCard` / `ModifierSheet` / overlay host — Checkout and Self Service do not fork the same Figma modal.
- [ ] Steppers, note fields, money rows, dividers: one shared implementation per pattern.
- [ ] Interaction visuals (pressed/focused) follow design-system interaction helpers where the component supports them.
- [ ] New shared pieces extracted when the same Figma component appears on 2+ screens in this change.

---

## Pass 5 — Polish, states & adaptive

- [ ] Empty / filled / loading (if any) all match Figma variants available in cache.
- [ ] Order-type / meal chips: selected + unselected labels and counts match copy (`strings.xml`).
- [ ] Overlay/modal: blurb, chips, notes, stepper + add CTA match Figma customize sheet; content-wrapped height.
- [ ] Disabled/inactive and enabled CTAs both checked.
- [ ] Tablet landscape + portrait: no overflow; widths capped with `AppTheme.sizes.*`.
- [ ] Cached screenshot assets (if any) spot-checked against Preview or device capture for remaining deltas.
- [ ] No leftover debug borders / wrong scrim alpha on sheets.
- [ ] Pass 2–4 regressions re-checked after polish edits (bg, type, shared components still correct).

---

## Cache tools (quick map)

| Need | Prefer cache of |
|---|---|
| Structure, copy, CSS-like specs, positions | `get_design_context` |
| Color/type variables | `get_variable_defs` |
| Visual reference (bg, card fills, hierarchy) | `get_screenshot` (+ `*.asset*.png` beside JSON) |
| Frame tree | `get_metadata` |

### Quick Figma → token map (colors)

| Figma | Compose |
|---|---|
| Background/Default, Oat Milk `#FBF6EE` | `AppTheme.colors.background` |
| Background card / white | `AppTheme.colors.card` |
| Elevated cream `#F3ECDD` | `AppTheme.colors.surface` |
| Text/Default espresso | `AppTheme.colors.textPrimary` |
| Text/Muted | `AppTheme.colors.textMuted` |
| Surface/Primary coral | `AppTheme.colors.primary` |

Refresh only with `"forceRefresh": true` when meta says `incomplete: true` or the file is missing.
