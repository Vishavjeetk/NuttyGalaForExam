package com.example.nuttygala.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nuttygala.FragmentToActivity
import com.example.nuttygala.PaymentActivity
import com.example.nuttygala.databinding.FragmentCartBinding
import com.example.nuttygala.epoxycontrollers.CartFragmentEpoxyController
import com.example.nuttygala.models.CartModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CartFragment : Fragment() {


    private lateinit var binding: FragmentCartBinding


    private var mCallback: FragmentToActivity? = null
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var receivedDataFromFirestore: MutableList<CartModel>
    lateinit var epoxyController: CartFragmentEpoxyController
    var mutableListOfSubtotalValue = mutableListOf<CartModel>()
    var account: GoogleSignInAccount? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mCallback = context as FragmentToActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString()
                        + " must implement FragmentToActivity"
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(layoutInflater)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("1059667677594-ucidp245f7pee8vd8t16eq64i337mcgd.apps.googleusercontent.com")
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        account = GoogleSignIn.getLastSignedInAccount(requireContext())


        epoxyController = CartFragmentEpoxyController(
            ::onDeleteClicked,
            ::onCounterButtonsClicked
        )
        binding.epoxyView.setController(epoxyController)
        receivedDataFromFirestore = mutableListOf<CartModel>()

        //This Code Will Fetch Cart Data for calculating subtotal value
        val email = account?.email
        val db = Firebase.firestore
        val userDocRef = email?.let { db.collection("users").document(it) }
        val cartDocRef = userDocRef?.collection("cart")

        var subtotalValue = 0

        mutableListOfSubtotalValue.clear()
        cartDocRef?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            mutableListOfSubtotalValue = mutableListOf()

            for (doc in snapshot!!.documents) {
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
                mutableListOfSubtotalValue.add(cartItem)
            }
            subtotalValue = 0
            for (each in mutableListOfSubtotalValue) {
                subtotalValue += each.itemPrice?.let { each.itemCounter?.times(it) }!!
            }
            binding.cartFragmentSubtotalValue.text = "Rs.${subtotalValue}.00"
            binding.proceedToBuy.text = "Proceed To Buy (${mutableListOfSubtotalValue.size}) Items."
        }

        /*val db = Firebase.firestore
        val email = account?.email
        if (email != null) {
            db.collection("users")
                .document(email)
                .addSnapshotListener(EventListener<DocumentSnapshot?> { snapshot, e ->
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e)
                        return@EventListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        receivedDataFromFirestore.clear()
                        //snapshot.data?.get("itemsInTheCart")
                        Log.d("TAG", "Current data: " + snapshot.data)
                        val size: List<Any?> =
                            snapshot.data?.get("itemsInCart") as List<Any?>

                        db.collection("allDryFruitsProducts")
                            .whereIn("itemName", size)
                            .get()
                            .addOnCompleteListener { task1 ->
                                if (task1.isSuccessful) {
                                    for (document in task1.result) {
                                        Log.d("TAG", "onCreateView: ${task1.result.size()}")
                                        Log.d("TAG", document.id + " => " + document.data)
                                        receivedDataFromFirestore.add(
                                            document.toObject(
                                                SingleItemModel::class.java
                                            )
                                        )
                                    }
                                    binding.epoxyView.setController(epoxyController)
                                    epoxyController.setData(receivedDataFromFirestore)
                                } else {
                                    Log.d("TAG", "Error getting documents: ")
                                }
                            }
                    } else {
                        Log.d("TAG", "Current data: null")
                    }
                })          */
        /*val size: List<Any?> =
                            task.result.data?.get("itemsInCart") as List<Any?>

                        db.collection("allDryFruitsProducts")
                            .whereIn("itemName",size)
                            .get()
                            .addOnCompleteListener { task1 ->
                                if (task1.isSuccessful) {
                                    for (document in task1.result) {
                                        Log.d("TAG", "onCreateView: ${task1.result.size()}")
                                        Log.d("TAG", document.id + " => " + document.data)
                                        receivedDataFromFirestore.add(document.toObject(
                                            SingleItemModel::class.java))
                                    }
                                    binding.epoxyView.setController(epoxyController)
                                    epoxyController.setData(receivedDataFromFirestore)
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.exception)
                                }
                            }*//*

        } else {
            Log.d("TAG", "Error getting documents: ")
        }*/

        //This will get data from cartSubCollection

        fetchDataAgain()

        binding.proceedToBuy.setOnClickListener {
            if (subtotalValue != 0) {
                Log.d("SubtotalValue", "onCreateView: $subtotalValue")
                val intent = Intent(requireContext(), PaymentActivity::class.java)
                intent.putStringArrayListExtra(
                    "data",
                    arrayListOf("fromAddToCart", subtotalValue.toString(),account?.email)
                )
                startActivity(intent)
            }
            else {
                Toast.makeText(requireContext(),"Nothing In The Cart",Toast.LENGTH_SHORT).show()
            }
        }



        /*cartDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            receivedDataFromFirestore = mutableListOf()

            for (doc in snapshot!!.documents) {
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
            binding.epoxyView.setController(epoxyController)
            epoxyController.setData(receivedDataFromFirestore)
        }*/

        return binding.root
    }

    private fun onCounterButtonsClicked(itemName: String, action: String) {
        val email = account?.email
        val db = Firebase.firestore
        val userDocRef = email?.let { db.collection("users").document(it) }
        val cartDocRef = userDocRef?.collection("cart")?.document(itemName)
        if (action == "Plus") {

            cartDocRef?.get()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentCounter = it.result.getLong("itemCounter")?.toInt()
                    cartDocRef.update("itemCounter",currentCounter?.plus(1))
                }
            }

        }
        else {
            cartDocRef?.get()?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentCounter = it.result.getLong("itemCounter")?.toInt()
                    cartDocRef.update("itemCounter",currentCounter?.minus(1))
                }
            }
        }
    }

    private fun onDeleteClicked(itemName: String) {
        sendData(itemName)
        fetchDataAgain()
        /*val currentFragmentId = findNavController().currentDestination?.id
        findNavController().popBackStack(currentFragmentId!!,true)
        findNavController().navigate(currentFragmentId)*/
    }

    private fun fetchDataAgain() {

        val email = account?.email
        val db = Firebase.firestore
        val userDocRef = email?.let { db.collection("users").document(it) }
        val cartDocRef = userDocRef?.collection("cart")

        receivedDataFromFirestore.clear()
        cartDocRef?.get()!!.addOnCompleteListener { document ->
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
                binding.epoxyView.setController(epoxyController)
                epoxyController.setData(receivedDataFromFirestore)
            }
        }
    }

    override fun onDetach() {
        mCallback = null
        super.onDetach()
    }

    fun onRefresh() {
        Toast.makeText(
            activity, "Fragment : Refresh called.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun sendData(itemName: String): Boolean {
        mCallback!!.deleteFromCartOfFragmentToActivity(itemName)
        return true
    }


}