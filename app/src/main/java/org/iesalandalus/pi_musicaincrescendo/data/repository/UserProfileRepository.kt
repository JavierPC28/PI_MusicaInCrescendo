package org.iesalandalus.pi_musicaincrescendo.data.repository

/**
 * Repositorio para manejar la creación inicial del perfil de usuario
 * en Firebase Realtime Database.
 */
fun interface UserProfileRepository {
    suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean
    )
}