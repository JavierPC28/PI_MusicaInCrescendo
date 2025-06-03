package org.iesalandalus.pi_musicaincrescendo.domain.repository

import android.net.Uri

/**
 * Repositorio de repertorio.
 */
fun interface RepertoireRepository {
    suspend fun addRepertoire(
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>,
        dateSaved: Long
    )
}