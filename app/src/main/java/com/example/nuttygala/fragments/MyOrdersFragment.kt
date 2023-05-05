package com.example.nuttygala.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nuttygala.databinding.FragmentMyOrdersBinding
import com.example.nuttygala.epoxycontrollers.MyOrdersFragmentEpoxyController
import com.example.nuttygala.models.OrdersModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyOrdersFragment : Fragment() {

    private lateinit var binding: FragmentMyOrdersBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    var account: GoogleSignInAccount? = null
    lateinit var receivedOrderDataFromFirebase: MutableList<OrdersModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyOrdersBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        val epoxyController = MyOrdersFragmentEpoxyController()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("1059667677594-ucidp245f7pee8vd8t16eq64i337mcgd.apps.googleusercontent.com")
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        account = GoogleSignIn.getLastSignedInAccount(requireContext())

        receivedOrderDataFromFirebase = mutableListOf()
        val db = Firebase.firestore
        val userColRef = account?.email?.let { db.collection("users").document(it) }
        val ordersColRef = userColRef?.collection("orders")

        ordersColRef?.orderBy("date",com.google.firebase.firestore.Query.Direction.DESCENDING)?.get()?.addOnSuccessListener { documents ->
            for (document in documents) {
                receivedOrderDataFromFirebase.add(document.toObject(OrdersModel::class.java))
            }
            binding.epoxyView.setController(epoxyController)
            epoxyController.setData(receivedOrderDataFromFirebase)
            Log.d("ordersData", "onCreateView: $receivedOrderDataFromFirebase")
        }
        return binding.root
    }

}