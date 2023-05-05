package com.example.nuttygala

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.OrdersModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.razorpay.*
import org.json.JSONObject
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class PaymentActivity : AppCompatActivity(), PaymentResultListener {

    lateinit var receivedDataFromFirestore: MutableList<CartModel>
    var arrayList: ArrayList<String>? = arrayListOf()
    var totalPrice = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        Checkout.preload(applicationContext)
        val co = Checkout()
        // apart from setting it in AndroidManifest.xml, keyId can also be set
        // programmatically during runtime
        co.setKeyID("rzp_test_Pc696tPHc59tp2")

        receivedDataFromFirestore = mutableListOf()


        val total = intent.extras
        arrayList = total?.getStringArrayList("data")
        if (arrayList?.get(0) == "fromAddToCart") {
            fetchCartData(arrayList?.get(2)!!)
            totalPrice = arrayList!![1].toInt()
            startPayment(arrayList!![1].toInt())
        }
        else {
            val imageUrl = arrayList?.get(1)
            val itemName = arrayList?.get(2)
            val itemCounter = arrayList?.get(3)
            val itemPrice = arrayList?.get(4)
            receivedDataFromFirestore.add(CartModel(
                itemImageUrl = imageUrl,
                itemName = itemName,
                itemCounter = itemCounter?.toInt(),
                itemPrice = itemPrice?.toInt()
            ))
            val counter = arrayList?.get(3)?.toInt()
            val price = arrayList?.get(4)?.toInt()
            totalPrice = price?.let { counter?.times(it) }!!
            startPayment(totalPrice)
        }

    }

    private fun startPayment(data: Int) {
        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val amount = data * 100
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Razorpay Corp")
            options.put("description","Demoing Charges")
            //You can omit the image option to fetch the image from the dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#3399cc");
            options.put("currency","INR");
            //options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount",amount)//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email","vishavjeetsingh348@gmail.com")
            prefill.put("contact","7889108186")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this,"Payment successful",Toast.LENGTH_SHORT).show()
        var email = ""
        email = if (arrayList?.get(0) == "fromAddToCart") {
            arrayList?.get(2)!!
        } else {
            arrayList?.get(5)!!
        }

        val db = Firebase.firestore
        val userDocRef = db.collection("users").document(email)
        val orderColRef = userDocRef.collection("orders")
        val orderId = UUID.randomUUID().toString()
        val date = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        val order = OrdersModel(
            orderId = orderId,
            items = receivedDataFromFirestore,
            total = totalPrice,
            date = date
        )

        orderColRef.document(orderId).set(order)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this,"Payment Unsuccessful",Toast.LENGTH_SHORT).show()
    }


    private fun fetchCartData(email: String) {

        val db = Firebase.firestore
        val userDocRef = db.collection("users").document(email)
        val cartDocRef = userDocRef.collection("cart")

        receivedDataFromFirestore.clear()
        cartDocRef.get().addOnCompleteListener { document ->
            if (document.isSuccessful) {
                for (doc in document.result) {
                    val itemName = doc.getString("itemName")
                    val itemImageUrl = doc.getString("itemImageUrl")
                    val itemPrice = doc.getLong("itemPrice")?.toInt()
                    val itemCounter = doc.getLong("itemCounter")?.toInt()
                    val cartItem = CartModel(
                        itemImageUrl = itemImageUrl,
                        itemName = itemName,
                        itemPrice = itemPrice,
                        itemCounter = itemCounter
                    )
                    receivedDataFromFirestore.add(cartItem)
                }
            }
        }
    }
}