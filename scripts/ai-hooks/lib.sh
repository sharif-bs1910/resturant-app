#!/usr/bin/env bash
set -uo pipefail

HOOK_SOURCE="cursor"
HOOK_INPUT=""

hook_init() {
  HOOK_SOURCE="${1:-cursor}"
  HOOK_INPUT="$(cat)"
}

running_claude_config_inside_cursor() {
  [[ "$HOOK_SOURCE" == "claude" && -n "${CURSOR_VERSION:-}" ]]
}

project_dir() {
  local dir="${CURSOR_PROJECT_DIR:-${CLAUDE_PROJECT_DIR:-}}"
  if [[ -z "$dir" ]]; then
    dir="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
  fi
  printf '%s\n' "$dir"
}

context_repo_dir() {
  printf '%s/resturant-app-architecture\n' "$(cd "$(project_dir)/.." && pwd)"
}

json_field() {
  jq -r "($1) // empty" <<<"$HOOK_INPUT" 2>/dev/null
}

session_id() {
  local id
  id="$(json_field '.session_id // .conversation_id')"
  printf '%s\n' "${id:-default}"
}

edited_file_path() {
  json_field '.tool_input.file_path // .tool_input.path // .tool_input.target_file // .file_path'
}

session_dir() {
  local dir
  dir="$(project_dir)/.ai/.session"
  mkdir -p "$dir"
  printf '%s\n' "$dir"
}

session_log() {
  printf '%s/%s.log\n' "$(session_dir)" "$(session_id)"
}

record_edit() {
  [[ -n "${1:-}" ]] && printf '%s\n' "$1" >> "$(session_log)"
  return 0
}
