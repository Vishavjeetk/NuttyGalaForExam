package com.example.nuttygala.epoxycontrollers

import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.TypedEpoxyController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.nuttygala.R
import com.example.nuttygala.ViewBindingKotlinModel
import com.example.nuttygala.databinding.FooterForAllFragmentsBinding
import com.example.nuttygala.databinding.FrontPageItemModelBinding
import com.example.nuttygala.databinding.ImageSliderModelBinding
import com.example.nuttygala.databinding.NewArrivalModelBinding
import com.example.nuttygala.databinding.OurBenefitsModelBinding
import com.example.nuttygala.databinding.UserNameModelBinding
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.SingleItemModel
import com.squareup.picasso.Picasso

class FrontPageEpoxyController(
    private val onItemClicked: (String) -> Unit,
    private val onAddToCartClicked: (CartModel) -> Unit,
    private val onViewAllProductsClicked: () -> Unit
) : TypedEpoxyController<MutableList<SingleItemModel>>() {
    override fun buildModels(data: MutableList<SingleItemModel>) {

        UserNameModel("Vishavjeet Singh").id("username-1").addTo(this)
        ImageSliderModel("1", "2", "3").id("image-slider-1").addTo(this)
        NewArrivalModel("Hey", onViewAllProductsClicked = onViewAllProductsClicked).id("new-arrival-1").addTo(this)
        for (index in data.indices step 2) {

            val twoDataItems = mutableListOf(data[index], data[index + 1])
            val itemModels = twoDataItems.map {
                ItemModel(
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
        OurBenefits("Hello").id("benefits-model").addTo(this)
        Footer("Hello").id("footer-model").addTo(this)
    }

    data class Footer(
        val name: String
    ): ViewBindingKotlinModel<FooterForAllFragmentsBinding>(R.layout.footer_for_all_fragments) {
        override fun FooterForAllFragmentsBinding.bind() {
        }
    }

    data class OurBenefits(
        val name: String
    ): ViewBindingKotlinModel<OurBenefitsModelBinding>(R.layout.our_benefits_model) {
        override fun OurBenefitsModelBinding.bind() {

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
            ourPrice.text = "Rs.${ itemData.itemRetailPrice }.00"
            marketPrice.text = "Rs.${ itemData.itemMRP }.00"
            frontPageItemModelTitleTextView.setOnClickListener {
                onItemClicked(frontPageItemModelTitleTextView.text.toString())
            }
            frontPageItemModelImageView.setOnClickListener {
                onItemClicked(frontPageItemModelTitleTextView.text.toString())
            }
            if (itemData.itemAvailableInStock == true) {
                frontPageItemModelAddToCartButton.isEnabled = true
                val numerator = itemData.itemRetailPrice?.let { itemData.itemMRP?.minus(it) }
                val denominator = ((itemData.itemRetailPrice?.let { itemData.itemMRP?.plus(it) })?.div(2))?.toFloat()
                val divide = ((numerator?.div(denominator!!))?.times(100))?.toInt()
                discountView.text = "$divide% off"
            }
            else {
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

    data class UserNameModel(
        val name: String
    ) : ViewBindingKotlinModel<UserNameModelBinding>(R.layout.user_name_model) {
        override fun UserNameModelBinding.bind() {
            frontPageUserName.text = name
        }
    }

    data class ImageSliderModel(
        val firstImageUrl: String,
        val secondImageUrl: String,
        val thirdImageUrl: String
    ) : ViewBindingKotlinModel<ImageSliderModelBinding>(R.layout.image_slider_model) {
        override fun ImageSliderModelBinding.bind() {
            val imageList = ArrayList<SlideModel>()
            imageList.add(
                SlideModel(
                    "https://cdn.shopify.com/s/files/1/0615/2450/8915/files/A-Source-To-Live-A-Healthy-And-Happy-Life_db648727-227a-4ddb-9363-33cc64747fe0.jpg?v=1663244599\"",
                    ScaleTypes.FIT
                )
            )
            imageList.add(
                SlideModel(
                    "https://cdn.shopify.com/s/files/1/0615/2450/8915/files/4.jpg?v=1663243498",
                    ScaleTypes.FIT
                )
            )
            imageList.add(
                SlideModel(
                    "https://cdn.shopify.com/s/files/1/0615/2450/8915/files/Premium-Quality-Products_92704d07-7a29-40f7-b6c5-dd5f94c843ba.jpg?v=1663244599",
                    ScaleTypes.FIT
                )
            )

            imageSlider.setImageList(imageList)
        }
    }

    data class NewArrivalModel(
        val name: String,
        val onViewAllProductsClicked: () -> Unit
    ) : ViewBindingKotlinModel<NewArrivalModelBinding>(R.layout.new_arrival_model) {
        override fun NewArrivalModelBinding.bind() {
            viewAllProducts.setOnClickListener {
                onViewAllProductsClicked()
            }
        }
    }

}