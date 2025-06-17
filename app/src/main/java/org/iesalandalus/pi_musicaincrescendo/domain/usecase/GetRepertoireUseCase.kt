package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

/**
 * Caso de uso para obtener la lista de obras del repertorio en tiempo real.
 * @param repo El repositorio de repertorio.
 */
class GetRepertoireUseCase(
    private val repo: RepertoireRepository
) {
    /**
     * Ejecuta el caso de uso.
     * @return Un [Flow] que emite la lista de obras cada vez que hay cambios.
     */
    operator fun invoke(): Flow<List<Repertoire>> = repo.getRepertoireRealTime()
}