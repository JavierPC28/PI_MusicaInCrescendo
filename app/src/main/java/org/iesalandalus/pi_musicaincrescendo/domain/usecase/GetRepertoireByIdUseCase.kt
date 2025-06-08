package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

class GetRepertoireByIdUseCase(private val repo: RepertoireRepository) {
    suspend operator fun invoke(id: String): Repertoire? = repo.getRepertoireById(id)
}