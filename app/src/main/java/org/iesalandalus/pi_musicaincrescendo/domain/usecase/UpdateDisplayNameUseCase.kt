package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.data.repository.ProfileRepository

/**
 * Caso de uso para actualizar el nombre de usuario.
 */
class UpdateDisplayNameUseCase(
    private val repo: ProfileRepository
) {
    /**
     * Ejecuta la actualización.
     * @param uid UID del usuario.
     * @param newName Nuevo nombre de usuario.
     */
    suspend operator fun invoke(uid: String, newName: String) {
        repo.updateDisplayName(uid, newName)
    }
}