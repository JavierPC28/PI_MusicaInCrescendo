package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

class CheckRepertoireExistsForUpdateUseCase(private val repo: RepertoireRepository) {
    suspend operator fun invoke(workId: String, title: String, composer: String): Boolean {
        return repo.repertoireExistsForUpdate(workId, title, composer)
    }
}