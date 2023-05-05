package com.example.nuttygala.epoxycontrollers

import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.TypedEpoxyController
import com.example.nuttygala.R
import com.example.nuttygala.ViewBindingKotlinModel
import com.example.nuttygala.databinding.FrontPageItemModelBinding
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.SingleItemModel
import com.squareup.picasso.Picasso

class AllProductsFragmentEpoxyController(
    private val onItemClicked: (String) -> Unit,
    private val onAddToCartClicked: (CartModel) -> Unit
): TypedEpoxyController<MutableList<SingleItemModel>>() {
    override fun buildModels(data: MutableList<SingleItemModel>?) {
        if (data != null) {
            for (index in data.indices step 2) {

                val twoDataItems = mutableListOf(data[index], data[index + 1])
                val itemModels = twoDataItems.map {
                    FrontPageEpoxyController.ItemModel(
                        it,
                        onItemClicked = onItemClicked,
                        onAddToCartClicked = onAddToCartClicked
                    ).id(it.itemName)
                }
                CarouselModel_()
                    .models(itemModels)
                    .id(index)
                    .numViewsToShowOnScreen(2f)
                    .addTo(this)

            }
        }
    }

    data class ItemModel(
        val itemData: SingleItemModel,
        val onItemClicked: (String) -> Unit,
        val onAddToCartClicked: (CartModel) -> Unit
    ) : ViewBindingKotlinModel<FrontPageItemModelBinding>(R.layout.front_page_item_model) {
        override fun FrontPageItemModelBinding.bind() {
            Picasso.get().load(itemData.itemImageUrl)
                .into(frontPageItemModelImageView)
            frontPageItemModelTitleTextView.text = itemData.itemName
            ourPrice.text = "Rs.${itemData.itemRetailPrice}.00"
            marketPrice.text = "Rs.${itemData.itemMRP}.00"
            frontPageItemModelTitleTextView.setOnClickListener {
                onItemClicked(frontPageItemModelTitleTextView.text.toString())
            }
            frontPageItemModelImageView.setOnClickListener {
                onItemClicked(frontPageItemModelTitleTextView.text.toString())
            }
            if (itemData.itemAvailableInStock == true) {
                frontPageItemModelAddToCartButton.isEnabled = true
                val numerator = itemData.itemRetailPrice?.let { itemData.itemMRP?.minus(it) }
                val denominator =
                    ((itemData.itemRetailPrice?.let { itemData.itemMRP?.plus(it) })?.div(2))?.toFloat()
                val divide = ((numerator?.div(denominator!!))?.times(100))?.toInt()
                discountView.text = "$divide% off"
            } else {
                discountView.text = "Sold Out"
                frontPageItemModelAddToCartButton.isEnabled = false
            }

            //This will update the content in cartSubCollection
            frontPageItemModelAddToCartButton.setOnClickListener {
                onAddToCartClicked(
                    CartModel(
                        itemImageUrl = itemData.itemImageUrl,
                        itemName = itemData.itemName,
                        itemCounter = 1,
                        itemPrice = itemData.itemMRP
                    )
                )
            }
        }
    }
}