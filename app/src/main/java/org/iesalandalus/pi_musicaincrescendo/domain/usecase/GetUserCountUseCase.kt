package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepository

class GetUserCountUseCase(
    private val repo: UserRepository
) {
    suspend operator fun invoke(): Int {
        return repo.getUserCount()
    }
}