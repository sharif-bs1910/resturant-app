package com.noshitechinc.restaurant.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.noshitechinc.restaurant.core.designsystem.preview.PreviewSurface
import com.noshitechinc.restaurant.core.designsystem.preview.ScreenPreviews
import com.noshitechinc.restaurant.core.designsystem.theme.AppTheme

@Composable
fun CheckoutOverlayHost(state: CheckoutUiState, actions: OverlayActions) {
    if (state.overlay == CheckoutOverlay.None) return
    val dismissable = state.overlay != CheckoutOverlay.Processing
    Box(
        Modifier
            .fillMaxSize()
            .background(AppTheme.colors.textPrimary.copy(alpha = 0.45f))
            .then(if (dismissable) Modifier.clickable(onClick = actions.onDismiss) else Modifier)
            .padding(AppTheme.spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.clickable(enabled = dismissable, onClick = {})) {
            when (state.overlay) {
                CheckoutOverlay.Modifiers -> state.draft?.let { ModifierSheet(it, actions) }
                CheckoutOverlay.Tender -> TenderSheet(state, actions)
                CheckoutOverlay.GiftCard -> GiftCardSheet(state, actions)
                CheckoutOverlay.GiftAmount -> GiftAmountSheet(state, actions)
                CheckoutOverlay.Address -> AddressSheet(state, actions)
                CheckoutOverlay.Draft -> DraftSheet(state, actions)
                CheckoutOverlay.Saved -> SavedSheet(state, actions.onBackToOrders)
                CheckoutOverlay.Customer -> CustomerSheet(state, actions)
                CheckoutOverlay.PaymentLink -> PaymentLinkSheet(state, actions)
                CheckoutOverlay.PaymentLinkEdit -> PaymentLinkEditSheet(state, actions)
                CheckoutOverlay.Processing -> ProcessingOverlay()
                CheckoutOverlay.None -> Unit
            }
        }
    }
}

@ScreenPreviews
@Composable
private fun DraftPreview() {
    PreviewSurface {
        DraftSheet(
            CheckoutUiState(step = CheckoutStep.Building, lines = PhoneCart, channel = OrderChannel.Phone),
            emptyOverlay(),
        )
    }
}

@ScreenPreviews
@Composable
private fun AddressPreview() {
    PreviewSurface {
        AddressSheet(
            CheckoutUiState(overlay = CheckoutOverlay.Address, fulfillment = Fulfillment.Delivery),
            emptyOverlay(),
        )
    }
}

private fun emptyOverlay() = OverlayActions({}, { _, _ -> }, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})
