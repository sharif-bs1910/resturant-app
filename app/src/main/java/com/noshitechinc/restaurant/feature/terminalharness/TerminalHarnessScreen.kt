package com.noshitechinc.restaurant.feature.terminalharness

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.common.UiText
import com.noshitechinc.restaurant.core.designsystem.component.button.AppButton
import com.noshitechinc.restaurant.core.designsystem.component.button.ButtonVariant
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.terminal.model.ReaderInfo
import com.noshitechinc.restaurant.core.ui.AppScaffold
import com.noshitechinc.restaurant.core.ui.asString

@Composable
fun TerminalHarnessRoute(onBack: () -> Unit, viewModel: TerminalHarnessViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val submitting by viewModel.submittingKeys.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        // Result only includes permissions we just requested — re-check the live grant state.
        if (hasTerminalPermissions(context, requireBluetooth = !state.simulated)) {
            viewModel.onDiscover()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    fun discoverWithPermissions() {
        val requireBluetooth = !state.simulated
        if (hasTerminalPermissions(context, requireBluetooth = requireBluetooth)) {
            viewModel.onDiscover()
            return
        }
        permissionLauncher.launch(terminalPermissions(requireBluetooth = requireBluetooth))
    }

    AppScaffold(effects = viewModel.effects) { padding ->
        TerminalHarnessScreen(
            state = state,
            discovering = "terminal_discover" in submitting,
            connecting = "terminal_connect" in submitting,
            paying = "terminal_pay" in submitting,
            testingStub = "terminal_stub" in submitting,
            onBack = onBack,
            onToggleSimulated = viewModel::onToggleSimulated,
            onSelectReader = viewModel::onSelectReader,
            onTestStub = viewModel::onTestStub,
            onDiscover = ::discoverWithPermissions,
            onConnect = viewModel::onConnect,
            onDisconnect = viewModel::onDisconnect,
            onPay = viewModel::onPay,
            onCancelPayment = viewModel::onCancelPayment,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
fun TerminalHarnessScreen(
    state: TerminalHarnessUiState,
    discovering: Boolean,
    connecting: Boolean,
    paying: Boolean,
    testingStub: Boolean,
    onBack: () -> Unit,
    onToggleSimulated: (Boolean) -> Unit,
    onSelectReader: (String) -> Unit,
    onTestStub: () -> Unit,
    onDiscover: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onPay: () -> Unit,
    onCancelPayment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.spacing.xl)
            .testTag(TerminalHarnessTags.ROOT),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        Text(
            text = stringResource(R.string.terminal_harness_title),
            style = AppTheme.typography.headlineSmall,
            color = AppTheme.colors.textPrimary,
        )
        Text(
            text = stringResource(R.string.terminal_harness_subtitle),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.textSecondary,
        )
        MetaRow(stringResource(R.string.terminal_harness_location), state.locationId.ifBlank { "—" })
        MetaRow(stringResource(R.string.terminal_harness_stub_url), state.stubBaseUrl.ifBlank { "—" })
        MetaRow(
            stringResource(R.string.terminal_harness_phase),
            stringResource(state.phase.labelRes()),
        )
        state.connectedSerial?.let {
            MetaRow(stringResource(R.string.terminal_harness_connected), it)
        }
        state.lastPaymentIntentId?.let {
            MetaRow(stringResource(R.string.terminal_harness_payment_intent), it)
        }
        state.statusText?.let {
            Text(
                text = it.asString(),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.destructive,
                modifier = Modifier.testTag(TerminalHarnessTags.STATUS),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.terminal_harness_simulated),
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.textPrimary,
            )
            Switch(
                checked = state.simulated,
                onCheckedChange = onToggleSimulated,
                enabled = state.enabled &&
                    state.phase != TerminalHarnessPhase.ProcessingPayment &&
                    state.phase != TerminalHarnessPhase.Discovering,
                modifier = Modifier.testTag(TerminalHarnessTags.SIMULATED_SWITCH),
            )
        }

        if (state.readers.isNotEmpty()) {
            Text(
                text = stringResource(R.string.terminal_harness_readers),
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.textPrimary,
            )
            state.readers.forEach { reader ->
                ReaderRow(
                    reader = reader,
                    selected = reader.id == state.selectedReaderId,
                    onClick = { onSelectReader(reader.id) },
                )
            }
        }

        Column(
            modifier = Modifier.widthIn(max = AppTheme.sizes.formMaxWidth),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
        ) {
            AppButton(
                text = stringResource(R.string.terminal_harness_test_stub),
                onClick = onTestStub,
                loading = testingStub,
                variant = ButtonVariant.Secondary,
                enabled = state.enabled && !testingStub && !discovering && !paying,
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = stringResource(R.string.terminal_harness_discover),
                onClick = onDiscover,
                loading = discovering,
                enabled = state.enabled && !discovering && !connecting && !paying,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TerminalHarnessTags.DISCOVER),
            )
            AppButton(
                text = stringResource(R.string.terminal_harness_connect),
                onClick = onConnect,
                loading = connecting,
                enabled = state.enabled &&
                    state.readers.isNotEmpty() &&
                    !discovering &&
                    !connecting &&
                    !paying,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TerminalHarnessTags.CONNECT),
            )
            AppButton(
                text = stringResource(R.string.terminal_harness_pay),
                onClick = onPay,
                loading = paying,
                enabled = state.enabled &&
                    (
                        state.phase == TerminalHarnessPhase.Connected ||
                            state.phase == TerminalHarnessPhase.Succeeded ||
                            state.phase == TerminalHarnessPhase.Declined
                        ) &&
                    !paying,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TerminalHarnessTags.PAY),
            )
            AppButton(
                text = stringResource(R.string.terminal_harness_cancel_pay),
                onClick = onCancelPayment,
                variant = ButtonVariant.Secondary,
                enabled = paying || state.phase == TerminalHarnessPhase.ProcessingPayment,
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = stringResource(R.string.terminal_harness_disconnect),
                onClick = onDisconnect,
                variant = ButtonVariant.Outline,
                enabled = state.connectedSerial != null && !paying,
                modifier = Modifier.fillMaxWidth(),
            )
            AppButton(
                text = stringResource(R.string.terminal_harness_back),
                onClick = onBack,
                variant = ButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs)) {
        Text(text = label, style = AppTheme.typography.labelMedium, color = AppTheme.colors.textSecondary)
        Text(text = value, style = AppTheme.typography.bodyMedium, color = AppTheme.colors.textPrimary)
    }
}

