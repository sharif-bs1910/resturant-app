#!/usr/bin/env bash
source "$(dirname "$0")/lib.sh"
hook_init "$@"
running_claude_config_inside_cursor && exit 0

file="$(edited_file_path)"
[[ "$file" =~ \.kt$ && -f "$file" ]] || exit 0
report="$("$(dirname "$0")/design-lint-check.sh" "$file")"
[[ -z "$report" ]] && exit 0

message="Design lint (ui-design-system rule) found issues. Fix them before finishing:
$report"
if [[ "$HOOK_SOURCE" == "claude" ]]; then
  jq -n --arg m "$message" '{hookSpecificOutput: {hookEventName: "PostToolUse", additionalContext: $m}}'
else
  jq -n --arg m "$message" '{additional_context: $m}'
fi
