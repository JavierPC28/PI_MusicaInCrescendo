package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

class GetRepertoireUseCase(
    private val repo: RepertoireRepository
) {
    operator fun invoke(): Flow<List<Repertoire>> = repo.getRepertoireRealTime()
}