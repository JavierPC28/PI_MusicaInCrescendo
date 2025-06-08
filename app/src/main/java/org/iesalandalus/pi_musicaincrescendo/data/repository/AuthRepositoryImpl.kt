package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.util.Date

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override suspend fun register(email: String, password: String): AuthResult {
        return auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, password).await()
    }

    override fun currentUserEmail(): String? = auth.currentUser?.email

    override fun logout() {
        auth.signOut()
    }

    override fun currentUserRegistrationDate(): Date? {
        val metadata = auth.currentUser?.metadata
        return metadata?.creationTimestamp?.let { Date(it) }
    }

    override fun currentUserId(): String? = auth.currentUser?.uid
}