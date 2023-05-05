package com.example.nuttygala.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.nuttygala.FragmentToActivity
import com.example.nuttygala.R
import com.example.nuttygala.databinding.FragmentAllProductsBinding
import com.example.nuttygala.databinding.FragmentFrontPageBinding
import com.example.nuttygala.epoxycontrollers.AllProductsFragmentEpoxyController
import com.example.nuttygala.epoxycontrollers.FrontPageEpoxyController
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.SingleItemModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllProductsFragment : Fragment() {

    private lateinit var binding: FragmentAllProductsBinding
    private var mCallback: FragmentToActivity? = null


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
        binding = FragmentAllProductsBinding.inflate(layoutInflater)
        val db = Firebase.firestore
        val epoxyController = AllProductsFragmentEpoxyController(::onItemClicked, ::onAddToCartClicked)
        binding.epoxyView.setController(epoxyController)
        val receivedDataFromFirestore = mutableListOf<SingleItemModel>()

        db.collection("allDryFruitsProducts")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        Log.d("TAG", "onCreateView: ${task.result.size()}")
                        Log.d("TAG", document.id + " => " + document.data)
                        receivedDataFromFirestore.add(document.toObject(SingleItemModel::class.java))
                    }
                    binding.epoxyView.setController(epoxyController)
                    epoxyController.setData(receivedDataFromFirestore)
                } else {
                    Log.d("TAG", "Error getting documents: ", task.exception)
                }
            }

        // Inflate the layout for this fragment
        return binding.root
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

    private fun onItemClicked(s: String) {
        val directions = AllProductsFragmentDirections.actionAllProductsFragmentToSingleItemFragment(s)
        findNavController().navigate(directions)
    }

}