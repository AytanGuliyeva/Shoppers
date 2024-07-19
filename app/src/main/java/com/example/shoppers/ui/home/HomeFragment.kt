package com.example.shoppers.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shoppers.R
import com.example.shoppers.base.util.Resource
import com.example.shoppers.data.model.Products
import com.example.shoppers.databinding.FragmentHomeBinding
import com.example.shoppers.ui.profile.ProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    private lateinit var productsAdapter: ProductsAdapter
    val viewModel: HomeViewModel by viewModels()
    private var selectedProduct: Products? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        auth = Firebase.auth
        firestore=Firebase.firestore
        viewModel.fetchProducts()

        if (auth.currentUser?.uid=="eiRmiAlokPMC57x092MalvFEuBH3"){
            binding.floatActionButton.visibility=View.VISIBLE
        }
        initNavigationListeners()
        viewModel.productsResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    productsAdapter.submitList(resource.data)
                    binding.progressBar.visibility = View.GONE
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE

                    Toast.makeText(requireContext(), "Error occurred!", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

    }
    private fun setupRecyclerView() {
        productsAdapter = ProductsAdapter(itemClick = {
            selectedProduct = it
           // productsDetail(selectedProduct!!.productId, selectedProduct!!.userId)
        })
        binding.rvProducts.adapter = productsAdapter
    }

    private fun initNavigationListeners(){
        binding.floatActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addProductFragment)

        }
    }

}