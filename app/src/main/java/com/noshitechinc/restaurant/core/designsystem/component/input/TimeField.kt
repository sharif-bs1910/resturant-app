package com.noshitechinc.restaurant.core.designsystem.component.input

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.model.TimeOfDay
import com.noshitechinc.restaurant.core.designsystem.preview.ComponentPreviews
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeField(
    time: TimeOfDay?,
    onTimeChange: (TimeOfDay) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }
    val display = if (time == null) {
        ""
    } else {
        DateFormat.getTimeFormat(context).format(calendarFor(time).time)
    }

    Box(modifier = modifier) {
        AppTextField(
            value = display,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = label,
            errorText = errorText,
            enabled = enabled,
            readOnly = true,
            trailingContent = {
                IconButton(
                    onClick = { showPicker = true },
                    enabled = enabled,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = stringResource(R.string.a11y_choose_time),
                    )
                }
            },
        )
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(end = AppTheme.sizes.minTouchTarget)
                    .clickable { showPicker = true },
            )
        }
    }

    if (showPicker) {
        val initial = time ?: run {
            val now = Calendar.getInstance()
            TimeOfDay(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))
        }
        val state = rememberTimePickerState(
            initialHour = initial.hour,
            initialMinute = initial.minute,
            is24Hour = DateFormat.is24HourFormat(context),
        )
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text(stringResource(R.string.time_picker_title)) },
            text = { TimePicker(state = state) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeChange(TimeOfDay(state.hour, state.minute))
                        showPicker = false
                    },
                ) {
                    Text(stringResource(R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }
}

private fun calendarFor(time: TimeOfDay): Calendar = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, time.hour)
    set(Calendar.MINUTE, time.minute)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

@ComponentPreviews
@Composable
private fun TimeFieldStatesPreview() {
    PreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
            TimeField(time = null, onTimeChange = {}, label = "Ready by")
            TimeField(time = TimeOfDay(9, 30), onTimeChange = {}, label = "Ready by")
            TimeField(
                time = TimeOfDay(9, 30),
                onTimeChange = {},
                label = "Ready by",
                errorText = "Required",
            )
            TimeField(
                time = TimeOfDay(9, 30),
                onTimeChange = {},
                label = "Ready by",
                enabled = false,
            )
        }
    }
}
