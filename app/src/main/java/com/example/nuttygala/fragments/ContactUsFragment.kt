package com.example.nuttygala.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nuttygala.databinding.FragmentBulkOrdersBinding
import com.squareup.picasso.Picasso

class ContactUsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBulkOrdersBinding.inflate(layoutInflater)

        Picasso.get()
            .load("https://media.istockphoto.com/id/1154896831/photo/assorted-nuts-and-dried-fruit-background-organic-food-in-wooden-bowls-top-view.jpg?b=1&s=612x612&w=0&k=20&c=2yuhEKmY9LmKdaC2LWzZAPAuspXmFIxR99UTBpSYCeY=")
            .into(binding.bulkOrderImageView)
        // Inflate the layout for this fragment
        return binding.root

    }
}