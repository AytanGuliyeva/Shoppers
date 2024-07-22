package com.example.shoppers.ui.card

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.shoppers.base.util.Resource
import com.example.shoppers.databinding.FragmentCardBinding
import com.example.shoppers.ui.home.HomeViewModel
import com.example.shoppers.ui.home.ProductsAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class CardFragment : Fragment() {
    private lateinit var binding: FragmentCardBinding
    val viewModel: CardViewModel by viewModels()
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    private lateinit var cardProductsAdapter: CardProductsAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        firestore= Firebase.firestore
        setupRecyclerView()
        setupSwipeToUpdateCount()
        viewModel.fetchProducts()
        viewModel.productsResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    cardProductsAdapter.submitList(resource.data)
                }

                is Resource.Error -> {

                    Toast.makeText(requireContext(), "Error occurred!", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                }
            }
        }

    }
    private fun setupRecyclerView() {
        cardProductsAdapter = CardProductsAdapter { product ->
            updateProductCount(product.productId)
        }
        binding.rvProducts.adapter = cardProductsAdapter
    }

    private fun setupSwipeToUpdateCount() {
        val itemTouchHelper = ItemTouchHelper(SwipeToUpdateCountCallback(cardProductsAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvProducts)
    }

    private fun updateProductCount(productId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val productRef = firestore.collection("Products").document(productId)

        productRef.get()
            .addOnSuccessListener { document ->
                val currentCount = document.getLong("count") ?: 0
                if (currentCount > 0) {
                    productRef.update("count", currentCount - 1)
                        .addOnSuccessListener {
                            Log.d("CardFragment", "Product count decremented successfully for $productId")
                        }
                        .addOnFailureListener { e ->
                            Log.e("CardFragment", "Error decrementing product count: $e")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("CardFragment", "Error fetching product: $e")
            }
    }
}