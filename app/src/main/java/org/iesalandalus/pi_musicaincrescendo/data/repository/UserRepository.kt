package org.iesalandalus.pi_musicaincrescendo.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.User
import org.iesalandalus.pi_musicaincrescendo.domain.model.UserProfile

/**
 * Interfaz que define las operaciones para la gestión de datos de usuarios.
 */
interface UserRepository {
    /**
     * Crea un perfil de usuario en la base de datos.
     */
    suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean,
        instruments: List<String> = emptyList(),
        photoUrl: String? = null
    )

    /**
     * Actualiza la lista de instrumentos de un usuario.
     */
    suspend fun updateInstruments(uid: String, instruments: List<String>)

    /**
     * Actualiza el nombre de visualización de un usuario.
     */
    suspend fun updateDisplayName(uid: String, displayName: String)

    /**
     * Actualiza la URL de la foto de perfil de un usuario.
     */
    suspend fun updatePhotoUrl(uid: String, photoUrl: String)

    /**
     * Sube una imagen de perfil y devuelve su URL de descarga.
     */
    suspend fun uploadProfileImage(uid: String, imageUri: Uri): String

    /**
     * Obtiene el perfil de un usuario específico.
     */
    suspend fun getUserProfile(uid: String): UserProfile

    /**
     * Obtiene el número total de usuarios.
     */
    suspend fun getUserCount(): Int

    /**
     * Obtiene una lista con todos los perfiles de usuario.
     */
    suspend fun getAllUserProfiles(): List<User>

    /**
     * Obtiene el número de usuarios en tiempo real.
     */
    fun getUserCountRealTime(): Flow<Int>

    /**
     * Obtiene una lista de todos los usuarios en tiempo real.
     */
    fun getUsersRealTime(): Flow<List<User>>

    /**
     * Elimina el perfil de un usuario.
     */
    suspend fun deleteUserProfile(uid: String)

    /**
     * Comprueba si un usuario existe en la base de datos.
     */
    suspend fun userExists(uid: String): Boolean
}