package com.example.nuttygala

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.findNavController
import com.example.nuttygala.databinding.ActivitySearchBinding
import com.example.nuttygala.models.SingleItemModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }

    private fun onAddToCartClicked(s: String) {

    }

    private fun onItemClicked(s: String) {

    }
}