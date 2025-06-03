package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import android.net.Uri
import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

/**
 * Caso de uso para añadir repertorio.
 */
class AddRepertoireUseCase(
    private val repo: RepertoireRepository
) {
    suspend operator fun invoke(
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>,
        dateSaved: Long
    ) {
        repo.addRepertoire(title, composer, videoUrl, instrumentFiles, dateSaved)
    }
}