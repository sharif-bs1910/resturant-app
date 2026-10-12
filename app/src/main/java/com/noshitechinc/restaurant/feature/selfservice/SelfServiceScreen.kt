package com.noshitechinc.restaurant.feature.selfservice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme
import com.noshitechinc.restaurant.core.ui.AppScaffold
import com.noshitechinc.restaurant.feature.checkout.CheckoutOverlay
import com.noshitechinc.restaurant.feature.checkout.CheckoutOverlayHost
import com.noshitechinc.restaurant.feature.checkout.GiftField
import com.noshitechinc.restaurant.feature.checkout.MenuItem
import com.noshitechinc.restaurant.feature.checkout.ModifierGroup
import com.noshitechinc.restaurant.feature.checkout.OverlayActions
import com.noshitechinc.restaurant.feature.checkout.Settlement

@Composable
fun SelfServiceRoute(viewModel: SelfServiceViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val layoutDirection = LocalLayoutDirection.current
    AppScaffold(effects = viewModel.effects) { scaffoldPadding ->
        SelfServiceScreen(
            state = state,
            onSearch = viewModel::onSearch,
            onMeal = viewModel::onMeal,
            onCategory = viewModel::onCategory,
            onOrderType = viewModel::onOrderType,
            onOpenItem = viewModel::onOpenItem,
            onLineQuantity = viewModel::onLineQuantity,
            onRemoveLine = viewModel::onRemoveLine,
            onContinue = viewModel::onPlaceOrder,
            onGuestPhone = viewModel::onGuestPhone,
            onGuestName = viewModel::onGuestName,
            onTableNumber = viewModel::onTableNumber,
            onGuestContinue = viewModel::onGuestContinue,
            onToggleLine = viewModel::onToggleLineExpanded,
            onMakePayment = viewModel::onMakePayment,
            onRedeemGift = viewModel::onRedeemGiftCard,
            onBackToMenu = viewModel::onBackToMenu,
            onCancelPayment = viewModel::onCancelPayment,
            onTryAgain = viewModel::onTryPaymentAgain,
            onBackToOrder = viewModel::onBackToOrderFromDecline,
            onNewOrder = viewModel::onNewOrder,
            overlay = OverlayActions(
                onDismiss = viewModel::onDismissOverlay,
                onToggleOption = viewModel::onToggleOption,
                onDraftQuantity = viewModel::onDraftQuantity,
                onDraftNote = viewModel::onDraftNote,
                onAddToOrder = viewModel::onAddToOrder,
                onSettlement = {},
                onRedeemGiftCard = viewModel::onRedeemGiftCard,
                onSendPaymentLink = {},
                onGiftField = viewModel::onGiftField,
                onGiftDigit = viewModel::onGiftDigit,
                onGiftDelete = viewModel::onGiftDelete,
                onApplyGiftCard = viewModel::onApplyGiftCard,
                onBackToTender = viewModel::onBackFromGift,
                onBackToOrders = viewModel::onDismissOverlay,
                onContinueGiftCard = viewModel::onContinueGiftCard,
                onGiftAmountDigits = viewModel::onGiftAmountDigits,
                onGiftAmountDelete = viewModel::onGiftAmountDelete,
            ),
            // Top inset handled by SelfServiceTopBar (white under status bar); keep side/bottom.
            modifier = Modifier.padding(
                start = scaffoldPadding.calculateLeftPadding(layoutDirection),
                end = scaffoldPadding.calculateRightPadding(layoutDirection),
                bottom = scaffoldPadding.calculateBottomPadding(),
            ),
        )
    }
}

