package com.example.ecosajha.repository

interface AuthRepositoryInterface {

    fun login(email : String, password : String, callback : (Boolean, String) -> Unit)
    fun register(email : String, password : String, callback : (Boolean, String, String) -> Unit)

    fun forgetPassword(email: String,callback: (Boolean, String) -> Unit)
    }