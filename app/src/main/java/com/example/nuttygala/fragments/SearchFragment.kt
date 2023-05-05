package com.example.nuttygala.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.example.nuttygala.FragmentToActivity
import com.example.nuttygala.R
import com.example.nuttygala.databinding.FragmentSearchBinding
import com.example.nuttygala.epoxycontrollers.SearchActivityEpoxyController
import com.example.nuttygala.models.CartModel
import com.example.nuttygala.models.SingleItemModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {


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

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        val db = Firebase.firestore
        val epoxyController = SearchActivityEpoxyController(::onItemClicked, ::onAddToCartClicked)
        var receivedDataFromFirestore = mutableListOf<SingleItemModel>()

        var currentText = ""
        val handler = Handler(Looper.getMainLooper())
        val searchRunnable = Runnable {
            receivedDataFromFirestore.clear()
            db.collection("allDryFruitsProducts")
                .whereArrayContains("searchArray",currentText.toLowerCase())
                .get()
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        for (document in task1.result) {
                            Log.d("TAG", "onCreateView: ${task1.result.size()}")
                            Log.d("TAG", document.id + " => " + document.data)
                            receivedDataFromFirestore.add(document.toObject(
                                SingleItemModel::class.java))
                        }
                        binding.searchActivityEpoxyView.setController(epoxyController)
                        epoxyController.setData(receivedDataFromFirestore)
                    } else {
                        Log.d("TAG", "Error getting documents: ")
                    }
                }
        }

        binding.searchEditText.doAfterTextChanged {
            currentText = it?.toString() ?: ""

            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, 250L)
        }

        binding.backIcon.setOnClickListener {
            val currentFragmentId = findNavController().currentDestination?.id
            findNavController().popBackStack(currentFragmentId!!,true)
            findNavController().navigate(R.id.frontPageFragment)
        }
        binding.clearText.setOnClickListener { binding.searchEditText.text.clear() }


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
        val directions = SearchFragmentDirections.actionSearchFragmentToSingleItemFragment(s)
        findNavController().navigate(directions)
    }

    override fun onStop() {
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        super.onStop()
    }

    override fun onResume() {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        super.onResume()
    }
}