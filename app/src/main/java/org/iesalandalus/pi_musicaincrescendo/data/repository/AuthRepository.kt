package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import java.util.Date

/**
 * Interfaz que define las operaciones de autenticación.
 */
interface AuthRepository {
    /** Registra un nuevo usuario con email y contraseña. */
    suspend fun register(email: String, password: String): AuthResult

    /** Inicia sesión con email y contraseña. */
    suspend fun login(email: String, password: String): AuthResult

    /** Inicia sesión con credenciales de Google. */
    suspend fun signInWithGoogle(idToken: String): AuthResult

    /** Obtiene el email del usuario actual. */
    fun currentUserEmail(): String?

    /** Cierra la sesión del usuario actual. */
    fun logout()

    /** Obtiene la fecha de registro del usuario actual. */
    fun currentUserRegistrationDate(): Date?

    /** Obtiene el ID del usuario actual. */
    fun currentUserId(): String?

    /** Elimina la cuenta del usuario actual. */
    suspend fun deleteAccount()

    /** Obtiene el objeto FirebaseUser del usuario actual. */
    fun getCurrentUser(): FirebaseUser?
}