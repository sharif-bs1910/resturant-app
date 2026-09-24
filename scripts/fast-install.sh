#!/usr/bin/env bash
# Fast incremental build + install to connected Android device(s).
# Does NOT clean by default — Gradle only rebuilds what changed.
#
# Usage:
#   ./scripts/fast-install.sh
#   ./scripts/fast-install.sh --flavor staging
#   ./scripts/fast-install.sh --serial emulator-5554
#   ./scripts/fast-install.sh --assemble-only   # build APK, no device needed
#   ./scripts/fast-install.sh --clean           # slow full rebuild once
#   ./scripts/fast-install.sh --no-launch
#   ./scripts/fast-install.sh --all             # install on every connected device
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ANDROID_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
GRADLEW="$ANDROID_ROOT/gradlew"

FLAVOR="dev"
BUILD_TYPE="debug"
DO_CLEAN=false
LAUNCH=true
INSTALL_ALL=false
ASSEMBLE_ONLY=false
SERIAL="${ANDROID_SERIAL:-}"
OFFLINE=false

BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log() { printf "${BLUE}▸ %s${NC}\n" "$*"; }
ok() { printf "${GREEN}✓ %s${NC}\n" "$*"; }
warn() { printf "${YELLOW}! %s${NC}\n" "$*"; }
fail() { printf "${RED}✗ %s${NC}\n" "$*" >&2; exit 1; }

usage() {
  cat <<'EOF'
Fast incremental build + install (Noshitech Restaurant)

Usage:
  ./scripts/fast-install.sh [options]

Options:
  --flavor <dev|staging|prod>   Default: dev
  --release                     Build release instead of debug
  --serial <id>                 Target one device (or set ANDROID_SERIAL)
  --all                         Install on all connected devices
  --assemble-only               Build APK only (no device / no install)
  --clean                       Run gradle clean first (slow; use rarely)
  --offline                     Pass --offline to Gradle
  --no-launch                   Do not start the app after install
  -h, --help                    Show this help

Examples:
  ./scripts/fast-install.sh
  ./scripts/fast-install.sh --assemble-only
  ./scripts/fast-install.sh --flavor staging --serial R58M123ABC
  ./scripts/fast-install.sh --clean
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --flavor)
      FLAVOR="${2:?missing flavor}"
      shift 2
      ;;
    --release)
      BUILD_TYPE="release"
      shift
      ;;
    --serial|-s)
      SERIAL="${2:?missing serial}"
      shift 2
      ;;
    --all)
      INSTALL_ALL=true
      shift
      ;;
    --assemble-only)
      ASSEMBLE_ONLY=true
      shift
      ;;
    --clean)
      DO_CLEAN=true
      shift
      ;;
    --offline)
      OFFLINE=true
      shift
      ;;
    --no-launch)
      LAUNCH=false
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      fail "Unknown option: $1 (try --help)"
      ;;
  esac
done

case "$FLAVOR" in
  dev|staging|prod) ;;
  *) fail "Invalid flavor: $FLAVOR (use dev|staging|prod)" ;;
esac

FLAVOR_CAP="$(printf '%s' "$FLAVOR" | awk '{print toupper(substr($0,1,1)) substr($0,2)}')"
BUILD_CAP="$(printf '%s' "$BUILD_TYPE" | awk '{print toupper(substr($0,1,1)) substr($0,2)}')"
VARIANT="${FLAVOR_CAP}${BUILD_CAP}"
INSTALL_TASK=":app:install${VARIANT}"
ASSEMBLE_TASK=":app:assemble${VARIANT}"

case "$FLAVOR" in
  dev) PACKAGE_ID="com.noshitechinc.restaurant.dev" ;;
  staging) PACKAGE_ID="com.noshitechinc.restaurant.staging" ;;
  prod) PACKAGE_ID="com.noshitechinc.restaurant" ;;
esac
MAIN_ACTIVITY="com.noshitechinc.restaurant.app.MainActivity"

export ANDROID_HOME="${ANDROID_HOME:-$HOME/Android/Sdk}"
export GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
if [[ -z "${JAVA_HOME:-}" ]]; then
  if [[ -d /usr/lib/jvm/java-21-openjdk-amd64 ]]; then
    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
  elif [[ -d /opt/android-studio/jbr ]]; then
    export JAVA_HOME=/opt/android-studio/jbr
  elif [[ -d "$HOME/.local/opt/android-studio/jbr" ]]; then
    export JAVA_HOME="$HOME/.local/opt/android-studio/jbr"
  fi
