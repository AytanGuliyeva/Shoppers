package com.example.shoppers.ui.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppers.base.util.ConstValues
import com.example.shoppers.base.util.Resource
import com.example.shoppers.data.model.Products
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel:ViewModel() {
    private  var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _productsResult = MutableLiveData<Resource<List<Products>>>()
    val productsResult: LiveData<Resource<List<Products>>>
        get() = _productsResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    fun fetchProducts() {
        _loading.postValue(true)
        firestore.collection("Products").get()
            .addOnSuccessListener { querySnapshot ->
                val postList = mutableListOf<Products>()
                for (document in querySnapshot.documents) {
                    val post = document.toObject(Products::class.java)
                    Log.d(TAG, "Document data: ${document.data}")

                    post?.let {
                        Log.e(TAG, "fetchProducts: ${it.productImageUrl}")
//                        if (auth.currentUser?.uid == it.userId) {
//                            val postWithTimestamp = it.copy(time = timestamp)
//                            postList.add(postWithTimestamp)
//                        }
                        postList.add(it)
                                            }
                }
                _productsResult.postValue(Resource.Success(postList))
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Failed! ${exception.message}", exception)
            }
    }
}