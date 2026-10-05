package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noshitechinc.restaurant.R
import com.noshitechinc.restaurant.core.adaptive.rememberAdaptiveInfo
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun CheckoutRoute(viewModel: CheckoutViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CheckoutScreen(
        state = state,
        onOpenOrders = viewModel::onOpenOrders,
        onNewOrder = viewModel::onNewOrder,
        onSearch = viewModel::onSearch,
        onOpenOrder = viewModel::onOpenOrder,
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
        ),
    )
}

@Composable
fun CheckoutScreen(
    state: CheckoutUiState,
    onOpenOrders: () -> Unit,
    onNewOrder: () -> Unit,
    onSearch: (String) -> Unit,
    onOpenOrder: (OpenOrder) -> Unit,
    actions: CheckoutActions,
    queue: QueueActions,
    overlay: OverlayActions,
    onSection: (KitchenSection) -> Unit,
    menu: MenuActions,
    board: BoardActions,
    modifier: Modifier = Modifier,
) {
    val title = when (state.section) {
        KitchenSection.Orders -> R.string.checkout_nav_orders

        KitchenSection.Menu -> R.string.checkout_nav_menu

        KitchenSection.Checkout -> when (state.step) {
            CheckoutStep.Idle -> R.string.checkout_title
            CheckoutStep.Queue -> R.string.checkout_title
            CheckoutStep.Building -> R.string.checkout_new_order_title
            CheckoutStep.Confirmed -> R.string.checkout_confirmed_title
        }
    }
    val adaptive = rememberAdaptiveInfo()
    Box(modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Row(Modifier.fillMaxSize()) {
            if (adaptive.usesTwoPane) KitchenRail(section = state.section, onSection = onSection)
            Column(Modifier.weight(1f)) {
                CheckoutTopBar(
                    title = stringResource(title),
                    onOpenOrders = onOpenOrders,
                    onNewOrder = onNewOrder,
                    ordersSelected = state.step != CheckoutStep.Building,
                    showActions = state.section == KitchenSection.Checkout,
                )
                if (!adaptive.usesTwoPane) {
                    Row(
                        Modifier.padding(horizontal = AppTheme.spacing.xl, vertical = AppTheme.spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
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
                    }
                }
                when (state.section) {
                    KitchenSection.Orders -> OrderBoardContent(state, board)

                    KitchenSection.Menu -> MenuContent(state, menu)

                    KitchenSection.Checkout -> when (state.step) {
                        CheckoutStep.Idle -> IdleContent(state, onSearch, onNewOrder, onOpenOrder)
                        CheckoutStep.Queue -> QueueContent(state, queue)
                        CheckoutStep.Building -> BuilderContent(state, actions)
                        CheckoutStep.Confirmed -> ConfirmationContent(state, onNewOrder)
                    }
                }
            }
        }
        CheckoutOverlayHost(state, overlay)
        state.promptedMenuItem?.let { item ->
            UnavailableDialog(item.name, state.unavailableUntil, menu)
        }
    }
}

@ScreenPreviews
@Composable
private fun CheckoutIdlePreview() {
    PreviewSurface {
        CheckoutScreen(CheckoutUiState(), {
        }, {}, {}, {}, emptyActions(), emptyQueue(), emptyOverlay(), {}, emptyMenuActions(), emptyBoard())
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
            emptyOverlay(),
            {},
            emptyMenuActions(),
            emptyBoard(),
        )
    }
}

private fun emptyActions() = CheckoutActions({}, {}, {}, {}, { _, _ -> }, {}, {}, {}, {})

private fun emptyQueue() = QueueActions({}, {}, {}, {}, {}, {}, {}, {})

private fun emptyOverlay() = OverlayActions({}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})

private fun emptyBoard() = BoardActions({}, {}, {}, {}, {}, {}, {})