@Composable
private fun ReaderRow(reader: ReaderInfo, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = AppTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        Text(
            text = reader.serialNumber ?: reader.id,
            style = AppTheme.typography.bodyLarge,
            color = if (selected) AppTheme.colors.primary else AppTheme.colors.textPrimary,
        )
    }
}

private fun TerminalHarnessPhase.labelRes(): Int = when (this) {
    TerminalHarnessPhase.Idle -> R.string.terminal_harness_phase_idle
    TerminalHarnessPhase.Discovering -> R.string.terminal_harness_phase_discovering
    TerminalHarnessPhase.ReadyToConnect -> R.string.terminal_harness_phase_ready
    TerminalHarnessPhase.Connecting -> R.string.terminal_harness_phase_connecting
    TerminalHarnessPhase.Connected -> R.string.terminal_harness_phase_connected
    TerminalHarnessPhase.CreatingIntent -> R.string.terminal_harness_phase_creating_intent
    TerminalHarnessPhase.ProcessingPayment -> R.string.terminal_harness_phase_processing
    TerminalHarnessPhase.Succeeded -> R.string.terminal_harness_phase_succeeded
    TerminalHarnessPhase.Declined -> R.string.terminal_harness_phase_declined
    TerminalHarnessPhase.Error -> R.string.terminal_harness_phase_error
}

object TerminalHarnessTags {
    const val ROOT = "terminal_harness_root"
    const val DISCOVER = "terminal_harness_discover"
    const val CONNECT = "terminal_harness_connect"
    const val PAY = "terminal_harness_pay"
    const val STATUS = "terminal_harness_status"
    const val SIMULATED_SWITCH = "terminal_harness_simulated"
}

private fun terminalPermissions(requireBluetooth: Boolean): Array<String> = buildList {
    add(Manifest.permission.ACCESS_FINE_LOCATION)
    add(Manifest.permission.ACCESS_COARSE_LOCATION)
    if (requireBluetooth && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        add(Manifest.permission.BLUETOOTH_SCAN)
        add(Manifest.permission.BLUETOOTH_CONNECT)
    }
}.toTypedArray()

private fun hasTerminalPermissions(context: android.content.Context, requireBluetooth: Boolean): Boolean {
    val locationGranted =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    if (!locationGranted) return false
    if (!requireBluetooth || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
    return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) ==
        PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) ==
        PackageManager.PERMISSION_GRANTED
}

@ScreenPreviews
@Composable
private fun TerminalHarnessIdlePreview() {
    PreviewSurface {
        TerminalHarnessScreen(
            state = TerminalHarnessUiState(
                enabled = true,
                locationId = "tml_preview",
                stubBaseUrl = "http://10.0.2.2:4242/",
            ),
            discovering = false,
            connecting = false,
            paying = false,
            testingStub = false,
            onBack = {},
            onToggleSimulated = {},
            onSelectReader = {},
            onTestStub = {},
            onDiscover = {},
            onConnect = {},
            onDisconnect = {},
            onPay = {},
            onCancelPayment = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun TerminalHarnessConnectedPreview() {
    PreviewSurface {
        TerminalHarnessScreen(
            state = TerminalHarnessUiState(
                enabled = true,
                phase = TerminalHarnessPhase.Connected,
                connectedSerial = "SIMULATED-001",
                readers = listOf(
                    ReaderInfo("1", "SIMULATED-001", "Simulated M2", "tml_sim"),
                ),
                selectedReaderId = "1",
                locationId = "tml_preview",
                stubBaseUrl = "http://10.0.2.2:4242/",
            ),
            discovering = false,
            connecting = false,
            paying = false,
            testingStub = false,
            onBack = {},
            onToggleSimulated = {},
            onSelectReader = {},
            onTestStub = {},
            onDiscover = {},
            onConnect = {},
            onDisconnect = {},
            onPay = {},
            onCancelPayment = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun TerminalHarnessDeclinedPreview() {
    PreviewSurface {
        TerminalHarnessScreen(
            state = TerminalHarnessUiState(
                enabled = true,
                phase = TerminalHarnessPhase.Declined,
                connectedSerial = "SIMULATED-001",
                statusText = UiText.Dynamic("Card declined"),
                locationId = "tml_preview",
                stubBaseUrl = "http://10.0.2.2:4242/",
            ),
            discovering = false,
            connecting = false,
            paying = false,
            testingStub = false,
            onBack = {},
            onToggleSimulated = {},
            onSelectReader = {},
            onTestStub = {},
            onDiscover = {},
            onConnect = {},
            onDisconnect = {},
            onPay = {},
            onCancelPayment = {},
        )
    }
}
