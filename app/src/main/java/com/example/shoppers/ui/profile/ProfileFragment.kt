package com.example.shoppers.ui.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shoppers.R
import com.example.shoppers.base.util.ConstValues
import com.example.shoppers.base.util.Resource
import com.example.shoppers.databinding.FragmentProfileBinding
import com.example.shoppers.ui.home.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    val viewModel: ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding= FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        firestore= Firebase.firestore
        buttonLogout()
        viewModel.fetchUserInformation()
        viewModel.userInformation.observe(viewLifecycleOwner) { userResource ->
            when (userResource) {
                is Resource.Success -> {
                    binding.textWelcome.text="Hello ${userResource.data.username}!"
                    binding.edtUsername.text=userResource.data.username
                    binding.edtEmail.text=userResource.data.email

                }

                is Resource.Error -> {
                }

                is Resource.Loading -> {
                }
            }
        }

    }
    private fun buttonLogout() {
        binding.btnLogout.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.logout_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val btnLogout: TextView = dialog.findViewById(R.id.btnLogOut)
            val btnCancel: TextView = dialog.findViewById(R.id.btnCancel)

            btnLogout.setOnClickListener {
//                firestore.collection(ConstValues.USERS).document(auth.currentUser!!.uid)
//                    .update(ConstValues.TOKEN, "")
                auth.signOut()
                findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}