package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

/**
 * Caso de uso para eliminar una obra del repertorio.
 * @param repo El repositorio de repertorio.
 */
class DeleteRepertoireUseCase(private val repo: RepertoireRepository) {
    /**
     * Ejecuta la eliminación de la obra.
     * @param id El ID de la obra a eliminar.
     */
    suspend operator fun invoke(id: String) = repo.deleteRepertoire(id)
}