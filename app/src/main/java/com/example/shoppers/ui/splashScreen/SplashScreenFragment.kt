package com.example.shoppers.ui.splashScreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.shoppers.R
import com.example.shoppers.databinding.FragmentSplashScreenBinding


class SplashScreenFragment : Fragment() {
    private lateinit var binding: FragmentSplashScreenBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_splashScreenFragment_to_signInFragment)
            }, 1000)
        }
    }
}