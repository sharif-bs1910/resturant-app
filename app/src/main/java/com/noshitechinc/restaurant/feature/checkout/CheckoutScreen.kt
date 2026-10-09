package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.adaptive.rememberAdaptiveInfo
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.ui.ObserveEffects
import com.noshitechinc.restaurant.core.ui.ShowMessage
import com.noshitechinc.restaurant.core.ui.UiEffect
import com.noshitechinc.restaurant.core.ui.asString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@Composable
fun CheckoutRoute(viewModel: CheckoutViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CheckoutScreen(
        state = state,
        effects = viewModel.effects,
        onOpenOrders = viewModel::onOpenOrders,
        onNewOrder = viewModel::onNewOrder,
        onSearch = viewModel::onSearch,
        onOpenOrder = viewModel::onOpenOrder,
        onPrintTicket = viewModel::onPrintTicket,
        onPrintReceipt = viewModel::onPrintReceipt,
        actions = CheckoutActions(
            onCategory = viewModel::onCategory,
            onChannel = viewModel::onChannel,
            onFulfillment = viewModel::onFulfillment,
            onOpenItem = viewModel::onOpenItem,
            onLineQuantity = viewModel::onLineQuantity,
            onRemoveLine = viewModel::onRemoveLine,
            onPay = viewModel::onPay,
            onSaveDraft = viewModel::onSaveDraft,
            onEditAddress = viewModel::onEditAddress,
            onEditCustomer = viewModel::onEditCustomer,
            onBuilderSearch = viewModel::onBuilderSearch,
        ),
        queue = QueueActions(
            onFilter = viewModel::onQueueFilter,
            onSearch = viewModel::onQueueSearch,
            onSelect = viewModel::onSelectQueueOrder,
            onClose = viewModel::onCloseQueue,
            onEdit = viewModel::onEditQueueOrder,
            onPay = viewModel::onPayQueueOrder,
            onGift = viewModel::onGiftFromQueue,
            onSave = viewModel::onSaveDraft,
        ),
        payment = PaymentActions(
            onSettlement = viewModel::onSettlement,
            onPayAmountMode = viewModel::onPayAmountMode,
            onAmountDigit = viewModel::onAmountDigit,
            onAmountDelete = viewModel::onAmountDelete,
            onCustomAmountDigits = viewModel::onCustomAmountDigits,
            onCashDigit = viewModel::onCashDigit,
            onCashDelete = viewModel::onCashDelete,
            onCashAmountDigits = viewModel::onCashAmountDigits,
            onCashTenderExact = viewModel::onCashTenderExact,
            onCashTenderRoundUp = viewModel::onCashTenderRoundUp,
            onRedeemGiftCard = viewModel::onRedeemGiftCard,
            onCollectPayment = viewModel::onCollectPayment,
            onBack = viewModel::onBackFromPayment,
            onSendPaymentLink = viewModel::onOpenPaymentLink,
            onTogglePayLine = viewModel::onTogglePayLine,
            onSelectAllPayLines = viewModel::onSelectAllPayLines,
            onClearItemCustomAmount = viewModel::onClearItemCustomAmount,
            onCardChargeCustom = viewModel::onCardChargeCustom,
            onTipOption = viewModel::onTipOption,
            onTipDigit = viewModel::onTipDigit,
            onTipDelete = viewModel::onTipDelete,
            onTipAmountDigits = viewModel::onTipAmountDigits,
            onDeliveryApp = viewModel::onDeliveryApp,
            onPlatformOrderId = viewModel::onPlatformOrderId,
        ),
        overlay = OverlayActions(
            onDismiss = viewModel::onBackToCart,
            onToggleOption = viewModel::onToggleOption,
            onDraftQuantity = viewModel::onDraftQuantity,
            onDraftNote = viewModel::onDraftNote,
            onAddToOrder = viewModel::onAddToOrder,
            onSettlement = viewModel::onSettlement,
            onRedeemGiftCard = viewModel::onRedeemGiftCard,
            onSendPaymentLink = viewModel::onSendPaymentLink,
            onGiftField = viewModel::onGiftField,
            onGiftDigit = viewModel::onGiftDigit,
            onGiftDelete = viewModel::onGiftDelete,
            onApplyGiftCard = viewModel::onApplyGiftCard,
            onBackToTender = viewModel::onBackToTender,
            onBackToOrders = viewModel::onClosePaymentSent,
            onStreet = viewModel::onStreet,
            onApt = viewModel::onApt,
            onZip = viewModel::onZip,
            onDeliveryNotes = viewModel::onDeliveryNotes,
            onKeepAsPickup = viewModel::onKeepAsPickup,
            onSaveAddress = viewModel::onSaveAddress,
            onCustomerName = viewModel::onCustomerName,
            onCustomerPhone = viewModel::onCustomerPhone,
            onSaveCustomer = viewModel::onSaveCustomer,
            onContinueGiftCard = viewModel::onContinueGiftCard,
            onGiftAmountDigit = viewModel::onGiftAmountDigit,
            onGiftAmountDigits = viewModel::onGiftAmountDigits,
            onGiftAmountDelete = viewModel::onGiftAmountDelete,
            onCity = viewModel::onCity,
            onRegion = viewModel::onRegion,
            onOpenPaymentLink = viewModel::onOpenPaymentLink,
            onEditPaymentLink = viewModel::onEditPaymentLink,
            onSavePaymentLinkDetails = viewModel::onSavePaymentLinkDetails,
        ),
        onSection = viewModel::onSection,
        menu = MenuActions(
            onFilter = viewModel::onMenuFilter,
            onSearch = viewModel::onMenuSearch,
            onSelect = viewModel::onSelectMenuItem,
            onName = viewModel::onMenuName,
            onDescription = viewModel::onMenuDescription,
            onPrice = viewModel::onMenuPrice,
            onStock = viewModel::onMenuStock,
            onStopTracking = viewModel::onStopTracking,
            onTrackInventory = viewModel::onTrackInventory,
            onAvailability = viewModel::onMenuAvailability,
            onUntil = viewModel::onUnavailableUntil,
            onConfirmUnavailable = viewModel::onConfirmUnavailable,
            onDismissUnavailable = viewModel::onDismissUnavailable,
            onSave = viewModel::onSaveMenuItem,
            onCancel = viewModel::onCancelMenuEdit,
        ),
        board = BoardActions(
            onFilter = viewModel::onBoardFilter,
            onSearch = viewModel::onBoardSearch,
            onSelect = viewModel::onSelectBoard,
            onToggleLine = viewModel::onToggleBoardLine,
            onMarkComplete = viewModel::onMarkBoardComplete,
            onToggleFulfillment = viewModel::onToggleBoardFulfillment,
            onClose = viewModel::onCloseBoardDetail,
            onAddCharge = viewModel::onAddCharge,
            onRefund = viewModel::onRefund,
            onCancel = viewModel::onCancelOrder,
            onDismissDialog = viewModel::onDismissBoardDialog,
            onChargeAmount = viewModel::onChargeAmount,
            onChargeReason = viewModel::onChargeReason,
            onRefundAmount = viewModel::onRefundAmount,
            onRefundReason = viewModel::onRefundReason,
            onBoardStreet = viewModel::onBoardStreet,
            onBoardApt = viewModel::onBoardApt,
            onBoardZip = viewModel::onBoardZip,
            onBoardNotes = viewModel::onBoardNotes,
            onConfirmCharge = viewModel::onConfirmCharge,
            onConfirmRefund = viewModel::onConfirmRefund,
            onConfirmCancel = viewModel::onConfirmCancel,
            onKeepPickup = viewModel::onKeepBoardPickup,
            onSaveAddress = viewModel::onSaveBoardAddress,
            onNewOrder = viewModel::onNewOrder,
        ),
        onSettings = viewModel::onSettingsChange,
        onKitchenMode = viewModel::onKitchenMode,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CheckoutScreen(
    state: CheckoutUiState,
    onOpenOrders: () -> Unit,
    onNewOrder: () -> Unit,
    onSearch: (String) -> Unit,
    onOpenOrder: (OpenOrder) -> Unit,
    actions: CheckoutActions,
    queue: QueueActions,
    payment: PaymentActions,
    overlay: OverlayActions,
    onSection: (KitchenSection) -> Unit,
    menu: MenuActions,
    board: BoardActions,
    onSettings: (SettingsState) -> Unit = {},
    onKitchenMode: (Boolean) -> Unit = {},
    onPrintTicket: () -> Unit = {},
    onPrintReceipt: () -> Unit = {},
    effects: Flow<UiEffect> = emptyFlow(),
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    ObserveEffects(effects) { effect ->
        if (effect is ShowMessage) {
            scope.launch { snackbarHostState.showSnackbar(effect.text.asString(context)) }
        }
    }
    val title = when (state.section) {
        KitchenSection.Orders -> R.string.checkout_nav_orders

        KitchenSection.Menu -> R.string.checkout_nav_menu

        KitchenSection.Settings -> R.string.checkout_nav_settings

        KitchenSection.Status -> R.string.checkout_kitchen_title

        KitchenSection.Checkout -> when (state.step) {
            CheckoutStep.Idle -> R.string.checkout_title
            CheckoutStep.Queue -> R.string.checkout_title
            CheckoutStep.Building -> R.string.checkout_new_order_title
            CheckoutStep.Payment -> R.string.checkout_payment_title
            CheckoutStep.Confirmed -> R.string.checkout_confirmed_title
        }
    }
    val adaptive = rememberAdaptiveInfo()
    Box(modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Row(Modifier.fillMaxSize()) {
            if (adaptive.usesTwoPane) {
                KitchenRail(section = state.section, kitchenBusy = state.kitchenBusy, onSection = onSection)
            }
            Column(Modifier.weight(1f)) {
                CheckoutTopBar(
                    title = stringResource(title),
                    onOpenOrders = onOpenOrders,
                    onNewOrder = onNewOrder,
                    ordersSelected = state.step != CheckoutStep.Building && state.step != CheckoutStep.Payment,
                    showActions = state.section == KitchenSection.Checkout,
                )
                if (!adaptive.usesTwoPane) {
                    FlowRow(
                        Modifier.padding(horizontal = AppTheme.spacing.xl, vertical = AppTheme.spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                    ) {
                        CheckoutChip(stringResource(R.string.checkout_nav_checkout), state.section == KitchenSection.Checkout) {
                            onSection(KitchenSection.Checkout)
                        }
                        CheckoutChip(stringResource(R.string.checkout_nav_orders), state.section == KitchenSection.Orders) {
                            onSection(KitchenSection.Orders)
                        }
                        CheckoutChip(stringResource(R.string.checkout_nav_menu), state.section == KitchenSection.Menu) {
                            onSection(KitchenSection.Menu)
                        }
                        CheckoutChip(stringResource(R.string.checkout_nav_settings), state.section == KitchenSection.Settings) {
                            onSection(KitchenSection.Settings)
                        }
                        CheckoutChip(stringResource(R.string.checkout_kitchen_title), state.section == KitchenSection.Status) {
                            onSection(KitchenSection.Status)
                        }
                    }
                }
                when (state.section) {
                    KitchenSection.Orders -> OrderBoardContent(state, board)

                    KitchenSection.Menu -> MenuContent(state, menu)

                    KitchenSection.Settings -> SettingsContent(state, onSettings)

                    KitchenSection.Status -> KitchenStatusContent(state, onKitchenMode)

                    KitchenSection.Checkout -> when (state.step) {
                        CheckoutStep.Idle -> IdleContent(state, onSearch, onNewOrder, onOpenOrder)

                        CheckoutStep.Queue -> QueueContent(state, queue)

                        CheckoutStep.Building -> BuilderContent(state, actions)

                        CheckoutStep.Payment -> PaymentContent(state, payment)

                        CheckoutStep.Confirmed -> ConfirmationContent(
                            state,
                            onNewOrder,
                            onPrintTicket = onPrintTicket,
                            onPrintReceipt = onPrintReceipt,
                        )
                    }
                }
            }
        }
        CheckoutOverlayHost(state, overlay)
        if (state.section == KitchenSection.Orders) BoardDialogHost(state, board)
        if (state.section == KitchenSection.Settings) SettingsDialogHost(state, onSettings)
        state.promptedMenuItem?.let { item ->
            UnavailableDialog(item.name, state.unavailableUntil, menu)
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(AppTheme.spacing.lg),
        )
    }
}

@ScreenPreviews
@Composable
private fun CheckoutIdlePreview() {
    PreviewSurface {
        CheckoutScreen(
            CheckoutUiState(),
            {},
            {},
            {},
            {},
            emptyActions(),
            emptyQueue(),
            emptyPayment(),
            emptyOverlay(),
            {},
            emptyMenuActions(),
            emptyBoard(),
        )
    }
}

@ScreenPreviews
@Composable
private fun CheckoutBuilderPreview() {
    PreviewSurface {
        CheckoutScreen(
            CheckoutUiState(step = CheckoutStep.Building, lines = SampleCart),
            {},
            {},
            {},
            {},
            emptyActions(),
            emptyQueue(),
            emptyPayment(),
            emptyOverlay(),
            {},
            emptyMenuActions(),
            emptyBoard(),
        )
    }
}

private fun emptyActions() = CheckoutActions({}, {}, {}, {}, { _, _ -> }, {}, {}, {}, {})

private fun emptyQueue() = QueueActions({}, {}, {}, {}, {}, {}, {}, {})

private fun emptyPayment() = PaymentActions({}, {}, {}, {}, {}, {}, {}, {}, {})

private fun emptyOverlay() = OverlayActions({}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})

private fun emptyBoard() = BoardActions({}, {}, {}, {}, {}, {}, {})
