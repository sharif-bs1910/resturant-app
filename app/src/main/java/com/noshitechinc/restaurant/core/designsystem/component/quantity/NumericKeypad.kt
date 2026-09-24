package com.noshitechinc.restaurant.core.designsystem.component.quantity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.interaction.ForcedInteraction
import com.noshitechinc.restaurant.core.designsystem.interaction.focusRing
import com.noshitechinc.restaurant.core.designsystem.interaction.rememberInteractionVisuals
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

object NumericKeypadTags {
    fun digit(n: Int): String = "keypad_key_$n"
    const val BACKSPACE = "keypad_backspace"
    const val DECIMAL = "keypad_decimal"
    const val DOUBLE_ZERO = "keypad_double_zero"
    const val CLEAR = "keypad_clear"
}

@Composable
fun NumericKeypad(
    onKey: (KeypadKey) -> Unit,
    modifier: Modifier = Modifier,
    mode: KeypadMode = KeypadMode.Integer,
    enabled: Boolean = true,
) {
    val leftKey: KeypadKey = when (mode) {
        KeypadMode.Decimal -> KeypadKey.Decimal
        KeypadMode.Currency -> KeypadKey.DoubleZero
        KeypadMode.Integer, KeypadMode.Pin -> KeypadKey.Clear
    }
    Column(
        modifier = modifier.widthIn(max = AppTheme.sizes.keypadMaxWidth),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        KeypadRow {
            DigitKey(1, onKey, enabled)
            DigitKey(2, onKey, enabled)
            DigitKey(3, onKey, enabled)
        }
        KeypadRow {
            DigitKey(4, onKey, enabled)
            DigitKey(5, onKey, enabled)
            DigitKey(6, onKey, enabled)
        }
        KeypadRow {
            DigitKey(7, onKey, enabled)
            DigitKey(8, onKey, enabled)
            DigitKey(9, onKey, enabled)
        }
        KeypadRow {
            SpecialKey(leftKey, mode, onKey, enabled)
            DigitKey(0, onKey, enabled)
            KeypadButton(
                onClick = { onKey(KeypadKey.Backspace) },
                enabled = enabled,
                testTag = NumericKeypadTags.BACKSPACE,
                contentDescription = stringResource(R.string.a11y_backspace),
                icon = Icons.AutoMirrored.Filled.Backspace,
            )
        }
    }
}

@Composable
private fun KeypadRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        content = content,
    )
}

@Composable
private fun RowScope.DigitKey(value: Int, onKey: (KeypadKey) -> Unit, enabled: Boolean) {
    KeypadButton(
        onClick = { onKey(KeypadKey.Digit(value)) },
        enabled = enabled,
        testTag = NumericKeypadTags.digit(value),
        label = value.toString(),
    )
}

@Composable
private fun RowScope.SpecialKey(key: KeypadKey, mode: KeypadMode, onKey: (KeypadKey) -> Unit, enabled: Boolean) {
    val (labelRes, tag) = when (key) {
        KeypadKey.Decimal -> R.string.keypad_decimal to NumericKeypadTags.DECIMAL
        KeypadKey.DoubleZero -> R.string.keypad_double_zero to NumericKeypadTags.DOUBLE_ZERO
        KeypadKey.Clear -> R.string.keypad_clear to NumericKeypadTags.CLEAR
        else -> error("unexpected special key for mode $mode: $key")
    }
    KeypadButton(
        onClick = { onKey(key) },
        enabled = enabled,
        testTag = tag,
        label = stringResource(labelRes),
    )
}

@Composable
private fun RowScope.KeypadButton(
    onClick: () -> Unit,
    enabled: Boolean,
    testTag: String,
    label: String? = null,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val visuals = rememberInteractionVisuals(source)
    val shape = RoundedCornerShape(AppTheme.radius.md)
    val container = when {
        !enabled -> AppTheme.colors.disabledContainer
        visuals.pressed -> AppTheme.colors.surfacePressed
        else -> AppTheme.colors.surface
    }
    val content = if (enabled) AppTheme.colors.textPrimary else AppTheme.colors.textDisabled
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = container,
        contentColor = content,
        border = BorderStroke(AppTheme.border.thin, if (enabled) AppTheme.colors.outline else AppTheme.colors.outlineVariant),
        interactionSource = source,
        modifier = Modifier
            .weight(1f)
            .heightIn(min = AppTheme.sizes.keypadKey)
            .testTag(testTag)
            .focusRing(visuals.focused, AppTheme.colors.focusRing, AppTheme.border.focus, shape),
    ) {
        Box(contentAlignment = Alignment.Center) {
            when {
                icon != null -> Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier,
                )

                label != null -> Text(
                    text = label,
                    style = AppTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@ComponentPreviews
@Composable
private fun NumericKeypadIntegerPreview() {
    PreviewSurface {
        NumericKeypad(onKey = {}, mode = KeypadMode.Integer)
    }
}

@ComponentPreviews
@Composable
private fun NumericKeypadDecimalPreview() {
    PreviewSurface {
        NumericKeypad(onKey = {}, mode = KeypadMode.Decimal)
    }
}

@ComponentPreviews
@Composable
private fun NumericKeypadCurrencyPreview() {
    PreviewSurface {
        NumericKeypad(onKey = {}, mode = KeypadMode.Currency)
    }
}

@ComponentPreviews
@Composable
private fun NumericKeypadPinPreview() {
    PreviewSurface {
        NumericKeypad(onKey = {}, mode = KeypadMode.Pin)
    }
}

@ComponentPreviews
@Composable
private fun NumericKeypadDisabledPreview() {
    PreviewSurface {
        NumericKeypad(onKey = {}, enabled = false)
    }
}

@ComponentPreviews
@Composable
private fun NumericKeypadPressedPreview() {
    PreviewSurface(forcedInteraction = ForcedInteraction.Pressed) {
        NumericKeypad(onKey = {})
    }
}

@ComponentPreviews
@Composable
private fun NumericKeypadInteractivePreview() {
    PreviewSurface {
        var value by remember { mutableStateOf("") }
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) {
            Text(
                text = value.ifEmpty { " " },
                style = AppTheme.typography.numericDisplay,
                color = AppTheme.colors.textPrimary,
            )
            NumericKeypad(
                onKey = { key -> value = KeypadReducer.reduce(value, key, KeypadMode.Decimal) },
                mode = KeypadMode.Decimal,
            )
        }
    }
}
