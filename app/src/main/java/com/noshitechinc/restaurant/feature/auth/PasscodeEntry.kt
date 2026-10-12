package com.noshitechinc.restaurant.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

object PasscodeTags {
    fun digit(n: Int) = "passcode_digit_$n"
    const val DELETE = "passcode_delete"
}

private enum class CellState { Filled, Active, Empty, Error }

@Composable
fun PasscodeCells(length: Int, value: String, masked: Boolean, hasError: Boolean, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val metrics = rememberPasscodeMetrics(cellCount = length, maxWidth = maxWidth)
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .then(if (metrics.scrollCells) Modifier.horizontalScroll(rememberScrollState()) else Modifier),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
        ) {
            repeat(length) { index ->
                val state = when {
                    hasError -> CellState.Error
                    index < value.length -> CellState.Filled
                    index == value.length -> CellState.Active
                    else -> CellState.Empty
                }
                val glyph = when {
                    index >= value.length -> ""
                    masked -> "•"
                    else -> value[index].toString()
                }
                PasscodeCell(glyph = glyph, state = state, width = metrics.cellWidth, height = metrics.cellHeight)
            }
        }
    }
}

@Composable
private fun PasscodeCell(glyph: String, state: CellState, width: Dp, height: Dp) {
    val shape = RoundedCornerShape(AppTheme.radius.sm)
    val borderColor = when (state) {
        CellState.Active -> AppTheme.colors.primary
        CellState.Error -> AppTheme.colors.destructive
        else -> AppTheme.colors.textPrimary.copy(alpha = 0.14f)
    }
    val stroke = if (state == CellState.Active || state == CellState.Error) AppTheme.border.focus else AppTheme.border.thin
    Surface(
        shape = shape,
        color = AppTheme.colors.card,
        border = BorderStroke(stroke, borderColor),
        modifier = Modifier.size(width = width, height = height),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = glyph,
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
        }
    }
}

@Composable
fun PasscodeKeypad(onDigit: (Int) -> Unit, onDelete: () -> Unit, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val metrics = rememberPasscodeMetrics(cellCount = 6, maxWidth = maxWidth)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .then(if (metrics.scrollKeys) Modifier.horizontalScroll(rememberScrollState()) else Modifier),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            KeyRow {
                DigitKey(1, onDigit, metrics.keyWidth, metrics.keyHeight)
                DigitKey(2, onDigit, metrics.keyWidth, metrics.keyHeight)
                DigitKey(3, onDigit, metrics.keyWidth, metrics.keyHeight)
            }
            KeyRow {
                DigitKey(4, onDigit, metrics.keyWidth, metrics.keyHeight)
                DigitKey(5, onDigit, metrics.keyWidth, metrics.keyHeight)
                DigitKey(6, onDigit, metrics.keyWidth, metrics.keyHeight)
            }
            KeyRow {
                DigitKey(7, onDigit, metrics.keyWidth, metrics.keyHeight)
                DigitKey(8, onDigit, metrics.keyWidth, metrics.keyHeight)
                DigitKey(9, onDigit, metrics.keyWidth, metrics.keyHeight)
            }
            KeyRow {
                Box(modifier = Modifier.size(width = metrics.keyWidth, height = metrics.keyHeight))
                DigitKey(0, onDigit, metrics.keyWidth, metrics.keyHeight)
                DeleteKey(onDelete, metrics.keyWidth, metrics.keyHeight)
            }
        }
    }
}

@Composable
private fun KeyRow(content: @Composable () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)) { content() }
}

@Composable
private fun DigitKey(digit: Int, onDigit: (Int) -> Unit, width: Dp, height: Dp) {
    Surface(
        onClick = { onDigit(digit) },
        shape = RoundedCornerShape(AppTheme.radius.sm),
        color = AppTheme.colors.card,
        border = BorderStroke(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
        modifier = Modifier
            .size(width = width, height = height)
            .testTag(PasscodeTags.digit(digit)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = digit.toString(),
                style = AppTheme.typography.headlineSmall,
                color = AppTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
        }
    }
}

@Composable
private fun DeleteKey(onDelete: () -> Unit, width: Dp, height: Dp) {
    Surface(
        onClick = onDelete,
        shape = RoundedCornerShape(AppTheme.radius.sm),
        color = AppTheme.colors.card,
        border = BorderStroke(AppTheme.border.thin, AppTheme.colors.textPrimary.copy(alpha = 0.14f)),
        modifier = Modifier
            .size(width = width, height = height)
            .testTag(PasscodeTags.DELETE),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = stringResource(R.string.auth_delete),
                tint = AppTheme.colors.textMuted,
                modifier = Modifier.size(AppTheme.sizes.iconMd),
            )
        }
    }
}

private data class PasscodeMetrics(
    val cellWidth: Dp,
    val cellHeight: Dp,
    val keyWidth: Dp,
    val keyHeight: Dp,
    val scrollCells: Boolean,
    val scrollKeys: Boolean,
)

@Composable
private fun rememberPasscodeMetrics(cellCount: Int, maxWidth: Dp): PasscodeMetrics {
    val gap = AppTheme.spacing.md
    val cellToken = AppTheme.sizes.passcodeCellWidth
    val keyToken = AppTheme.sizes.passcodeKeyWidth
    val keyHeightToken = AppTheme.sizes.passcodeKeyHeight
    if (maxWidth == Dp.Unspecified || maxWidth == Dp.Infinity) {
        return PasscodeMetrics(cellToken, keyHeightToken, keyToken, keyHeightToken, scrollCells = false, scrollKeys = false)
    }
    val keyFitted = (maxWidth - gap * 2) / 3
    val scrollKeys = keyFitted < AppTheme.sizes.minTouchTarget
    val keyWidth = if (scrollKeys) keyToken else minOf(keyToken, keyFitted)
    val keyScale = keyWidth / keyToken
    val keyHeight = maxOf(AppTheme.sizes.minTouchTarget, keyHeightToken * keyScale)
    val cellFitted = (maxWidth - gap * (cellCount - 1)) / cellCount
    val scrollCells = cellFitted < AppTheme.spacing.xxl
    val cellWidth = if (scrollCells) cellToken else minOf(cellToken, cellFitted)
    val cellHeight = if (cellWidth == cellToken) {
        keyHeightToken
    } else {
        maxOf(AppTheme.spacing.xxxl, keyHeightToken * (cellWidth / cellToken))
    }
    return PasscodeMetrics(cellWidth, cellHeight, keyWidth, keyHeight, scrollCells, scrollKeys)
}

@ComponentPreviews
@Composable
private fun PasscodeEntryPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg), horizontalAlignment = Alignment.CenterHorizontally) {
            PasscodeCells(length = 6, value = "482", masked = false, hasError = false)
            PasscodeKeypad(onDigit = {}, onDelete = {})
        }
    }
}
