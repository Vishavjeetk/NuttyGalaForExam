package com.example.nuttygala.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nuttygala.FragmentToActivity
import com.example.nuttygala.chatbotfiles.MainActivity2
import com.example.nuttygala.databinding.FragmentFrontPageBinding
import com.example.nuttygala.epoxycontrollers.FrontPageEpoxyController
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.SingleItemModel
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class FrontPageFragment : Fragment() {

    var count = 0
    private var mCallback: FragmentToActivity? = null
    private lateinit var binding: FragmentFrontPageBinding


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
        // Inflate the layout for this fragment

        FirebaseApp.initializeApp(requireContext())
        var db = Firebase.firestore
        binding = FragmentFrontPageBinding.inflate(layoutInflater)
        val epoxyController = FrontPageEpoxyController(::onItemClicked, ::onAddToCartClicked, ::onViewAllProductsClicked)
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


        binding.epoxyView.setOnScrollChangeListener(View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY + 5 && binding.extendedFab.isExtended) {     // Scroll Down
                binding.extendedFab.shrink()
            }

            if (scrollY < oldScrollY + 5 && !binding.extendedFab.isExtended) {      // Scroll Up
                binding.extendedFab.extend()
            }

        })

        binding.extendedChatBotFab.setOnClickListener {
            val intent = Intent(activity,MainActivity2::class.java)
            startActivity(intent)
        }

        binding.extendedFab.setOnClickListener {
            val url = "https://wa.me/7889108186"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        return binding.root
    }

    private fun onViewAllProductsClicked() {
        val directions = FrontPageFragmentDirections.actionFrontPageFragmentToAllProductsFragment()
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

    private fun onItemClicked(s: String) {
        val directions = FrontPageFragmentDirections.actionFrontPageFragmentToSingleItemFragment(s)
        findNavController().navigate(directions)
    }


}