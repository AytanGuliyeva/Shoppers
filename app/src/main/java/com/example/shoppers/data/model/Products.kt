package com.example.shoppers.data.model

data class Products(
    val product: String = "",
    val productId: String = "",
    val userId: String = "",
    val detail: String = "",
    val price: String = "0f",
    val productImageUrl: String = "",
    var isSave: Boolean = false,
    var count:Int=0
)
