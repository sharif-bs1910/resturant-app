#!/usr/bin/env bash
set -uo pipefail
HOOKS="$(cd "$(dirname "$0")/.." && pwd)"
unset CURSOR_VERSION
fail=0

pass() { echo "PASS $1"; }
bad() { echo "FAIL $1: $2"; fail=1; }
assert_contains() { [[ "$2" == *"$3"* ]] && pass "$1" || bad "$1" "expected '$3' in: $2"; }
assert_not_contains() { [[ "$2" != *"$3"* ]] && pass "$1" || bad "$1" "did not expect '$3' in: $2"; }
assert_empty() { [[ -z "$2" ]] && pass "$1" || bad "$1" "expected empty, got: $2"; }

WORK="$(mktemp -d)"
mkdir -p "$WORK/resturant-app" "$WORK/resturant-app-architecture"
export CURSOR_PROJECT_DIR="$WORK/resturant-app"
trap 'rm -rf "$WORK"' EXIT

run() { local script="$1" source="$2" json="$3"; printf '%s' "$json" | "$HOOKS/$script" "$source"; }

out="$(run guard-secrets.sh cursor '{"hook_event_name":"beforeReadFile","file_path":"/x/resturant-app/local.properties"}')"
assert_contains "cursor read local.properties denied" "$out" '"permission": "deny"'

out="$(run guard-secrets.sh cursor '{"hook_event_name":"beforeReadFile","file_path":"/x/resturant-app/local.properties.example"}')"
assert_contains "cursor read local.properties.example allowed" "$out" '"permission":"allow"'

out="$(run guard-secrets.sh cursor '{"hook_event_name":"beforeReadFile","file_path":"/x/app/src/dev/google-services.json"}')"
assert_contains "cursor read google-services.json denied" "$out" '"permission": "deny"'

out="$(run guard-secrets.sh cursor '{"hook_event_name":"beforeShellExecution","command":"git push --force origin main"}')"
assert_contains "force push denied" "$out" '"permission": "deny"'

out="$(run guard-secrets.sh cursor '{"hook_event_name":"beforeShellExecution","command":"./gradlew assembleDevDebug"}')"
assert_contains "gradle allowed" "$out" '"permission":"allow"'

out="$(run guard-secrets.sh cursor '{"hook_event_name":"beforeShellExecution","command":"rm -rf app/build && ./gradlew clean"}')"
assert_contains "rm -rf build allowed" "$out" '"permission":"allow"'

out="$(run guard-secrets.sh cursor '{"hook_event_name":"beforeShellExecution","command":"rm -rf app/src"}')"
assert_contains "rm -rf src denied" "$out" '"permission": "deny"'

out="$(run guard-secrets.sh cursor '{"hook_event_name":"beforeShellExecution","command":"cat local.properties"}')"
assert_contains "cat secret denied" "$out" '"permission": "deny"'

out="$(run guard-secrets.sh claude '{"hook_event_name":"PreToolUse","tool_name":"Read","tool_input":{"file_path":"/x/app/src/prod/google-services.json"}}')"
assert_contains "claude read secret denied" "$out" '"permissionDecision": "deny"'

out="$(run guard-secrets.sh claude '{"hook_event_name":"PreToolUse","tool_name":"Bash","tool_input":{"command":"git commit --no-verify -m x"}}')"
assert_contains "claude no-verify denied" "$out" '"permissionDecision": "deny"'

out="$(run guard-secrets.sh claude '{"hook_event_name":"PreToolUse","tool_name":"Bash","tool_input":{"command":"./gradlew test"}}')"
assert_empty "claude safe bash no output" "$out"

out="$(run session-start.sh cursor '{"hook_event_name":"sessionStart","session_id":"s1"}')"
assert_contains "cursor session start context" "$out" 'PROJECT-INDEX.md'
assert_contains "cursor session start key" "$out" '"additional_context"'
assert_not_contains "context repo found" "$out" 'WARNING'

out="$(run session-start.sh claude '{"hook_event_name":"SessionStart","session_id":"s1"}')"
assert_contains "claude session start" "$out" '"hookEventName": "SessionStart"'

out="$(CURSOR_VERSION=9.9 run session-start.sh claude '{"hook_event_name":"sessionStart","session_id":"s1"}')"
assert_empty "claude config inside cursor skipped" "$out"

