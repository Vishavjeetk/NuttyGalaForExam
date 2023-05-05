package com.example.nuttygala.epoxycontrollers

import android.os.Handler
import android.os.Looper
import com.airbnb.epoxy.TypedEpoxyController
import com.example.nuttygala.R
import com.example.nuttygala.ViewBindingKotlinModel
import com.example.nuttygala.databinding.ItemInCartModelBinding
import com.example.nuttygala.models.CartModel
import com.squareup.picasso.Picasso

class CartFragmentEpoxyController(
    private val onDeleteClicked: (String) -> Unit,
    private val onCounterButtonsClicked: (String, String) -> Unit
):
    TypedEpoxyController<MutableList<CartModel>>() {
    override fun buildModels(data: MutableList<CartModel>?) {
        if (data != null) {
            for (eachData in data) {
                CartItemModel(
                    itemImageUrl = eachData.itemImageUrl,
                    itemName = eachData.itemName,
                    itemRetailPrice = eachData.itemPrice,
                    itemCounter = eachData.itemCounter,
                    onDeleteClicked = onDeleteClicked,
                    onCounterButtonsClicked = onCounterButtonsClicked
                )
                    .id(eachData.itemName)
                    .addTo(this)
            }
        }
    }

    data class CartItemModel(
        val itemImageUrl: String? = null,
        val itemName: String? = null,
        val itemRetailPrice: Int? = null,
        val itemCounter: Int? = 1,
        val onDeleteClicked: (String) -> Unit,
        val onCounterButtonsClicked: (String,String) -> Unit
    ): ViewBindingKotlinModel<ItemInCartModelBinding>(R.layout.item_in_cart_model) {
        override fun ItemInCartModelBinding.bind() {
            Picasso.get().load(itemImageUrl?.ifEmpty { null })
                .into(cartPageItemModelImageView)
            cartPageItemModelNameTextView.text = itemName
            cartPageItemModelRetailPrice.text = "Rs.${itemRetailPrice}.00"
            cartPageItemModelCounter.text = itemCounter.toString()
            val totalPrice = itemCounter?.let { itemRetailPrice?.times(it) }
            cartPageItemModelNameTotalPrice.text = "Total: Rs.${ totalPrice }.00"
            cartFragmentPlusIcon.setOnClickListener {

                if (!cartFragmentMinusIcon.isEnabled) {
                    cartFragmentMinusIcon.isEnabled = true
                }
                onCounterButtonsClicked(
                    cartPageItemModelNameTextView.text.toString(),
                    "Plus"
                )
                cartPageItemModelCounter.text = cartPageItemModelCounter.text.toString().toInt().plus(1).toString()
                cartFragmentPlusIcon.isEnabled = false
                cartFragmentMinusIcon.isEnabled = false

                Handler(Looper.getMainLooper()).postDelayed({
                    //Do something after 100ms
                    cartFragmentPlusIcon.isEnabled = true
                    cartFragmentMinusIcon.isEnabled = true
                }, 1000)
                val totalPrice =
                    itemRetailPrice?.let { cartPageItemModelCounter.text.toString().toInt().times(it) }
                cartPageItemModelNameTotalPrice.text = "Total: Rs.${ totalPrice }.00"

            }

            cartFragmentMinusIcon.setOnClickListener {

                if (cartPageItemModelCounter.text.toString().toInt() == 1) {
                    cartFragmentMinusIcon.isEnabled = false
                }
                else {
                    onCounterButtonsClicked(
                        cartPageItemModelNameTextView.text.toString(),
                        "Minus"
                    )
                    cartPageItemModelCounter.text = cartPageItemModelCounter.text.toString().toInt().minus(1).toString()
                    cartFragmentMinusIcon.isEnabled = false
                    cartFragmentPlusIcon.isEnabled = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        //Do something after 100ms
                        cartFragmentMinusIcon.isEnabled = true
                        cartFragmentPlusIcon.isEnabled = true
                    }, 1000)
                }
                val totalPrice =
                    itemRetailPrice?.let { cartPageItemModelCounter.text.toString().toInt().times(it) }
                cartPageItemModelNameTotalPrice.text = "Total: Rs.${ totalPrice }.00"
            }

            cartFragmentDeleteIcon.setOnClickListener {
                onDeleteClicked(cartPageItemModelNameTextView.text.toString())
            }

        }

    }
}