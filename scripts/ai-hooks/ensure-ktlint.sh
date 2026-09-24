#!/usr/bin/env bash
set -euo pipefail
KTLINT_VERSION="1.8.0"
root="$(cd "$(dirname "$0")/../.." && pwd)"
bin="$root/.ai/.tools/ktlint-$KTLINT_VERSION"
if [[ ! -x "$bin" ]]; then
  mkdir -p "$(dirname "$bin")"
  curl -fsSL -o "$bin.tmp" "https://github.com/pinterest/ktlint/releases/download/$KTLINT_VERSION/ktlint"
  chmod +x "$bin.tmp"
  mv "$bin.tmp" "$bin"
fi
printf '%s\n' "$bin"
