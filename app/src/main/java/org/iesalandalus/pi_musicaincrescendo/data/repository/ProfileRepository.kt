package org.iesalandalus.pi_musicaincrescendo.data.repository

/**
 * Repositorio para manejar datos de perfil de usuario.
 */
interface ProfileRepository {
    /**
     * Actualiza el nombre de usuario en la base de datos.
     * @param uid UID del usuario autenticado.
     * @param displayName Nuevo nombre de usuario a guardar.
     */
    suspend fun updateDisplayName(uid: String, displayName: String)
}