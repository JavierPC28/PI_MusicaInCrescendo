package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.data.repository.UserProfile
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepository

class GetAllUserProfilesUseCase(
    private val repo: UserRepository
) {
    suspend operator fun invoke(): List<Pair<String, UserProfile>> {
        return repo.getAllUserProfiles()
    }
}