UI="$WORK/resturant-app/app/src/main/java/com/noshitechinc/restaurant/core/designsystem/component/demo"
mkdir -p "$UI"
cat > "$UI/Bad.kt" <<'EOF'
package demo

@Composable
fun Bad() {
    Box(Modifier.padding(16.dp).background(Color(0xFFFF0000))) {
        Text("Hello there")
    }
}
EOF
cat > "$UI/Good.kt" <<'EOF'
package demo

@Composable
fun Good(label: String) {
    Box(Modifier.padding(AppTheme.spacing.lg).padding(0.dp)) {
        Text(label)
    }
}

@ComponentPreviews
@Composable
private fun GoodPreview() {
    PreviewSurface { Good(label = "Preview text is fine") }
}
EOF
out="$("$HOOKS/design-lint-check.sh" "$UI/Bad.kt")"; code=$?
assert_contains "lint color" "$out" "hardcoded color"
assert_contains "lint dp" "$out" "raw dp/sp"
assert_contains "lint string" "$out" "hardcoded user-facing string"
assert_contains "lint preview" "$out" "without a @Preview"
[[ $code -eq 1 ]] && pass "lint exit 1" || bad "lint exit 1" "got $code"
out="$("$HOOKS/design-lint-check.sh" "$UI/Good.kt")"; code=$?
assert_empty "lint clean file" "$out"
[[ $code -eq 0 ]] && pass "lint exit 0" || bad "lint exit 0" "got $code"

out="$(run design-lint.sh cursor "{\"hook_event_name\":\"postToolUse\",\"tool_name\":\"Write\",\"tool_input\":{\"file_path\":\"$UI/Bad.kt\"}}")"
assert_contains "cursor design lint feedback" "$out" '"additional_context"'
out="$(run design-lint.sh claude "{\"hook_event_name\":\"PostToolUse\",\"tool_name\":\"Edit\",\"tool_input\":{\"file_path\":\"$UI/Bad.kt\"}}")"
assert_contains "claude design lint feedback" "$out" '"hookEventName": "PostToolUse"'

NET="$WORK/resturant-app/app/src/main/java/com/noshitechinc/restaurant/core/network/Foo.kt"
mkdir -p "$(dirname "$NET")"; echo "package x" > "$NET"
run format-kotlin.sh cursor "{\"hook_event_name\":\"afterFileEdit\",\"conversation_id\":\"s2\",\"file_path\":\"$NET\"}" >/dev/null
[[ -f "$WORK/resturant-app/.ai/.session/s2.log" ]] && pass "edit recorded" || bad "edit recorded" "no session log"

out="$(run doc-sync-reminder.sh cursor '{"hook_event_name":"stop","conversation_id":"s2","status":"completed","loop_count":0}')"
assert_contains "doc sync followup" "$out" '"followup_message"'
assert_contains "doc sync lists file" "$out" 'core/network/Foo.kt'
out="$(run doc-sync-reminder.sh cursor '{"hook_event_name":"stop","conversation_id":"s2","status":"completed","loop_count":0}')"
assert_empty "doc sync reminds once" "$out"

run format-kotlin.sh claude "{\"hook_event_name\":\"PostToolUse\",\"session_id\":\"s3\",\"tool_input\":{\"file_path\":\"$NET\"}}" >/dev/null
out="$(run doc-sync-reminder.sh claude '{"hook_event_name":"Stop","session_id":"s3","stop_hook_active":true}')"
assert_empty "claude stop_hook_active skipped" "$out"
out="$(run doc-sync-reminder.sh claude '{"hook_event_name":"Stop","session_id":"s3","stop_hook_active":false}')"
assert_contains "claude doc sync block" "$out" '"decision": "block"'

run format-kotlin.sh cursor "{\"hook_event_name\":\"afterFileEdit\",\"conversation_id\":\"s4\",\"file_path\":\"$NET\"}" >/dev/null
run format-kotlin.sh cursor "{\"hook_event_name\":\"afterFileEdit\",\"conversation_id\":\"s4\",\"file_path\":\"$WORK/resturant-app-architecture/PROJECT-INDEX.md\"}" >/dev/null
out="$(run doc-sync-reminder.sh cursor '{"hook_event_name":"stop","conversation_id":"s4","status":"completed","loop_count":0}')"
assert_empty "doc sync skipped when docs edited" "$out"

exit $fail
