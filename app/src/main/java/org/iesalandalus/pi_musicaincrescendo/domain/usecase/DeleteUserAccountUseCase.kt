package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepository
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepository

class DeleteUserAccountUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        val userId = authRepository.currentUserId()
            ?: throw IllegalStateException("No hay un usuario logueado para eliminar.")

        userRepository.deleteUserProfile(userId)

        authRepository.deleteAccount()
    }
}