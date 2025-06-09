package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

class CheckRepertoireExistsUseCase(private val repo: RepertoireRepository) {
    suspend operator fun invoke(title: String, composer: String): Boolean {
        return repo.repertoireExists(title, composer)
    }
}