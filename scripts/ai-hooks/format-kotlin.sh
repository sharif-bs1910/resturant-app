#!/usr/bin/env bash
source "$(dirname "$0")/lib.sh"
hook_init "$@"
running_claude_config_inside_cursor && exit 0

file="$(edited_file_path)"
record_edit "$file"
[[ "$file" =~ \.kts?$ && -f "$file" ]] || exit 0

ktlint_bin="$("$(dirname "$0")/ensure-ktlint.sh" 2>/dev/null)" || exit 0
"$ktlint_bin" --format "$file" >/dev/null 2>&1 || true
exit 0
