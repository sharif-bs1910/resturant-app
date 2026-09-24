package com.noshitechinc.restaurant.core.common.model

data class PostalAddress(
    val street: String = "",
    val unit: String = "",
    val city: String = "",
    val state: String = "",
    val zip: String = "",
)
