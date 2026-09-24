---
name: build-ui-component
description: Builds a shared Jetpack Compose design-system component (tokens only, interaction visuals, long-content rules, full preview matrix, optional Compose UI test) in the Noshitech Restaurant Android app. Use when creating or extending a reusable component under core/designsystem/component.
---

# Build a shared UI component

Follows `.cursor/rules/ui-design-system.mdc`. The reference implementation is `core/designsystem/component/button/AppButton.kt`; copy its structure.

Paths: `SRC/` = `app/src/main/java/com/noshitechinc/restaurant/`, `ATEST/` = `app/src/androidTest/java/com/noshitechinc/restaurant/`.

## Before you start

1. Search for an existing component first: `rg -n "^fun [A-Z]" app/src/main/java/com/noshitechinc/restaurant/core/designsystem app/src/main/java/com/noshitechinc/restaurant/core/ui`. Extend or compose existing ones where possible.
2. Changing the public parameters of an existing shared component, or adding a design token, is an impact-analysis trigger: run the `impact-analysis` skill and wait for approval.
3. State the component API (signature) and get it approved.

## Location

`SRC/core/designsystem/component/<group>/AppXxx.kt`, one public component per file. Groups: `button`, `input`, `selection`, `quantity`, `card`, `dialog`, `row`. Add a new group only when none fits. Generic states go in `core/designsystem/state/`; anything that needs `AppError`, `LoadState` or `UiEffect` goes in `core/ui/` (the design system never imports `feature`, `data` or `domain`).

## API rules

- Parameter order: required params, then `modifier: Modifier = Modifier`, then optional params.
- Stateless: take `value` + `onValueChange` (or `selected` + `onSelectedChange`, `quantity` + `onQuantityChange`); never hold the value in `remember`.
- Interactive components take `enabled: Boolean = true` and `interactionSource: MutableInteractionSource? = null`.
- Components with async actions take `loading: Boolean = false` and ignore input while loading.
- Texts arrive as `String` parameters (callers use `stringResource`); the component's own strings (content descriptions, fixed labels) come from `strings.xml`.
- Test tags: accept them through the caller's `modifier`; expose an `object AppXxxTags` only for inner nodes a test must reach.

## Visuals

- `val source = interactionSource ?: remember { MutableInteractionSource() }`, then `val visuals = rememberInteractionVisuals(source)`; pass `source` to the clickable/`Surface`.
- Pressed colour from `visuals.pressed`; focus ring with `Modifier.focusRing(visuals.focused, AppTheme.colors.focusRing, AppTheme.border.focus, shape)`.
- Disabled uses `AppTheme.colors.disabledContainer` / `textDisabled`; errors use `AppTheme.colors.destructive`; statuses map to `StatusTone` in the caller.
- Tokens only: `AppTheme.colors`, `typography`, `spacing`, `radius`, `border`, `sizes`. No `Color(0x…)`, `Color.Red`, raw `N.dp`/`N.sp` or string literals outside preview functions.
- Touch targets at least `AppTheme.sizes.minTouchTarget` (use `heightIn(min = …)`, never a fixed height).

## Long content

- Names: `maxLines` + `TextOverflow.Ellipsis`. Prices and amounts are never truncated: lay them out first at intrinsic width, the name gets `Modifier.weight(1f)`.
- Addresses wrap up to 3 lines. Buttons and chips grow in height instead of truncating.
- Must hold at font scale 1.0, 1.5 and 2.0 and in the `en-XA` pseudo-locale.

## Skeleton

