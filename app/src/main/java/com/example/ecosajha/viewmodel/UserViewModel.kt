package com.example.ecosajha.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecosajha.model.UserModel
import com.example.ecosajha.repository.UserRepository
import com.google.firebase.auth.FirebaseUser

class UserViewModel(val repo : UserRepository) : ViewModel() {



    fun forgetPassword(email : String, callback : (Boolean, String) ->Unit) {
        repo.forgetPassword(email, callback)
    }
    fun getCurrentUser() : FirebaseUser? {
        return repo.getCurrentUser()
    }
    fun addUserToDatabase(userID : String, model: UserModel, callback: (Boolean, String) ->Unit) {
        repo.addUserToDatabase(userID, model, callback)
    }
    fun logout(callback : (Boolean, String) ->Unit) {
        repo.logout(callback)
    }

    private var _users = MutableLiveData<UserModel?>()
    val users : LiveData<UserModel?> get() = _users


    fun getUserByID(userID : String) {
        repo.getUserByID(userID) { users, success, message ->
            if(success && users != null) {
                _users.postValue(users)
            }
            else {
                _users.postValue(null)
            }
        }
    }

    fun updateProfile(userID : String, userData: MutableMap<String, Any?>, callback : (Boolean, String) ->Unit) {
        repo.updateProfile(userID, userData, callback)
    }

}