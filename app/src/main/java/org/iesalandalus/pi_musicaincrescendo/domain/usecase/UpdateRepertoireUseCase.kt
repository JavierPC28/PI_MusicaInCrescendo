package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import android.net.Uri
import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

/**
 * Caso de uso para actualizar una obra existente en el repertorio.
 * @param repo El repositorio de repertorio.
 */
class UpdateRepertoireUseCase(private val repo: RepertoireRepository) {
    /**
     * Ejecuta la actualización de la obra.
     * @param workId El ID de la obra a actualizar.
     * @param title El nuevo título de la obra.
     * @param composer El nuevo compositor.
     * @param videoUrl La nueva URL del vídeo (opcional).
     * @param instrumentFiles Un mapa con las nuevas partituras a añadir o reemplazar.
     */
    suspend operator fun invoke(
        workId: String,
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>
    ) = repo.updateRepertoire(workId, title, composer, videoUrl, instrumentFiles)
}