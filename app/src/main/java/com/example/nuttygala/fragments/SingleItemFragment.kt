package com.example.nuttygala.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nuttygala.FragmentToActivity
import com.example.nuttygala.PaymentActivity
import com.example.nuttygala.databinding.FragmentSingleItemBinding
import com.example.nuttygala.epoxycontrollers.SingleItemFragmentEpoxyController
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.ReviewModel
import com.example.nuttygala.models.SingleItemModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SingleItemFragment : Fragment() {

    private val safeArgs: SingleItemFragmentArgs by navArgs()
    private lateinit var receivedData: SingleItemModel
    private var mCallback: FragmentToActivity? = null
    private lateinit var mGoogleSignInClient: GoogleSignInClient
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
        val binding = FragmentSingleItemBinding.inflate(layoutInflater)
        val db = Firebase.firestore
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("1059667677594-ucidp245f7pee8vd8t16eq64i337mcgd.apps.googleusercontent.com")
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        account = GoogleSignIn.getLastSignedInAccount(requireContext())


        val epoxyController = SingleItemFragmentEpoxyController(
            ::onItemClicked,
            ::onAddToCartClicked,
            ::onAddToCartOfMainModelClicked,
            ::onBuyItNowClicked,
            ::onSubmitReviewClicked
            )
        val receivedDataFromFirestore = mutableListOf<SingleItemModel>()
        val reviewsList = mutableListOf<ReviewModel>()


        val docRef = db.collection("allDryFruitsProducts").document(safeArgs.itemName)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    receivedData = document.toObject(SingleItemModel::class.java)!!

                    db.collection("allDryFruitsProducts").document(safeArgs.itemName)
                        .collection("Ratings")
                        .get().addOnCompleteListener {
                            if (it.isSuccessful) {
                                for (doc in it.result) {
                                    reviewsList.add(doc.toObject(ReviewModel::class.java))
                                }
                            }
                        }

                    db.collection("allDryFruitsProducts")
                        .whereNotEqualTo("itemName",safeArgs.itemName)
                        .get()
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                for (document1 in task1.result) {
                                    receivedDataFromFirestore.add(document1.toObject(SingleItemModel::class.java))
                                    Log.d("Size", "onCreateView: ${task1.result.size()}")
                                }
                                Log.d("Received Data", "onCreateView: $receivedData")
                                binding.singleItemFragmentEpoxyView.setController(epoxyController)
                                epoxyController.setData(receivedData,receivedDataFromFirestore,reviewsList)
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.exception)
                            }
                        }
                } else {
                    Log.d("TAG", "No such document")
                }
            } else {
                Log.d("TAG", "get failed with ", task.exception)
            }
        }
        return binding.root
    }

    private fun onBuyItNowClicked(itemImageUrl: String, itemName: String, itemCounter: String, itemPrice: String) {
        val intent = Intent(requireContext(), PaymentActivity::class.java)
        intent.putStringArrayListExtra("data", arrayListOf(
            "fromSingleItemFragment",
            itemImageUrl,
            itemName,
            itemCounter,
            itemPrice,
            account?.email
        ))
        startActivity(intent)
    }

    private fun onSubmitReviewClicked(itemName: String, starRating: Float, reviewText: String) {
        val db = Firebase.firestore
        val itemRef = db.collection("allDryFruitsProducts").document(itemName)
        val ratingColRef = account?.email?.let { itemRef.collection("Ratings").document(it) }
        val userName = account?.displayName
        val reviewModel = ReviewModel(
            userName = userName,
            userRating = starRating,
            userReview = reviewText
        )

        ratingColRef?.set(reviewModel)?.addOnSuccessListener {
            Toast.makeText(requireContext(),"Review Submitted",Toast.LENGTH_SHORT).show()
        }
    }

    private fun onAddToCartOfMainModelClicked(data: CartModel) {
        sendData(data)
    }

    private fun onItemClicked(s: String) {
        val directions = SingleItemFragmentDirections.actionSingleItemFragmentSelf(s)
        findNavController().navigate(directions)
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

    private fun sendData(data: CartModel): Boolean {
        mCallback!!.addToCartOfFragmentToActivity(data)
        return true
    }

    private fun onAddToCartClicked(data: CartModel) {
        sendData(data)
    }

}