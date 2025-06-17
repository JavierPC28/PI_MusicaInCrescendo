package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

/**
 * Caso de uso para verificar si una obra con un título y compositor específicos ya existe.
 * @param repo El repositorio de repertorio.
 */
class CheckRepertoireExistsUseCase(private val repo: RepertoireRepository) {
    /**
     * Ejecuta la comprobación.
     * @param title El título a verificar.
     * @param composer El compositor a verificar.
     * @return `true` si la obra ya existe, `false` en caso contrario.
     */
    suspend operator fun invoke(title: String, composer: String): Boolean {
        return repo.repertoireExists(title, composer)
    }
}