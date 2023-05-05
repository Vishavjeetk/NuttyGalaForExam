package com.example.nuttygala.epoxycontrollers

import com.airbnb.epoxy.TypedEpoxyController
import com.example.nuttygala.R
import com.example.nuttygala.ViewBindingKotlinModel
import com.example.nuttygala.databinding.DividerBinding
import com.example.nuttygala.databinding.OrderDetailsModelBinding
import com.example.nuttygala.databinding.OrderIdLabelModelBinding
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.OrdersModel
import com.squareup.picasso.Picasso

class MyOrdersFragmentEpoxyController: TypedEpoxyController<MutableList<OrdersModel>>() {
    override fun buildModels(data: MutableList<OrdersModel>?) {
        if (data != null) {
            for (order in data) {
                OrderLabelModel("","").id(order.orderId).addTo(this)
                order.orderId?.let { OrderLabelModel("Order Id:", it) }?.id(order.orderId)?.addTo(this)
                OrderLabelModel("Total Amount:", "Rs.${ order.total }.00").id(order.orderId).addTo(this)
                OrderLabelModel("Date(YYYY-MM-DD):",order.date.toString().dropLast(17)).id(order.orderId).addTo(this)
                for (detail in order.items!!) {
                    OrderDetailsModel(
                        detail
                    ).id(detail.itemName).addTo(this)
                }
                Divider("Hello").id(order.orderId).addTo(this)
            }
        }
    }

    data class Divider(
        val name: String
    ): ViewBindingKotlinModel<DividerBinding>(R.layout.divider){
        override fun DividerBinding.bind() {

        }

    }


    data class OrderLabelModel(
        val label: String,
        val labelValue: String
    ): ViewBindingKotlinModel<OrderIdLabelModelBinding>(R.layout.order_id_label_model) {
        override fun OrderIdLabelModelBinding.bind() {
            insertOrderLabelHere.text = label
            insertOrderIdHere.text = labelValue
        }
    }

    data class OrderDetailsModel(
        val itemData: CartModel
    ): ViewBindingKotlinModel<OrderDetailsModelBinding>(R.layout.order_details_model) {
        override fun OrderDetailsModelBinding.bind() {
            Picasso.get().load(itemData.itemImageUrl).into(cartPageItemModelImageView)
            insertItemNameOfOrderHere.text = itemData.itemName
            insertItemPriceOfOrderHere.text = "Rs.${ itemData.itemPrice }.00"
            insertItemCounterOfOrderHere.text = itemData.itemCounter.toString()
        }

    }

}