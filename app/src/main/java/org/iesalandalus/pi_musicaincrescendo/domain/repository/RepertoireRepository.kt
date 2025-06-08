package org.iesalandalus.pi_musicaincrescendo.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire

/**
 * Repositorio de repertorio.
 */
interface RepertoireRepository {
    suspend fun addRepertoire(
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>,
        dateSaved: Long
    )

    fun getRepertoireRealTime(): Flow<List<Repertoire>>
}