fi

resolve_adb() {
  if command -v adb >/dev/null 2>&1; then
    command -v adb
    return
  fi
  if [[ -x "$ANDROID_HOME/platform-tools/adb" ]]; then
    echo "$ANDROID_HOME/platform-tools/adb"
    return
  fi
  fail "adb not found. Install platform-tools or set ANDROID_HOME"
}

list_devices() {
  "$ADB" devices | awk 'NR>1 && $2=="device" {print $1}'
}

require_devices() {
  mapfile -t DEVICES < <(list_devices)
  if [[ ${#DEVICES[@]} -eq 0 ]]; then
    fail "No connected devices/emulators (adb devices shows none in 'device' state). Use --assemble-only to build without a device."
  fi
  if [[ -n "$SERIAL" ]]; then
    local found=false
    for d in "${DEVICES[@]}"; do
      if [[ "$d" == "$SERIAL" ]]; then
        found=true
        break
      fi
    done
    $found || fail "Device not found: $SERIAL (connected: ${DEVICES[*]})"
    DEVICES=("$SERIAL")
  elif [[ "$INSTALL_ALL" == false && ${#DEVICES[@]} -gt 1 ]]; then
    warn "Multiple devices: ${DEVICES[*]}"
    warn "Using first: ${DEVICES[0]} (pass --serial <id> or --all)"
    DEVICES=("${DEVICES[0]}")
  fi
}

launch_app() {
  local serial="$1"
  log "Launching $PACKAGE_ID on $serial"
  "$ADB" -s "$serial" shell am start -n "$PACKAGE_ID/$MAIN_ACTIVITY" >/dev/null 2>&1 \
    || "$ADB" -s "$serial" shell monkey -p "$PACKAGE_ID" -c android.intent.category.LAUNCHER 1 >/dev/null 2>&1 \
    || warn "Could not auto-launch; open the app manually"
}

cd "$ANDROID_ROOT"

GRADLE_ARGS=(--parallel --build-cache)
$OFFLINE && GRADLE_ARGS+=(--offline)

if $DO_CLEAN; then
  warn "Cleaning (full rebuild — slow)"
  "$GRADLEW" "${GRADLE_ARGS[@]}" clean
fi

START_TS=$(date +%s)

if $ASSEMBLE_ONLY; then
  log "Building ($ASSEMBLE_TASK)…"
  "$GRADLEW" "${GRADLE_ARGS[@]}" "$ASSEMBLE_TASK"
  APK="$(find "$ANDROID_ROOT/app/build/outputs/apk/$FLAVOR/$BUILD_TYPE" -name "*.apk" ! -name "*.apk.idsig" 2>/dev/null | head -1 || true)"
  [[ -n "$APK" && -f "$APK" ]] && ok "APK: $APK"
else
  ADB="$(resolve_adb)"
  require_devices
  ok "Target device(s): ${DEVICES[*]}"
  log "Variant: $VARIANT  package: $PACKAGE_ID"

  if [[ ${#DEVICES[@]} -eq 1 ]]; then
    export ANDROID_SERIAL="${DEVICES[0]}"
    log "Building + installing ($INSTALL_TASK)…"
    "$GRADLEW" "${GRADLE_ARGS[@]}" "$INSTALL_TASK"
    unset ANDROID_SERIAL
    $LAUNCH && launch_app "${DEVICES[0]}"
  else
    log "Building once ($ASSEMBLE_TASK)…"
    "$GRADLEW" "${GRADLE_ARGS[@]}" "$ASSEMBLE_TASK"

    APK="$(find "$ANDROID_ROOT/app/build/outputs/apk/$FLAVOR/$BUILD_TYPE" -name "*.apk" ! -name "*.apk.idsig" | head -1 || true)"
    [[ -n "$APK" && -f "$APK" ]] || fail "APK not found under app/build/outputs/apk/$FLAVOR/$BUILD_TYPE"

    for serial in "${DEVICES[@]}"; do
      log "Installing on $serial…"
      "$ADB" -s "$serial" install -r "$APK"
      $LAUNCH && launch_app "$serial"
    done
  fi
fi

ELAPSED=$(( $(date +%s) - START_TS ))
ok "Done in ${ELAPSED}s (incremental next time — avoid --clean)"