```kotlin
package com.noshitechinc.restaurant.core.designsystem.component.selection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.interaction.focusRing
import com.noshitechinc.restaurant.core.designsystem.interaction.rememberInteractionVisuals
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewData
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun AppXxx(
    text: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val visuals = rememberInteractionVisuals(source)
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val c = AppTheme.colors
    val container = when {
        !enabled -> c.disabledContainer
        selected -> if (visuals.pressed) c.primaryPressed else c.primary
        else -> if (visuals.pressed) c.surfacePressed else c.surface
    }
    val content = when {
        !enabled -> c.textDisabled
        selected -> c.onPrimary
        else -> c.textPrimary
    }
    Surface(
        checked = selected,
        onCheckedChange = onSelectedChange,
        enabled = enabled,
        shape = shape,
        color = container,
        contentColor = content,
        border = if (selected) null else BorderStroke(AppTheme.border.thin, c.outline),
        interactionSource = source,
        modifier = modifier
            .heightIn(min = AppTheme.sizes.minTouchTarget)
            .focusRing(visuals.focused, c.focusRing, AppTheme.border.focus, shape),
    ) {
        Text(
            text = text,
            style = AppTheme.typography.labelLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.sm),
        )
    }
}

@ComponentPreviews
@Composable
private fun AppXxxStatesPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            AppXxx(text = "Unselected", selected = false, onSelectedChange = {})
            AppXxx(text = "Selected", selected = true, onSelectedChange = {})
            AppXxx(text = "Disabled", selected = false, onSelectedChange = {}, enabled = false)
            AppXxx(text = "Selected disabled", selected = true, onSelectedChange = {}, enabled = false)
        }
    }
}

@ComponentPreviews
@Composable
private fun AppXxxPressedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Pressed) {
        AppXxx(text = "Pressed", selected = false, onSelectedChange = {})
    }
}

@ComponentPreviews
@Composable
private fun AppXxxFocusedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Focused) {
        AppXxx(text = "Focused", selected = true, onSelectedChange = {})
    }
}

@ComponentPreviews
@Composable
private fun AppXxxLongTextPreview() {
    PreviewSurface {
        AppXxx(
            text = PreviewData.TRANSLATED_LABEL,
            selected = false,
            onSelectedChange = {},
            modifier = Modifier.width(220.dp),
        )
    }
}
```

Adjust the colours per variant; confirm every `AppTheme.colors.*` name you use exists in `theme/AppColors.kt`.

## Preview matrix

- [ ] Every variant × every applicable state: enabled, disabled, pressed (`ForcedInteraction.Pressed`), focused (`ForcedInteraction.Focused`), loading, error, empty, selected.
- [ ] Long and translated samples from `PreviewData` (`LONG_NAME`, `TRANSLATED_LABEL`, `LONG_PRICE`, `ADDRESS_LONG`) in a width-constrained container.
- [ ] Every preview is `private`, annotated `@ComponentPreviews` (tablet medium width, tablet expanded width, tablet medium width at font scale 2.0) and wrapped in `PreviewSurface { }`.
- [ ] Previews are at the bottom of the file; nothing but previews after the first preview annotation.

## Compose UI test (when the component has behaviour)

Add `ATEST/core/designsystem/AppXxxTest.kt` when the component has logic beyond display (clamping, ignoring taps while loading/disabled, keypad input). Model it on `AppButtonTest`: `@get:Rule val rule = createComposeRule()`, content wrapped in `AppTheme { }`, nodes found with `onNodeWithTag(...)` (never by display text). Use camelCase names for androidTest (`fun loadingButtonIgnoresTaps()`); backticked sentences only in `app/src/test` (names with spaces fail D8 below API 30, `minSdk` is 25). Put pure logic in a reducer object (like `KeypadReducer`) and unit-test it under `app/src/test/`.

## Verify

```bash
scripts/ai-hooks/design-lint-check.sh app/src/main/java/com/noshitechinc/restaurant/core/designsystem
ANDROID_HOME=$HOME/Android/Sdk ./gradlew ktlintCheck detekt testDevDebugUnitTest assembleDevDebug
ANDROID_HOME=$HOME/Android/Sdk ./gradlew assembleDevDebugAndroidTest
```

The design lint must print nothing; run `connectedDevDebugAndroidTest` when a device or emulator is available. Then run the `review-change` skill.
