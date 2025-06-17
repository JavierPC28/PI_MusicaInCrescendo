package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepository
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepository

/**
 * Caso de uso para eliminar la cuenta de un usuario por completo.
 * Esto incluye la eliminación de su perfil en la base de datos y su cuenta de autenticación.
 * @param authRepository El repositorio de autenticación.
 * @param userRepository El repositorio de perfiles de usuario.
 */
class DeleteUserAccountUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    /**
     * Ejecuta el proceso de eliminación de la cuenta para el usuario actualmente logueado.
     * @throws IllegalStateException si no hay ningún usuario logueado.
     */
    suspend operator fun invoke() {
        val userId = authRepository.currentUserId()
            ?: throw IllegalStateException("No hay un usuario logueado para eliminar.")

        // Primero elimina el perfil de la base de datos.
        userRepository.deleteUserProfile(userId)

        // Luego, elimina la cuenta de autenticación.
        authRepository.deleteAccount()
    }
}