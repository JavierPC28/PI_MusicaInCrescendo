package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepository

class CreateUserProfileUseCase(
    private val repo: UserRepository
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

class UpdateDisplayNameUseCase(
    private val repo: UserRepository
) {
    suspend operator fun invoke(uid: String, newName: String) {
        repo.updateDisplayName(uid, newName)
    }
}