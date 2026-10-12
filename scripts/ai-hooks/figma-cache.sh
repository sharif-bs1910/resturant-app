#!/usr/bin/env bash
# Figma MCP local cache hook — see figma-cache.py
# Usage: figma-cache.sh <before|after> [cursor|claude]
set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=lib.sh
source "$SCRIPT_DIR/lib.sh"

MODE="${1:-}"
case "$MODE" in
  before | after) shift ;;
  *) MODE="" ;;
esac

hook_init "$@"
running_claude_config_inside_cursor && exit 0

if ! command -v python3 >/dev/null 2>&1; then
  # Fail open: never block Figma MCP if python is missing
  if [[ "${MODE:-}" == "before" || "$(json_field '.hook_event_name')" == *efore* ]]; then
    printf '%s\n' '{"permission":"allow"}'
  fi
  exit 0
fi

args=()
[[ -n "$MODE" ]] && args+=("$MODE")
printf '%s' "$HOOK_INPUT" | python3 "$SCRIPT_DIR/figma-cache.py" "${args[@]}"
exit 0
