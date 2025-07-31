package com.example.ecosajha.repository

import com.google.firebase.auth.FirebaseAuth

class AuthRepositoryImpl(val auth: FirebaseAuth) : AuthRepositoryInterface {
    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Login Successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }

    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Registered Successfully", "${auth.currentUser?.uid}")
            } else {
                callback(false, "${it.exception?.message}", "")
            }
        }

    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Reset email sent to $email")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }


}