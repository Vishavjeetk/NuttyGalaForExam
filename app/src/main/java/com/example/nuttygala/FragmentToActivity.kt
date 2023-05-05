package com.example.nuttygala

import com.example.nuttygala.models.CartModel

interface FragmentToActivity {
    fun addToCartOfFragmentToActivity(data: CartModel?)
    fun deleteFromCartOfFragmentToActivity(itemName: String)
}