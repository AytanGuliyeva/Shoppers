package com.example.shoppers.ui.signUp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppers.base.util.ConstValues
import com.example.shoppers.base.util.Resource
import com.example.shoppers.data.model.Users
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModel : ViewModel() {
    private  var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val _loading = MutableLiveData<Boolean>()
//    val loading: LiveData<Boolean>
//        get() = _loading

    private val _userCreated = MutableLiveData<Resource<Users>>()
    val userCreated: LiveData<Resource<Users>>
        get() = _userCreated

    fun signUp(username: String, email: String, password: String) {
        _userCreated.postValue(Resource.Loading)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                userAccount(username, email, password)
            }
            .addOnFailureListener { exception ->
                _userCreated.postValue(Resource.Error(exception))
            }
    }

    private fun userAccount(username: String, email: String, password: String) {
        val userId = auth.currentUser?.uid ?: return
        val userMap = hashMapOf(
            ConstValues.USER_ID to userId,
            ConstValues.USERNAME to username,
            ConstValues.EMAIL to email,
            ConstValues.PASSWORD to password,
            ConstValues.IMAGE_URL to "gs://shoppers-27441.appspot.com/profile_photo_default.jpg"
        )

        val refDb = firestore.collection(ConstValues.USERS).document(userId)
        refDb.set(userMap)
            .addOnSuccessListener {
                _userCreated.value =
                    Resource.Success(Users(userId, username, email, password, "", ""))
            }
            .addOnFailureListener { exception ->
                _userCreated.postValue(Resource.Error(exception))
            }
    }
}
