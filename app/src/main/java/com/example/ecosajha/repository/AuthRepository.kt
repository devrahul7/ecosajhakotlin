package com.example.ecosajha.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun sendPasswordResetEmail(email: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                Result.success("Password reset email sent successfully")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                if (result.user != null) {
                    Result.success("Login successful")
                } else {
                    Result.failure(Exception("Login failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
