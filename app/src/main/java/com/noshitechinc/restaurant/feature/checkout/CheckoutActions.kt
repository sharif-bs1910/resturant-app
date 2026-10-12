package com.noshitechinc.restaurant.feature.checkout

data class CheckoutActions(
    val onCategory: (MenuCategory) -> Unit,
    val onChannel: (OrderChannel) -> Unit,
    val onFulfillment: (Fulfillment) -> Unit,
    val onOpenItem: (MenuItem) -> Unit,
    val onLineQuantity: (String, Int) -> Unit,
    val onRemoveLine: (String) -> Unit,
    val onPay: () -> Unit,
    val onSaveDraft: () -> Unit,
    val onEditAddress: () -> Unit,
    val onEditCustomer: () -> Unit = {},
    val onBuilderSearch: (String) -> Unit = {},
)
