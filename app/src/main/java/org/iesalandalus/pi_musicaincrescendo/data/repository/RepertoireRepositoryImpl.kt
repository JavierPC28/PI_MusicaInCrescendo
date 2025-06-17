package org.iesalandalus.pi_musicaincrescendo.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository
import java.util.UUID

/**
 * Implementación de RepertoireRepository para gestionar datos de repertorio en Firebase.
 * @param database Instancia de FirebaseDatabase.
 * @param storage Instancia de FirebaseStorage.
 */
class RepertoireRepositoryImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : RepertoireRepository {

    // Constantes privadas para mensajes de error.
    private companion object {
        const val ERROR_USER_NOT_AUTHENTICATED = "Usuario no autenticado"
    }

    /**
     * Añade una nueva obra al repertorio.
     * @param title Título de la obra.
     * @param composer Compositor de la obra.
     * @param videoUrl URL opcional de un vídeo de YouTube.
     * @param instrumentFiles Mapa con los instrumentos y las URIs de sus partituras en PDF.
     * @param dateSaved Marca de tiempo de cuándo se guardó la obra.
     */
    override suspend fun addRepertoire(
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>,
        dateSaved: Long
    ) {
        FirebaseAuth.getInstance().currentUser ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)

        // Sube cada partitura a Firebase Storage y obtiene su URL de descarga.
        val instrumentUrls = mutableMapOf<String, String>()
        for ((instrument, uri) in instrumentFiles) {
            val fileId = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("repertoire/${Constants.GROUP_ID}/$fileId.pdf")
            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            instrumentUrls[instrument] = downloadUrl
        }

        // Crea una nueva referencia en la base de datos para la obra.
        val repertoireRef = database.reference
            .child("repertoire")
            .child(Constants.GROUP_ID)
            .push()
        val repertoireId = repertoireRef.key ?: UUID.randomUUID().toString()

        // Prepara los datos para guardar en la base de datos.
        val data = mapOf(
            "title" to title,
            "composer" to composer,
            "videoUrl" to (videoUrl ?: ""),
            "instrumentFiles" to instrumentUrls,
            "dateSaved" to dateSaved
        )

