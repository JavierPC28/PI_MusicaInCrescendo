package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepository
import org.iesalandalus.pi_musicaincrescendo.domain.model.User
import org.iesalandalus.pi_musicaincrescendo.domain.model.UserProfile

/**
 * Agrupa todos los casos de uso relacionados con la gestión de perfiles de usuario.
 * @param repo El repositorio de usuarios.
 */
class UserUseCases(
    private val repo: UserRepository
) {
    /** Crea un nuevo perfil de usuario. */
    suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean,
        instruments: List<String> = emptyList(),
        photoUrl: String? = null
    ) = repo.createUserProfile(uid, displayName, gender, isDirector, instruments, photoUrl)

    /** Actualiza el nombre de visualización de un usuario. */
    suspend fun updateDisplayName(uid: String, newName: String) =
        repo.updateDisplayName(uid, newName)

    /** Actualiza los instrumentos de un usuario. */
    suspend fun updateInstruments(uid: String, instruments: List<String>) =
        repo.updateInstruments(uid, instruments)

    /** Actualiza la URL de la foto de perfil de un usuario. */
    suspend fun updatePhotoUrl(uid: String, photoUrl: String) = repo.updatePhotoUrl(uid, photoUrl)

    /** Sube una imagen de perfil y devuelve su URL. */
    suspend fun uploadProfileImage(uid: String, imageUri: Uri): String =
        repo.uploadProfileImage(uid, imageUri)

    /** Obtiene el perfil de un usuario. */
    suspend fun getUserProfile(uid: String): UserProfile =
        repo.getUserProfile(uid)

    /** Obtiene el número total de usuarios en tiempo real. */
    fun getUserCountRealTime(): Flow<Int> = repo.getUserCountRealTime()

    /** Obtiene la lista de todos los usuarios en tiempo real. */
    fun getUsersRealTime(): Flow<List<User>> = repo.getUsersRealTime()

    /** Comprueba si un usuario ya existe. */
    suspend fun userExists(uid: String): Boolean = repo.userExists(uid)
}