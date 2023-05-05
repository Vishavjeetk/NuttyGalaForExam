package com.example.nuttygala.models

data class OrdersModel(
    val orderId: String? = null,
    val items: List<CartModel>? = null,
    val total: Int? = null,
    val date: Any? = null
)
