package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

/**
 * Caso de uso para verificar si ya existe otra obra con el mismo título y compositor
 * al momento de actualizar una existente.
 * @param repo El repositorio de repertorio.
 */
class CheckRepertoireExistsForUpdateUseCase(private val repo: RepertoireRepository) {
    /**
     * Ejecuta la comprobación.
     * @param workId El ID de la obra que se está actualizando.
     * @param title El título a verificar.
     * @param composer El compositor a verificar.
     * @return `true` si existe una obra duplicada, `false` en caso contrario.
     */
    suspend operator fun invoke(workId: String, title: String, composer: String): Boolean {
        return repo.repertoireExistsForUpdate(workId, title, composer)
    }
}