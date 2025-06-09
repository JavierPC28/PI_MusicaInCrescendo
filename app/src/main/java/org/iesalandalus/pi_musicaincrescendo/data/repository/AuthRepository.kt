package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import java.util.Date

interface AuthRepository {
    suspend fun register(email: String, password: String): AuthResult
    suspend fun login(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(idToken: String): AuthResult
    fun currentUserEmail(): String?
    fun logout()
    fun currentUserRegistrationDate(): Date?
    fun currentUserId(): String?
    suspend fun deleteAccount()
    fun getCurrentUser(): FirebaseUser?
}