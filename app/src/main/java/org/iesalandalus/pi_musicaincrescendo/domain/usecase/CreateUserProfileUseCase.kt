package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.data.repository.UserProfileRepository

/**
 * Caso de uso para crear el perfil de un nuevo usuario
 * en la base de datos con nombre, género y rol de director.
 */
class CreateUserProfileUseCase(
    private val repo: UserProfileRepository
) {
    suspend operator fun invoke(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean
    ) {
        repo.createUserProfile(uid, displayName, gender, isDirector)
    }
}