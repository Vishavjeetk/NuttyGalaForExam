package com.example.nuttygala.epoxycontrollers

import android.text.Html
import android.util.Log
import android.view.View
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.Typed3EpoxyController
import com.example.nuttygala.R
import com.example.nuttygala.ViewBindingKotlinModel
import com.example.nuttygala.databinding.*
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.ReviewModel
import com.example.nuttygala.models.SingleItemModel
import com.squareup.picasso.Picasso

class SingleItemFragmentEpoxyController(
    private val onItemClicked: (String) -> Unit,
    private val onAddToCartClicked: (CartModel) -> Unit,
    private val onAddToCartOfMainModelClicked: (CartModel) -> Unit,
    private val onBuyItNowClicked: (String, String, String, String) -> Unit,
    private val onSubmitReviewClicked: (String, Float, String) -> Unit
) :
    Typed3EpoxyController<SingleItemModel, MutableList<SingleItemModel>,MutableList<ReviewModel>>() {

    data class ItemModel(
        val itemData: SingleItemModel,
        val onItemClicked: (String) -> Unit,
        val onAddToCartClicked: (CartModel) -> Unit
    ) : ViewBindingKotlinModel<FrontPageItemModelBinding>(R.layout.front_page_item_model) {
        override fun FrontPageItemModelBinding.bind() {
            Picasso.get().load(itemData.itemImageUrl?.ifEmpty { null })
                .into(frontPageItemModelImageView)
            frontPageItemModelTitleTextView.text = itemData.itemName
            ourPrice.text = "Rs.${ itemData.itemRetailPrice }.00"
            marketPrice.text = "Rs.${ itemData.itemMRP }.00"
            frontPageItemModelTitleTextView.setOnClickListener {
                onItemClicked(frontPageItemModelTitleTextView.text.toString())
            }

            val numerator = itemData.itemRetailPrice?.let { itemData.itemMRP?.minus(it) }
            val denominator = ((itemData.itemRetailPrice?.let { itemData.itemMRP?.plus(it) })?.div(2))?.toFloat()
            val divide = ((numerator?.div(denominator!!))?.times(100))?.toInt()
            discountView.text = "$divide% off"

            frontPageItemModelAddToCartButton.setOnClickListener {
                onAddToCartClicked(CartModel(
                    itemImageUrl = itemData.itemImageUrl,
                    itemName = itemData.itemName,
                    itemCounter = 1,
                    itemPrice = itemData.itemRetailPrice
                )
                )
            }
        }
    }

    data class ItemInformationModel(
        val information: SingleItemModel,
        val onAddToCartOfMainModelClicked: (CartModel) -> Unit,
        val onBuyItNowClicked: (String,String,String,String) -> Unit,
        val onSubmitReviewClicked: (String,Float,String) -> Unit
    ) : ViewBindingKotlinModel<SingleItemFragmentItemInformationBinding>(R.layout.single_item_fragment_item_information) {
        override fun SingleItemFragmentItemInformationBinding.bind() {
            Picasso.get().load(information.itemImageUrl?.ifEmpty { null })
                .into(singleItemFragmentItemImage)
            singleItemFragmentItemMarketPrice.text = "Rs.${ information.itemMRP }.00"
            singleItemFragmentItemRetailPrice.text = "Rs.${ information.itemRetailPrice }.00"
            singleItemFragmentItemDescription.text = Html.fromHtml(information.itemDescription)
            singleItemFragmentItemName.text = information.itemName

            singleItemFragmentBuyItNowButton.setOnClickListener {
                onBuyItNowClicked(
                    information.itemImageUrl!!,
                    information.itemName!!,
                    singleFragmentItemModelCounter.text.toString(),
                    information.itemRetailPrice.toString()
                )
            }

            submitReviewButton.setOnClickListener {
                if (productRatingStarReview.rating != 0f) {
                    information.itemName?.let { it1 ->
                        onSubmitReviewClicked(
                            it1,
                            productRatingStarReview.rating,
                            productRatingReview.text.toString()
                            )
                    }
                    productRatingStarReview.rating = 0f
                    productRatingReview.text.clear()
                }
            }


            val numerator = information.itemRetailPrice?.let { information.itemMRP?.minus(it) }
            val denominator = ((information.itemRetailPrice?.let { information.itemMRP?.plus(it) })?.div(2))?.toFloat()
            val divide = ((numerator?.div(denominator!!))?.times(100))?.toInt()
            discountView.text = "$divide% off"

            singleItemFragmentAddToCartButton.setOnClickListener {
                onAddToCartOfMainModelClicked(
                    CartModel(
                        itemImageUrl = information.itemImageUrl,
                        itemName = information.itemName,
                        itemPrice = information.itemRetailPrice,
                        itemCounter = singleFragmentItemModelCounter.text.toString().toInt()
                )
                )
            }

            singleItemFragmentPlusIcon.setOnClickListener {

                if (!singleItemFragmentMinusIcon.isEnabled) {
                    singleItemFragmentMinusIcon.isEnabled = true
                }
                singleFragmentItemModelCounter.text = singleFragmentItemModelCounter.text.toString().toInt().plus(1).toString()
            }

            singleItemFragmentMinusIcon.setOnClickListener {

                if (singleFragmentItemModelCounter.text.toString().toInt() == 1) {
                    singleItemFragmentMinusIcon.isEnabled = false
                }
                else {
                    singleFragmentItemModelCounter.text = singleFragmentItemModelCounter.text.toString().toInt().minus(1).toString()
                }
            }
        }
    }

    data class AllRatingsFromFirebaseModel(
        val reviewModel: ReviewModel?
    ): ViewBindingKotlinModel<AllRatingsFromFirebaseBinding>(R.layout.all_ratings_from_firebase) {
        override fun AllRatingsFromFirebaseBinding.bind() {
            if (reviewModel != null) {
                root.visibility = View.VISIBLE
                userNameHere.text = reviewModel.userName
                userRatingHere.rating = reviewModel.userRating!!
                userReviewHere.text = reviewModel.userReview
            }
        }

    }

    data class ReviewLabelModel(
        val label: String
    ): ViewBindingKotlinModel<StaicReviewLabelBinding>(R.layout.staic_review_label) {
        override fun StaicReviewLabelBinding.bind() {

        }

    }

    data class Footer(
        val name: String
    ): ViewBindingKotlinModel<FooterForAllFragmentsBinding>(R.layout.footer_for_all_fragments) {
        override fun FooterForAllFragmentsBinding.bind() {
        }
    }

    override fun buildModels(data1: SingleItemModel, data2: MutableList<SingleItemModel>,data3: MutableList<ReviewModel>) {
        Log.d("buildModelsData", "buildModels: $data1")

        ItemInformationModel(
            data1,
            onAddToCartOfMainModelClicked = onAddToCartOfMainModelClicked,
            onBuyItNowClicked = onBuyItNowClicked,
            onSubmitReviewClicked = onSubmitReviewClicked
            ).id("model-1").addTo(this)

        val itemModels = data2.map {
            ItemModel(
                it,
                onItemClicked = onItemClicked,
                onAddToCartClicked = onAddToCartClicked
            ).id(it.itemName)
        }
        CarouselModel_()
            .models(itemModels)
            .id("carousel-model")
            .numViewsToShowOnScreen(2f)
            .addTo(this)

        ReviewLabelModel("Label").id("label").addTo(this)

        for (review in data3) {
            AllRatingsFromFirebaseModel(review).id(review.userName).addTo(this)
        }

        Footer("Hello").id("footer-model").addTo(this)
    }

}