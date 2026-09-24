#!/usr/bin/env bash
source "$(dirname "$0")/lib.sh"
hook_init "$@"
running_claude_config_inside_cursor && exit 0

log="$(session_log)"
marker="$log.reminded"
[[ -f "$log" && ! -f "$marker" ]] || exit 0

if [[ "$HOOK_SOURCE" == "claude" ]]; then
  [[ "$(json_field '.stop_hook_active')" == "true" ]] && exit 0
else
  [[ "$(json_field '.status')" == "completed" ]] || exit 0
  [[ "$(json_field '.loop_count')" == "0" ]] || exit 0
fi

ctx="$(context_repo_dir)"
grep -qF "$ctx/" "$log" && exit 0

CONTRACT_RE='/(data/remote|core/network|core/auth|core/designsystem/theme|config/env)/|/app/build\.gradle\.kts$|/gradle/libs\.versions\.toml$|/AndroidManifest\.xml$|/core/common/(ApiResult|AppError)\.kt$|/core/ui/BaseViewModel\.kt$'
changed="$(sort -u "$log" | grep -E "$CONTRACT_RE")"
[[ -z "$changed" ]] && exit 0
touch "$marker"

message="Doc-sync check (AI-DLC spine rule 5): this session changed contract files:
$changed
Update the context repo in the same change: $ctx/docs/03-context/API-REGISTRY.md for endpoints, the matching ADR in $ctx/docs/03-context/adr/, $ctx/PROJECT-INDEX.md and $ctx/docs/05-breakdown/sprints/sprint-0.md. If no doc change is needed, say why in one sentence."

if [[ "$HOOK_SOURCE" == "claude" ]]; then
  jq -n --arg m "$message" '{decision: "block", reason: $m}'
else
  jq -n --arg m "$message" '{followup_message: $m}'
fi
