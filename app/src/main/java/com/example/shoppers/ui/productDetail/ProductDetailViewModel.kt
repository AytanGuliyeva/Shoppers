package com.example.shoppers.ui.productDetail

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppers.R
import com.example.shoppers.base.util.ConstValues
import com.example.shoppers.base.util.Resource
import com.example.shoppers.data.model.Products
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/*
class ProductDetailViewModel : ViewModel() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _productsResult = MutableLiveData<Resource<Products>>()
    val productsResult: LiveData<Resource<Products>>
        get() = _productsResult

    fun fetchProducts(productsId: String) {
        _loading.postValue(true)
        val postDocumentRef = firestore.collection("Products").document(productsId)

        postDocumentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val products = documentSnapshot.toObject(Products::class.java)
                if (products != null) {
                    _productsResult.postValue(Resource.Success(products))
                } else {
                    _productsResult.postValue(Resource.Error(Exception("Products data is null")))
                }
                _loading.postValue(false)
            }
            .addOnFailureListener { exception ->
                _productsResult.postValue(Resource.Error(exception))
                _loading.postValue(false)
            }
    }

    fun addProductToFirebase(product: Products) {
        val productRef = firestore.collection("Products").document(product.productId)
        productRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Ürün zaten varsa isSave değerini güncelle
                    val isSave = document.getBoolean("isSave") ?: false
                    productRef.update("isSave", !isSave)
                        .addOnSuccessListener {
                            Log.d("addProductToFirebase", "Product save status updated successfully")
                            if (!isSave) {
                                incrementProductCount()
                            } else {
                                decrementProductCount()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("addProductToFirebase", "Error updating product save status: $exception")
                        }
                } else {
                    // Ürün yoksa yeni ürün olarak ekle
                    product.isSave = true
                    productRef.set(product, SetOptions.merge())
                        .addOnSuccessListener {
                            Log.d("addProductToFirebase", "Product added successfully")
                            incrementProductCount()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("addProductToFirebase", "Error adding product: $exception")
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("addProductToFirebase", "Error fetching product: $exception")
            }
    }

    private fun incrementProductCount() {
        val productCountRef = firestore.collection("Metadata").document("ProductCount")
        productCountRef.update("count", FieldValue.increment(1))
            .addOnSuccessListener {
                Log.d("incrementProductCount", "Product count incremented successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("incrementProductCount", "Error incrementing product count: $exception")
            }
    }

    private fun decrementProductCount() {
        val productCountRef = firestore.collection("Metadata").document("ProductCount")
        productCountRef.update("count", FieldValue.increment(-1))
            .addOnSuccessListener {
                Log.d("decrementProductCount", "Product count decremented successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("decrementProductCount", "Error decrementing product count: $exception")
            }
    }

    fun checkSaveStatus(productsId: String) {
        firestore.collection("Saves").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val savedPostId = document.getBoolean(productsId) ?: false
                    if (savedPostId) {
                        // imageView.setImageResource(R.drawable.icons8_saved_icon)
                        // imageView.tag = "saved"
                    } else {
                        // imageView.setImageResource(R.drawable.save_icon)
                        // imageView.tag = "save"
                    }
                } else {
                    // imageView.setImageResource(R.drawable.save_icon)
                    // imageView.tag = "save"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("checkSaveStatus", "Error checking save status: $exception")
            }
    }
}
*/


class ProductDetailViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _productsResult = MutableLiveData<Resource<Products>>()
    val productsResult: LiveData<Resource<Products>>
        get() = _productsResult

    fun fetchProducts(productsId: String) {
        _loading.postValue(true)
        val postDocumentRef = firestore.collection("Products").document(productsId)

        postDocumentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val products = documentSnapshot.toObject(Products::class.java)
                if (products != null) {
                    _productsResult.postValue(Resource.Success(products))
                } else {
                    _productsResult.postValue(Resource.Error(Exception("Products data is null")))
                }
                _loading.postValue(false)
            }
            .addOnFailureListener { exception ->
                _productsResult.postValue(Resource.Error(exception))
                _loading.postValue(false)
            }
    }

    fun addProductToFirebase(product: Products) {
        val productRef = firestore.collection("Products").document(product.productId)
        productRef.get()
            .addOnSuccessListener { document ->
                val currentCount = document?.getLong("count")?.toInt() ?: 0
                val newCount = currentCount + 1

                // Update the count field
                productRef.update("count", newCount)
                    .addOnSuccessListener {
                        Log.d("addProductToFirebase", "Product count incremented successfully for ${product.productId}")

                        // Update the 'save' field based on the new count
                        val newSaveStatus = newCount > 0
                        productRef.update("save", newSaveStatus)
                            .addOnSuccessListener {
                                Log.d("addProductToFirebase", "Product save status updated successfully")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("addProductToFirebase", "Error updating product save status: $exception")
                            }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("addProductToFirebase", "Error incrementing product count: $exception")
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("addProductToFirebase", "Error fetching product: $exception")
            }
    }

    // Optionally, you can have a function to fetch and display the current count if needed
    fun fetchProductCount(productId: String, callback: (Int) -> Unit) {
        val productRef = firestore.collection("Products").document(productId)
        productRef.get()
            .addOnSuccessListener { document ->
                val count = document.getLong("count")?.toInt() ?: 0
                callback(count)
            }
            .addOnFailureListener { exception ->
                Log.e("fetchProductCount", "Error fetching product count: $exception")
                callback(0)
            }
    }
}
