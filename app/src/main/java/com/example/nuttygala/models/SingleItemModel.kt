package com.example.nuttygala.models

data class SingleItemModel(
    val itemName: String? = null,
    val itemImageUrl: String? = null,
    val itemMRP: Int? = null,
    val itemRetailPrice: Int? = null,
    val itemAvailableInStock: Boolean? = false,
    val itemDescription: String? = null,
)