@Composable
fun SelfServiceScreen(
    state: SelfServiceUiState,
    onSearch: (String) -> Unit,
    onMeal: (SelfServiceMeal) -> Unit,
    onCategory: (SelfServiceCategory) -> Unit,
    onOrderType: (SelfServiceOrderType) -> Unit,
    onOpenItem: (MenuItem) -> Unit,
    onLineQuantity: (String, Int) -> Unit,
    onRemoveLine: (String) -> Unit,
    onContinue: () -> Unit,
    onGuestPhone: (String) -> Unit,
    onGuestName: (String) -> Unit,
    onTableNumber: (String) -> Unit,
    onGuestContinue: () -> Unit,
    onToggleLine: (String) -> Unit,
    onMakePayment: () -> Unit,
    onRedeemGift: () -> Unit,
    onBackToMenu: () -> Unit,
    onCancelPayment: () -> Unit,
    onTryAgain: () -> Unit,
    onBackToOrder: () -> Unit,
    onNewOrder: () -> Unit,
    overlay: OverlayActions,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Column(Modifier.fillMaxSize()) {
            SelfServiceTopBar()
            when (state.step) {
                SelfServiceStep.Menu -> SelfServiceMenuContent(
                    state = state,
                    onSearch = onSearch,
                    onMeal = onMeal,
                    onCategory = onCategory,
                    onOrderType = onOrderType,
                    onOpenItem = onOpenItem,
                    onLineQuantity = onLineQuantity,
                    onRemoveLine = onRemoveLine,
                    onContinue = onContinue,
                )

                SelfServiceStep.Guest -> SelfServiceGuestContent(
                    state = state,
                    onPhone = onGuestPhone,
                    onName = onGuestName,
                    onTable = onTableNumber,
                    onContinue = onGuestContinue,
                )

                SelfServiceStep.Review -> SelfServiceReviewContent(
                    state = state,
                    onToggleLine = onToggleLine,
                    onMakePayment = onMakePayment,
                    onRedeemGift = onRedeemGift,
                    onBack = onBackToMenu,
                )

                SelfServiceStep.PaymentWaiting,
                SelfServiceStep.PaymentProcessing,
                SelfServiceStep.PaymentDeclined,
                -> SelfServicePaymentContent(
                    state = state,
                    onCancel = onCancelPayment,
                    onTryAgain = onTryAgain,
                    onBackToOrder = onBackToOrder,
                )

                SelfServiceStep.Success -> SelfServiceSuccessContent(
                    state = state,
                    onNewOrder = onNewOrder,
                )
            }
        }
        val checkoutState = state.asCheckoutProjection()
        if (checkoutState.overlay != CheckoutOverlay.None) {
            CheckoutOverlayHost(checkoutState, overlay)
        }
    }
}

@ScreenPreviews
@Composable
private fun SelfServiceMenuScreenPreview() {
    PreviewSurface {
        SelfServiceScreen(
            state = SelfServiceUiState(lines = SelfServiceDemoCart),
            onSearch = {},
            onMeal = {},
            onCategory = {},
            onOrderType = {},
            onOpenItem = {},
            onLineQuantity = { _, _ -> },
            onRemoveLine = {},
            onContinue = {},
            onGuestPhone = {},
            onGuestName = {},
            onTableNumber = {},
            onGuestContinue = {},
            onToggleLine = {},
            onMakePayment = {},
            onRedeemGift = {},
            onBackToMenu = {},
            onCancelPayment = {},
            onTryAgain = {},
            onBackToOrder = {},
            onNewOrder = {},
            overlay = emptySelfOverlay(),
        )
    }
}

private fun emptySelfOverlay() = OverlayActions(
    onDismiss = {},
    onToggleOption = { _: ModifierGroup, _: String -> },
    onDraftQuantity = {},
    onDraftNote = {},
    onAddToOrder = {},
    onSettlement = { _: Settlement -> },
    onRedeemGiftCard = {},
    onSendPaymentLink = {},
    onGiftField = { _: GiftField -> },
    onGiftDigit = {},
    onGiftDelete = {},
    onApplyGiftCard = {},
    onBackToTender = {},
    onBackToOrders = {},
)
