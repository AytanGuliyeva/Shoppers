package com.example.shoppers.ui.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppers.TotalPriceListener
import com.example.shoppers.data.model.Products
import com.example.shoppers.databinding.ItemCardProductsBinding
import kotlin.time.times

class CardProductsAdapter(
    val onItemSwipe: (Products) -> Unit,
    //private val totalPriceListener: TotalPriceListener
) : RecyclerView.Adapter<CardProductsAdapter.CardProductsViewHolder>() {

    private val diffUtilCallBack = object : DiffUtil.ItemCallback<Products>() {
        override fun areItemsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem == newItem
        }
    }

    val diffUtil = AsyncListDiffer(this, diffUtilCallBack)

    fun submitList(products: List<Products>) {
        diffUtil.submitList(products)
       // calculateTotalPrice()
    }

    private fun calculateTotalPrice() {
        var totalPrice = 0.0
        for (product in diffUtil.currentList) {
            val priceNumeric = product.price.toDoubleOrNull() ?: 0.0
            totalPrice += product.count * priceNumeric
        }
       // totalPriceListener.onTotalPriceCalculated(totalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardProductsViewHolder {
        val binding =
            ItemCardProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardProductsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onBindViewHolder(holder: CardProductsViewHolder, position: Int) {
        holder.bind(diffUtil.currentList[position])
    }

    inner class CardProductsViewHolder(private val binding: ItemCardProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Products) {
            Glide.with(binding.root)
                .load(item.productImageUrl)
                .into(binding.imgProduct)
            binding.nameProduct.text = item.product
            binding.txtPrice2.text = item.price
            binding.txtQtyCount.text = item.count.toString()
            val priceNumeric = item.price.toDouble()
            val total = item.count * priceNumeric
            binding.txtTotal.text = total.toString()
        }
    }
}
