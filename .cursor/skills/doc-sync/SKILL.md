---
name: doc-sync
description: Updates the context-repo docs (API registry, ADRs, PDRs, specification, sprint board, FOUND traceability, PROJECT-INDEX) affected by a code or decision change in the Noshitech Restaurant project, in the same change. Use when finishing any change, or when a decision, endpoint, requirement or ticket status changes.
---

# Doc sync

Docs are updated in the same change as the code or decision they describe, never in a later one. The source of truth for the rule is [`../resturant-app-architecture/AGENTS.md#doc-sync-same-change`](../../../../resturant-app-architecture/AGENTS.md#doc-sync-same-change).

`CTX/` = `../resturant-app-architecture/` (sibling of the code repo).

## Doc per change type

Copied from the context repo `AGENTS.md`; if the two differ, the context repo wins and this table must be updated.

| Change type | Doc to update |
|---|---|
| New or changed endpoint | `CTX/docs/03-context/API-REGISTRY.md` |
| Architecture or technology decision | New ADR in `CTX/docs/03-context/adr/` |
| Decision still open | `CTX/docs/03-context/PENDING-DECISIONS.md` |
| Ticket status | `CTX/docs/05-breakdown/sprints/sprint-0.md` |
| Phase or project status change | `CTX/PROJECT-INDEX.md` |
| New requirement | `CTX/docs/02-specification/SPECIFICATION.md` with a new FR ID |

## Steps

1. **List what changed** — files, endpoints, decisions, requirements and tickets touched. Map each to a row of the table.
2. **Endpoints** — add or update the row in `API-REGISTRY.md` (`api:{domain}:{slug}`, method, path, auth, request, response, errors, status `Placeholder (PDR-NNN)` / `Draft` / `Live` / `Deprecated`, consumers). Never renumber or delete a row.
3. **Decisions**
   - Decided architecture or technology choice → new `ADR-NNN-<slug>.md` in `adr/` from `CTX/templates/ADR-template.md` (next free number), and add it to the Decisions table in `PROJECT-INDEX.md`.
   - Still open → new `PDR-NNN` row in `PENDING-DECISIONS.md` (question, safe default, owner role, needed by) and in the Open decisions table of `PROJECT-INDEX.md`.
   - PDR decided → move it to the Outcomes section of `PENDING-DECISIONS.md` with date, decision and ADR link; update `PROJECT-INDEX.md`.
4. **Requirements** — a new requirement gets the next free `FR-FOUND-NNN` (or module prefix) in `SPECIFICATION.md`; never reuse an ID.
5. **Ticket status** — update the Status column (`To do` → `In progress` → `Done`) of the ticket row in `CTX/docs/05-breakdown/sprints/sprint-0.md` (or the current sprint file).
6. **Traceability** — in `CTX/docs/05-breakdown/modules/FOUND.md` (or the module file), confirm or correct the FR row: main code paths (`SRC/…`, `TEST/…`, `ATEST/…`) and test names, so they match the real files.
7. **Test cases** — when tests covering a `FOUND-QA-NNN` case are added or changed, update `CTX/docs/06-development/TEST-CONTEXT.md`.
8. **Project index** — update **Last updated** in the Current state section of `CTX/PROJECT-INDEX.md` to today's date whenever any context doc changes; update Phase / Sprint / Status if they changed.
9. **Check links and IDs** — relative links resolve; no ID renumbered or reused:

   ```bash
   rg -n "<new or changed ID>" ../resturant-app-architecture
   ```

## Report

List each context-repo file you changed with one line on why. If a change type needs no doc update, say which and why in one sentence (for example "internal refactor, no endpoint, decision, requirement or status change").
