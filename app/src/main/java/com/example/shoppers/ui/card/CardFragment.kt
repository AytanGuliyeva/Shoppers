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
import com.example.shoppers.TotalPriceListener
import com.example.shoppers.base.util.Resource
import com.example.shoppers.databinding.FragmentCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CardFragment : Fragment(), TotalPriceListener {

    private lateinit var binding: FragmentCardBinding
    private val viewModel: CardViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var cardProductsAdapter: CardProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        setupRecyclerView()
        setupSwipeToUpdateCount()
        viewModel.fetchProducts()
        viewModel.productsResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {

                    val sum =resource.data.sumOf { it.price.toDouble() }
                    binding.TotalPrice.text=sum.toString()
                    cardProductsAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error occurred!", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun setupRecyclerView() {
        cardProductsAdapter = CardProductsAdapter(
            onItemSwipe = { product ->
                updateProductCount(product.productId)
            },
           // totalPriceListener = this
        )
        binding.rvProducts.adapter = cardProductsAdapter
    }

    private fun setupSwipeToUpdateCount() {
        val itemTouchHelper = ItemTouchHelper(SwipeToUpdateCountCallback(cardProductsAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvProducts)
    }

    private fun updateProductCount(productId: String) {
        val productRef = firestore.collection("Products").document(productId)

        productRef.get()
            .addOnSuccessListener { document ->
                val currentCount = document.getLong("count") ?: 0
                if (currentCount > 0) {
                    productRef.update("count", currentCount - 1)
                        .addOnSuccessListener {
                            Log.d("CardFragment", "Product count decremented successfully for $productId")
                            viewModel.fetchProducts()
                          //  updateTotalPrice()
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

    private fun updateTotalPrice() {
        val products = cardProductsAdapter.diffUtil.currentList
        var totalPrice = 0.0
        for (product in products) {
            val priceNumeric = product.price.toDoubleOrNull() ?: 0.0
            totalPrice += product.count * priceNumeric
        }
        onTotalPriceCalculated(totalPrice)
    }

    override fun onTotalPriceCalculated(totalPrice: Double) {
        binding.TotalPrice.text = String.format("%.2f$", totalPrice)
    }
}
