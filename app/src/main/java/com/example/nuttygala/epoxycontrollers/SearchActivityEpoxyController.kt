package com.example.nuttygala.epoxycontrollers

import com.airbnb.epoxy.TypedEpoxyController
import com.example.nuttygala.R
import com.example.nuttygala.ViewBindingKotlinModel
import com.example.nuttygala.databinding.SearchActivityItemModelBinding
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.SingleItemModel
import com.squareup.picasso.Picasso

class SearchActivityEpoxyController(
    private val onItemClicked: (String) -> Unit,
    private val onAddToCartClicked: (CartModel) -> Unit
): TypedEpoxyController<MutableList<SingleItemModel>>() {
    override fun buildModels(data: MutableList<SingleItemModel>?) {
        if (data != null) {
            for (eachData in data) {
                SearchActivityItemModel(
                    eachData,
                    onItemClicked = onItemClicked,
                    onAddToCartClicked = onAddToCartClicked
                )
                    .id(eachData.itemName)
                    .addTo(this)
            }
        }
    }


    data class SearchActivityItemModel(
        val data: SingleItemModel,
        val onItemClicked: (String) -> Unit,
        val onAddToCartClicked: (CartModel) -> Unit
    ): ViewBindingKotlinModel<SearchActivityItemModelBinding>(R.layout.search_activity_item_model) {
        override fun SearchActivityItemModelBinding.bind() {
            Picasso.get().load(data.itemImageUrl?.ifEmpty { null })
                .into(searchActivityImageView)
            searchActivityItemName.text = data.itemName
            searchActivityMarketPrice.text = "Rs.${data.itemMRP}.00"
            searchActivityRetailPrice.text = "Rs.${data.itemRetailPrice}.00"

            root.setOnClickListener {
                data.itemName?.let { it1 -> onItemClicked(it1) }
            }

            searchActivityAddToCartButton.setOnClickListener {
                onAddToCartClicked(CartModel(
                    itemImageUrl = data.itemImageUrl,
                    itemName = data.itemName,
                    itemCounter = 1,
                    itemPrice = data.itemMRP
                ))
            }
        }
    }

}