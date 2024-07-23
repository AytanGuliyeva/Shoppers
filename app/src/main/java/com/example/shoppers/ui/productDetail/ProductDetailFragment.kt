package com.example.shoppers.ui.productDetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.shoppers.base.util.Resource
import com.example.shoppers.data.model.Products
import com.example.shoppers.databinding.FragmentProductDetailBinding

/*
class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    val viewModel: ProductDetailViewModel by viewModels()
    val args: ProductDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchProducts(args.productId)
        initListener()
        viewModel.productsResult.observe(viewLifecycleOwner) { productsResource ->
            when (productsResource) {
                is Resource.Success -> {
                    updateProductsUI(productsResource.data)
                }
                is Resource.Error -> {
                }
                is Resource.Loading -> {
                }
            }
        }
    }

    private fun updateProductsUI(products: Products) {
        binding.nameProduct.text = products.product
        binding.txtDetail.text = products.detail
        binding.price.text = products.price
        Glide.with(binding.root)
            .load(products.productImageUrl)
            .into(binding.imgProduct)
        viewModel.checkSaveStatus(products.productId)

        binding.btnAddProduct.setOnClickListener {
            viewModel.addProductToFirebase(products)
        }
    }

    private fun initListener() {
        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
*/

class ProductDetailFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailBinding
    private val viewModel: ProductDetailViewModel by viewModels()
    private val args: ProductDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchProducts(args.productId)
        initListener()
        viewModel.productsResult.observe(viewLifecycleOwner) { productsResource ->
            when (productsResource) {
                is Resource.Success -> {
                    updateProductsUI(productsResource.data)
                }
                is Resource.Error -> {
                    Log.e("ProductDetailFragment", "Error: ${productsResource.exception.message}")
                }
                is Resource.Loading -> {
                    // Show loading indicator if needed
                }
            }
        }
    }

    private fun updateProductsUI(products: Products) {
        binding.nameProduct.text = products.product
        binding.txtDetail.text = products.detail
        binding.price.text = products.price
        Glide.with(binding.root)
            .load(products.productImageUrl)
            .into(binding.imgProduct)

        viewModel.fetchProductCount(products.productId) { count ->
          //  binding.productCount.text = count.toString()
            Log.e("TAG", "updateProductsUI: $count", )
        }

        binding.btnAddProduct.setOnClickListener {
            viewModel.addProductToFirebase(products)
            Toast.makeText(requireContext(), "Products added.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initListener() {
        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
