#!/usr/bin/env bash
source "$(dirname "$0")/lib.sh"
hook_init "$@"
running_claude_config_inside_cursor && exit 0

find "$(session_dir)" -type f -mtime +7 -delete 2>/dev/null

ctx="$(context_repo_dir)"
if [[ -d "$ctx" ]]; then
  status="Context repo: $ctx"
else
  status="WARNING: context repo not found at $ctx. Ask the user where it is before doing any work."
fi

message="AI-DLC session bootstrap — Noshitech Restaurant (Android code repo).
$status
Before any work, read these Tier-1 hot files in order:
1. $ctx/AGENTS.md
2. $ctx/PROJECT-INDEX.md
3. $ctx/.ai/context/project-overview.md
4. $ctx/.ai/AI-ASSISTANT-RULES.md
Then read ./AGENTS.md in this repo. Summarize the current phase, the task and the constraints that apply, and get the user's confirmation before editing code. One task per chat."

if [[ "$HOOK_SOURCE" == "claude" ]]; then
  jq -n --arg m "$message" '{hookSpecificOutput: {hookEventName: "SessionStart", additionalContext: $m}}'
else
  jq -n --arg m "$message" '{additional_context: $m}'
fi
