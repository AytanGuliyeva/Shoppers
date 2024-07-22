package com.example.shoppers.ui.signUp

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shoppers.R
import com.example.shoppers.base.util.Resource
import com.example.shoppers.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    val viewModel: SignUpViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
        initNavigationListeners()
    }

    private fun setupViews() {
        binding.btSignUp.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()
            val email = binding.edtEmail.text.toString().trim()

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(
                    email
                )
            ) {
                Toast.makeText(requireContext(),
                    "All fields are required"
                   , Toast.LENGTH_SHORT)
                    .show()
            } else if (password.length < 6) {
                Toast.makeText(
                    requireContext(),
                    "Password must have at least 6 characters"
                        ,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.signUp(username, email, password)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userCreated.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    handleSignUpFailure(resource.exception)
                }
            }
        }
    }

    private fun handleSignUpFailure(exception: Throwable) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                Toast.makeText(
                    requireContext(),
                    "User with this email already exist"
                    ,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                Toast.makeText(
                    requireContext(),
                    "Sign up failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun initNavigationListeners() {
        binding.txtSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
    }
}