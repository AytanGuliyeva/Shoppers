package com.example.shoppers.ui.signIn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.shoppers.R
import com.example.shoppers.databinding.FragmentSignInBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        checkLogin()
        btnLogin()
        initNavigationListeners()

    }

    private fun btnLogin() {
        binding.btnLogin.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(
                    username, password
                ).addOnSuccessListener {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)

                }.addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Incorrect username or password", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkLogin() {
        if (auth.currentUser != null) {
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }
    }

    private fun initNavigationListeners() {
        binding.txtSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }
}