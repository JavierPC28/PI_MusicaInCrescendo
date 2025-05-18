package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.data.repository.UserProfile
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepository

class GetUserProfileUseCase(
    private val repo: UserRepository
) {
    suspend operator fun invoke(uid: String): UserProfile {
        return repo.getUserProfile(uid)
    }
}