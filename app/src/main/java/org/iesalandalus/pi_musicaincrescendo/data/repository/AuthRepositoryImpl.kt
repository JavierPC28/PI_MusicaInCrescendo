package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.auth.*
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Implementación de [AuthRepository] que utiliza Firebase Authentication.
 * @param auth Instancia de FirebaseAuth.
 */
class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    /** Registra un nuevo usuario en Firebase. */
    override suspend fun register(email: String, password: String): AuthResult {
        return auth.createUserWithEmailAndPassword(email, password).await()
    }

    /** Inicia sesión de un usuario en Firebase. */
    override suspend fun login(email: String, password: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, password).await()
    }

    /** Inicia sesión usando un token de ID de Google. */
    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential).await()
    }

    /** Devuelve el email del usuario actualmente autenticado. */
    override fun currentUserEmail(): String? = auth.currentUser?.email

    /** Cierra la sesión del usuario en Firebase. */
    override fun logout() {
        auth.signOut()
    }

    /** Obtiene la fecha de creación de la cuenta del usuario actual. */
    override fun currentUserRegistrationDate(): Date? {
        val metadata = auth.currentUser?.metadata
        return metadata?.creationTimestamp?.let { Date(it) }
    }

    /** Devuelve el UID del usuario actualmente autenticado. */
    override fun currentUserId(): String? = auth.currentUser?.uid

    /** Elimina la cuenta del usuario actual de Firebase Authentication. */
    override suspend fun deleteAccount() {
        auth.currentUser?.delete()?.await()
    }

    /** Devuelve el objeto FirebaseUser del usuario actual. */
    override fun getCurrentUser(): FirebaseUser? = auth.currentUser
}