package com.example.ecosajha.repository

import com.example.ecosajha.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepository {

    fun forgetPassword(email : String, callback : (Boolean, String) ->Unit)
    fun getCurrentUser() : FirebaseUser?
    fun addUserToDatabase(userID : String, model: UserModel, callback: (Boolean, String) ->Unit)
    fun logout(callback : (Boolean, String) ->Unit)
    fun getUserByID(userID : String, callback : (UserModel?, Boolean, String) ->Unit)
    fun updateProfile(userID : String, userData: MutableMap<String, Any?>, callback : (Boolean, String) ->Unit)
//    fun removeData(userID : String, callback: (Boolean, String) -> Unit)
}