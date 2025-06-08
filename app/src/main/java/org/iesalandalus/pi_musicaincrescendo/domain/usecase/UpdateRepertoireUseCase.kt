package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import android.net.Uri
import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository

class UpdateRepertoireUseCase(private val repo: RepertoireRepository) {
    suspend operator fun invoke(
        workId: String,
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>
    ) = repo.updateRepertoire(workId, title, composer, videoUrl, instrumentFiles)
}