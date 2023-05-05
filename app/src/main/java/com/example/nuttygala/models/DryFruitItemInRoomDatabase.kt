package com.example.nuttygala.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class DryFruitItemInRoomDatabase(
    @PrimaryKey
    val itemName: String,
    val itemImageUrl: String? = null,
    val itemMRP: String? = null,
    val itemRetailPrice: String? = null,
    val itemDescription: String? = null,
    val itemInTheCart: Boolean? = null
)
