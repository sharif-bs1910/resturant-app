#!/usr/bin/env bash
# List cached Figma MCP entries for a fileKey (optional nodeId).
# Usage: figma-cache-lookup.sh <fileKey> [nodeId]
set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# shellcheck source=lib.sh
source "$SCRIPT_DIR/lib.sh"

FILE_KEY="${1:-}"
NODE_ID="${2:-}"

if [[ -z "$FILE_KEY" ]]; then
  echo "usage: $0 <fileKey> [nodeId]" >&2
  exit 2
fi

ROOT="$(project_dir)/.ai/figma-cache"
SAFE_FILE="$(printf '%s' "$FILE_KEY" | tr ':/\\' '---' | tr -c 'A-Za-z0-9._-' '_')"
DIR="$ROOT/$SAFE_FILE"

if [[ ! -d "$DIR" ]]; then
  echo "No cache for fileKey=$FILE_KEY under $DIR"
  echo "Call Figma MCP once (hooks will save), or pass forceRefresh:true."
  exit 1
fi

echo "Cache root: $DIR"
if [[ -f "$DIR/session.json" ]]; then
  echo "session: $DIR/session.json"
  command -v jq >/dev/null 2>&1 && jq -c '{sessionId,cached_at}' "$DIR/session.json" 2>/dev/null || true
fi

NODE_GLOB="*"
if [[ -n "$NODE_ID" ]]; then
  SAFE_NODE="$(printf '%s' "$NODE_ID" | tr ':/\\' '---' | tr -c 'A-Za-z0-9._-' '_')"
  NODE_GLOB="$SAFE_NODE"
fi

shopt -s nullglob
found=0
for meta in "$DIR"/$NODE_GLOB/*.meta.json; do
  found=1
  json="${meta%.meta.json}.json"
  if command -v jq >/dev/null 2>&1; then
    jq -c --arg json "$json" '{
      tool: .tool,
      nodeId: .nodeId,
      incomplete: (.incomplete // false),
      cached_at: .cached_at,
      cache_file: .cache_file,
      assets: (.assets // []),
      exists: ($json | length > 0)
    }' "$meta" 2>/dev/null || echo "$meta"
  else
    echo "$meta"
    [[ -f "$json" ]] && echo "  json: $json"
  fi
done

if [[ "$found" -eq 0 ]]; then
  echo "No entries for node filter '${NODE_ID:-*}'."
  echo "Directories:"
  find "$DIR" -mindepth 1 -maxdepth 1 -type d -printf '  %f\n' 2>/dev/null || ls "$DIR"
  exit 1
fi

exit 0
