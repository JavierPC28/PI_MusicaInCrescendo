package org.iesalandalus.pi_musicaincrescendo.data.repository

/**
 * Repositorio para manejar datos de perfil de usuario.
 */
fun interface ProfileRepository {
    suspend fun updateDisplayName(uid: String, displayName: String)
}