package org.iesalandalus.pi_musicaincrescendo.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire

/**
 * Interfaz que define las operaciones para la gestión del repertorio.
 */
interface RepertoireRepository {
    /**
     * Añade una nueva obra al repertorio.
     */
    suspend fun addRepertoire(
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>,
        dateSaved: Long
    )

    /**
     * Obtiene un Flow con la lista de obras del repertorio en tiempo real.
     */
    fun getRepertoireRealTime(): Flow<List<Repertoire>>

    /**
     * Obtiene una obra específica por su ID.
     */
    suspend fun getRepertoireById(id: String): Repertoire?

    /**
     * Actualiza una obra existente.
     */
    suspend fun updateRepertoire(
        workId: String,
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>
    )

    /**
     * Elimina una obra por su ID.
     */
    suspend fun deleteRepertoire(id: String)

    /**
     * Comprueba si una obra con un título y compositor ya existe.
     */
    suspend fun repertoireExists(title: String, composer: String): Boolean

    /**
     * Comprueba si otra obra con el mismo título y compositor existe al actualizar.
     */
    suspend fun repertoireExistsForUpdate(workId: String, title: String, composer: String): Boolean
}