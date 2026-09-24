#!/usr/bin/env bash
set -uo pipefail

PREVIEW_RE='^[[:space:]]*@(Preview|TabletLandscapePreview|TabletPortraitPreview|FontScalePreviews|ComponentPreviews|ScreenPreviews)([[:space:](]|$)'
COLOR_RE='Color\(0x|Color\.(Red|Blue|Black|White|Gray|Green|Yellow|Cyan|Magenta|DarkGray|LightGray)([^A-Za-z]|$)'
DP_RE='(^|[^A-Za-z0-9_.])([1-9][0-9]*|[0-9]+\.[0-9]+)\.(dp|sp)([^A-Za-z]|$)'
STRING_RE='(Text\(|(text|title|label|message|placeholder|contentDescription)[[:space:]]*=)[[:space:]]*"[^"]*[A-Za-z][^"]*"'
COMPOSABLE_FUN_RE='^fun[[:space:]]+[A-Z]'
INLINE_COMPOSABLE_FUN_RE='^@Composable[[:space:]]+fun[[:space:]]+[A-Z]'
issues=0

report() { echo "$1:$2: $3"; issues=$((issues + 1)); }

is_ui_file() {
  [[ "$1" =~ /(feature|core/designsystem|core/ui)/ ]] && [[ ! "$1" =~ /core/designsystem/(theme|preview)/ ]]
}

check_file() {
  local f="$1" n=0 in_preview=0 has_preview=0 public_composable=0 prev="" line
  is_ui_file "$f" || return 0
  while IFS= read -r line || [[ -n "$line" ]]; do
    n=$((n + 1))
    if [[ "$line" =~ $PREVIEW_RE ]]; then in_preview=1; has_preview=1; fi
    if [[ $in_preview -eq 0 && ! "$line" =~ ^[[:space:]]*// ]]; then
      [[ "$line" =~ $COLOR_RE ]] && report "$f" "$n" "hardcoded color - use AppTheme.colors"
      [[ "$line" =~ $DP_RE ]] && report "$f" "$n" "raw dp/sp literal - use AppTheme.spacing/radius/border/sizes or AppTheme.typography"
      [[ "$line" =~ $STRING_RE ]] && report "$f" "$n" "hardcoded user-facing string - use stringResource(R.string.…)"
    fi
    if [[ "$prev" =~ ^@Composable && "$line" =~ $COMPOSABLE_FUN_RE ]] || [[ "$line" =~ $INLINE_COMPOSABLE_FUN_RE ]]; then
      public_composable=1
    fi
    prev="$line"
  done < "$f"
  if [[ $public_composable -eq 1 && $has_preview -eq 0 ]]; then
    report "$f" 1 "public @Composable without a @Preview in this file - add previews at the bottom"
  fi
}

for target in "$@"; do
  if [[ -d "$target" ]]; then
    while IFS= read -r f; do check_file "$f"; done < <(find "$target" -type f -name '*.kt' | sort)
  elif [[ -f "$target" ]]; then
    check_file "$target"
  fi
done

[[ $issues -eq 0 ]]
