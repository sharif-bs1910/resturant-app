#!/usr/bin/env bash
source "$(dirname "$0")/lib.sh"
hook_init "$@"

SECRET_PATH_RE='(^|/)(local\.properties|google-services\.json|[^/]*\.jks|[^/]*\.keystore|\.env(\.[^/]*)?|[^/]*\.env|secrets\.[^/]+|[^/]*service-account[^/]*\.json)$'
SAFE_SUFFIX_RE='\.(example|sample|template)$'
SECRET_IN_CMD_RE='(local\.properties|google-services\.json|\.jks|\.keystore|service-account[^[:space:]]*\.json)'
FORCE_PUSH_RE='git[[:space:]]+push.*(--force|--force-with-lease|[[:space:]]-f([[:space:]]|$))'
NO_VERIFY_RE='--no-verify'
RESET_HARD_RE='git[[:space:]]+reset[[:space:]]+--hard'
CLEAN_RE='git[[:space:]]+clean[[:space:]]+-[a-zA-Z]*f'
RM_RECURSIVE_RE='(^|[;&|[:space:]])rm[[:space:]]+-[a-zA-Z]*[rR]'
RM_SAFE_TARGET_RE='(^|/)(build|\.gradle|\.kotlin|\.ai/\.session)(/.*)?$'

is_secret_path() {
  local p="${1:-}"
  [[ -z "$p" ]] && return 1
  [[ "$p" =~ $SAFE_SUFFIX_RE ]] && return 1
  [[ "$p" =~ $SECRET_PATH_RE ]]
}

dangerous_reason() {
  local cmd="$1"
  if [[ "$cmd" =~ $FORCE_PUSH_RE ]]; then echo "force push"; return; fi
  if [[ "$cmd" =~ $NO_VERIFY_RE ]]; then echo "--no-verify skips hooks"; return; fi
  if [[ "$cmd" =~ $RESET_HARD_RE ]]; then echo "git reset --hard discards work"; return; fi
  if [[ "$cmd" =~ $CLEAN_RE ]]; then echo "git clean -f deletes untracked files"; return; fi
  if [[ "$cmd" =~ $RM_RECURSIVE_RE ]]; then
    local args target
    args="${cmd#*rm }"
    args="${args%%[;&|]*}"
    set -f
    for target in $args; do
      [[ "$target" == -* ]] && continue
      if [[ ! "$target" =~ $RM_SAFE_TARGET_RE ]]; then
        set +f
        echo "recursive delete outside build output ($target)"
        return
      fi
    done
    set +f
  fi
  if [[ "$cmd" =~ $SECRET_IN_CMD_RE && ! "$cmd" =~ \.example ]]; then echo "command touches a secret file"; return; fi
}

event="$(json_field '.hook_event_name')"
command="$(json_field '.command // .tool_input.command')"
path="$(edited_file_path)"

deny() {
  local msg="Blocked by AI guard: $1. Ask the user to do this manually if it is really needed."
  if [[ "$HOOK_SOURCE" == "claude" ]]; then
    jq -n --arg r "$msg" '{hookSpecificOutput: {hookEventName: "PreToolUse", permissionDecision: "deny", permissionDecisionReason: $r}}'
  elif [[ "$event" == "beforeReadFile" ]]; then
    jq -n --arg r "$msg" '{permission: "deny", user_message: $r}'
  else
    jq -n --arg r "$msg" '{permission: "deny", user_message: $r, agent_message: $r}'
  fi
  exit 0
}

if [[ -n "$command" ]]; then
  reason="$(dangerous_reason "$command")"
  [[ -n "$reason" ]] && deny "$reason"
fi
if is_secret_path "$path"; then
  deny "$(basename "$path") is a secret file"
fi

[[ "$HOOK_SOURCE" == "cursor" ]] && echo '{"permission":"allow"}'
exit 0
