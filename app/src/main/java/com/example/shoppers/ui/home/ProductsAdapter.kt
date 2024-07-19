package com.example.shoppers.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppers.data.model.Products
import com.example.shoppers.databinding.ItemProductsBinding

class ProductsAdapter(
    private var itemClick: (item: Products) -> Unit,
) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {
    private val diffUtilCallBack = object : DiffUtil.ItemCallback<Products>() {
        override fun areItemsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem == newItem
        }
    }

    private val diffUtil = AsyncListDiffer(this, diffUtilCallBack)

    fun submitList(products: List<Products>) {
        diffUtil.submitList(products)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val binding =
            ItemProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductsViewHolder(binding)    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bind(diffUtil.currentList[position])
    }
    inner class ProductsViewHolder(private val binding: ItemProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Products) {
            Glide.with(binding.root)
                .load(item.productImageUrl)
                .into(binding.imgProduct)
            binding.nameProduct.text=item.product
            binding.price.text=item.price
            itemView.setOnClickListener {
                itemClick(item)
            }
        }
    }

}