        // Guarda la información de la obra.
        database.reference
            .child("repertoire")
            .child(Constants.GROUP_ID)
            .child(repertoireId)
            .setValue(data)
            .await()
    }

    /**
     * Obtiene un Flow con la lista de obras del repertorio en tiempo real.
     * @return Un Flow que emite la lista de obras, ordenada por fecha de guardado.
     */
    override fun getRepertoireRealTime(): Flow<List<Repertoire>> = callbackFlow {
        FirebaseAuth.getInstance().currentUser?.uid ?: run {
            close(Exception(ERROR_USER_NOT_AUTHENTICATED))
            return@callbackFlow
        }

        val repertoireRef = database.reference.child("repertoire").child(Constants.GROUP_ID)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val repertoireList = snapshot.children.mapNotNull { dataSnapshot ->
                    dataSnapshot.getValue(Repertoire::class.java)
                        ?.copy(id = dataSnapshot.key ?: "")
                }
                trySend(repertoireList.sortedByDescending { it.dateSaved })
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(
                    message = "El listener de Firebase para repertorio fue cancelado.",
                    cause = error.toException()
                )
            }
        }
        repertoireRef.addValueEventListener(listener)
        // Cierra el listener cuando el Flow es cancelado.
        awaitClose { repertoireRef.removeEventListener(listener) }
    }

    /**
     * Obtiene una obra específica por su ID.
     * @param id El ID de la obra a buscar.
     * @return El objeto Repertoire o null si no se encuentra.
     */
    override suspend fun getRepertoireById(id: String): Repertoire? {
        FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)
        val snapshot = database.reference
            .child("repertoire")
            .child(Constants.GROUP_ID)
            .child(id)
            .get()
            .await()

        return snapshot.getValue(Repertoire::class.java)?.copy(id = snapshot.key ?: "")
    }

    /**
     * Actualiza una obra existente en el repertorio.
     * @param workId El ID de la obra a actualizar.
     * @param title Nuevo título.
     * @param composer Nuevo compositor.
     * @param videoUrl Nueva URL del vídeo.
     * @param instrumentFiles Mapa de nuevas partituras para añadir o reemplazar.
     */
    override suspend fun updateRepertoire(
        workId: String,
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>
    ) {
        FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)
        val workRef = database.reference.child("repertoire").child(Constants.GROUP_ID).child(workId)

        val oldWork = getRepertoireById(workId)
        val finalInstrumentUrls = oldWork?.instrumentFiles?.toMutableMap() ?: mutableMapOf()

        // Procesa los nuevos ficheros de partitura.
        for ((instrument, uri) in instrumentFiles) {
            // Si ya existía una partitura para este instrumento, la elimina de Storage.
            oldWork?.instrumentFiles?.get(instrument)?.let { oldUrl ->
                try {
                    storage.getReferenceFromUrl(oldUrl).delete().await()
                } catch (_: Exception) { /* Ignora errores si el fichero no existe. */
                }
            }
            // Sube el nuevo fichero y obtiene la URL.
            val fileId = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("repertoire/${Constants.GROUP_ID}/$fileId.pdf")

            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            finalInstrumentUrls[instrument] = downloadUrl
        }
        // Prepara y actualiza los datos en la base de datos.
        val updatedData = mapOf(
            "title" to title,
            "composer" to composer,
            "videoUrl" to (videoUrl ?: ""),
            "instrumentFiles" to finalInstrumentUrls,
            "dateSaved" to oldWork?.dateSaved
        )
        workRef.updateChildren(updatedData).await()
    }

    /**
     * Elimina una obra del repertorio.
     * @param id El ID de la obra a eliminar.
     */
    override suspend fun deleteRepertoire(id: String) {
        FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)
        val workRef = database.reference.child("repertoire").child(Constants.GROUP_ID).child(id)
        val work = getRepertoireById(id)

        // Elimina todos los ficheros de partitura asociados de Firebase Storage.
        work?.instrumentFiles?.values?.forEach { url ->
            try {
                storage.getReferenceFromUrl(url).delete().await()
            } catch (_: Exception) { /* Ignora errores. */
            }
        }
        // Elimina la entrada de la base de datos.
        workRef.removeValue().await()
    }

    /**
     * Comprueba si ya existe una obra con el mismo título y compositor.
     * @param title Título a comprobar.
     * @param composer Compositor a comprobar.
     * @return `true` si existe, `false` en caso contrario.
     */
    override suspend fun repertoireExists(title: String, composer: String): Boolean {
        val repertoireRef = database.reference.child("repertoire").child(Constants.GROUP_ID)
        val snapshot = repertoireRef.get().await()

        return snapshot.children.any { dataSnapshot ->
            val work = dataSnapshot.getValue(Repertoire::class.java)
            work?.title?.equals(title, ignoreCase = true) == true &&
                    work.composer.equals(composer, ignoreCase = true)
        }
    }

    /**
     * Comprueba si existe otra obra con el mismo título y compositor al actualizar.
     * Excluye la obra que se está editando de la comprobación.
     * @param workId El ID de la obra que se está actualizando.
     * @param title Título a comprobar.
     * @param composer Compositor a comprobar.
     * @return `true` si existe otra obra con los mismos datos, `false` en caso contrario.
     */
    override suspend fun repertoireExistsForUpdate(
        workId: String,
        title: String,
        composer: String
    ): Boolean {
        val repertoireRef = database.reference.child("repertoire").child(Constants.GROUP_ID)
        val snapshot = repertoireRef.get().await()

        return snapshot.children.any { dataSnapshot ->
            val id = dataSnapshot.key
            val work = dataSnapshot.getValue(Repertoire::class.java)
            id != workId &&
                    work?.title?.equals(title, ignoreCase = true) == true &&
                    work.composer.equals(composer, ignoreCase = true)
        }
    }
}