package com.example.shoppers.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppers.base.util.ConstValues
import com.example.shoppers.base.util.Resource
import com.example.shoppers.data.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel: ViewModel() {
    private  var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance()


    private val _userInformation = MutableLiveData<Resource<Users>>()
    val userInformation: LiveData<Resource<Users>>
        get() = _userInformation

    fun fetchUserInformation() {
        _userInformation.postValue(Resource.Loading)
        firestore.collection(ConstValues.USERS)
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toUser()
                    if (user != null) {
                        _userInformation.postValue(Resource.Success(user))
                    } else {
                        _userInformation.postValue(Resource.Error(Exception("User data is null")))
                    }
                } else {
                    _userInformation.postValue(Resource.Error(Exception("User document does not exist")))
                }
            }
            .addOnFailureListener { exception ->
                _userInformation.postValue(Resource.Error(exception))
            }
    }

    private fun DocumentSnapshot.toUser(): Users? {
        return try {
            val userId = getString(ConstValues.USER_ID)
            val username = getString(ConstValues.USERNAME)
            val email = getString(ConstValues.EMAIL)
            val password = getString(ConstValues.PASSWORD)
            val imageUrl = getString(ConstValues.IMAGE_URL)

            Users(
                userId.orEmpty(),
                username.orEmpty(),
                email.orEmpty(),
                password.orEmpty(),
                imageUrl.orEmpty(),
            )
        } catch (e: Exception) {
            null
        }
    }
}