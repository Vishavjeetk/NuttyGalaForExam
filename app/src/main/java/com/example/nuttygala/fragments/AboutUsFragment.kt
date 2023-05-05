package com.example.nuttygala.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nuttygala.databinding.FragmentAboutUsBinding
import com.squareup.picasso.Picasso

class AboutUsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAboutUsBinding.inflate(layoutInflater)

        Picasso.get()
            .load("https://firebasestorage.googleapis.com/v0/b/nutty-gala.appspot.com/o/nutty_gala_about_us_logo.png?alt=media&token=4c43a1f0-6102-4645-9c67-4d07f8f41e18")
            .into(binding.aboutUsImageView)
        return binding.root
    }

}