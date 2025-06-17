package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import android.net.Uri
import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

/**
 * Caso de uso para añadir una nueva obra al repertorio.
 * @param repo El repositorio de repertorio.
 */
class AddRepertoireUseCase(
    private val repo: RepertoireRepository
) {
    /**
     * Ejecuta el caso de uso.
     * @param title Título de la obra.
     * @param composer Compositor de la obra.
     * @param videoUrl URL opcional del vídeo.
     * @param instrumentFiles Mapa de instrumentos y URIs de sus partituras.
     * @param dateSaved Marca de tiempo del guardado.
     */
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