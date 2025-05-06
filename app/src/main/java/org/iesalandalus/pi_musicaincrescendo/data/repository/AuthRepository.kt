package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.auth.AuthResult

interface AuthRepository {
    suspend fun register(email: String, password: String): AuthResult
    suspend fun login(email: String, password: String): AuthResult
    fun currentUserEmail(): String?
}