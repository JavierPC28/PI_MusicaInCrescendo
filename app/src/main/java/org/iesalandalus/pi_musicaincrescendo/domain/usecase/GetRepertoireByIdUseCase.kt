package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

/**
 * Caso de uso para obtener una obra del repertorio por su ID.
 * @param repo El repositorio de repertorio.
 */
class GetRepertoireByIdUseCase(private val repo: RepertoireRepository) {
    /**
     * Ejecuta la obtención de la obra.
     * @param id El ID de la obra a buscar.
     * @return El objeto [Repertoire] si se encuentra, o `null` si no.
     */
    suspend operator fun invoke(id: String): Repertoire? = repo.getRepertoireById(id)
}