package org.iesalandalus.pi_musicaincrescendo.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.repository.RepertoireRepository
import java.util.UUID

/**
 * Implementación de RepertoireRepository.
 */
class RepertoireRepositoryImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : RepertoireRepository {

    private companion object {
        const val ERROR_USER_NOT_AUTHENTICATED = "Usuario no autenticado"
    }

    override suspend fun addRepertoire(
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>,
        dateSaved: Long
    ) {
        FirebaseAuth.getInstance().currentUser ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)

        val instrumentUrls = mutableMapOf<String, String>()
        for ((instrument, uri) in instrumentFiles) {
            val fileId = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("repertoire/${Constants.GROUP_ID}/$fileId.pdf")
            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            instrumentUrls[instrument] = downloadUrl
        }

        val repertoireRef = database.reference
            .child("repertoire")
            .child(Constants.GROUP_ID)
            .push()
        val repertoireId = repertoireRef.key ?: UUID.randomUUID().toString()

        val data = mapOf(
            "title" to title,
            "composer" to composer,
            "videoUrl" to (videoUrl ?: ""),
            "instrumentFiles" to instrumentUrls,
            "dateSaved" to dateSaved
        )

        database.reference
            .child("repertoire")
            .child(Constants.GROUP_ID)
            .child(repertoireId)
            .setValue(data)
            .await()
    }

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
                close(error.toException())
            }
        }
        repertoireRef.addValueEventListener(listener)

        awaitClose { repertoireRef.removeEventListener(listener) }
    }

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

    override suspend fun updateRepertoire(
        workId: String,
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>
    ) {
        FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)
        val workRef = database.reference.child("repertoire").child(Constants.GROUP_ID).child(workId)

        // Borramos los archivos antiguos
        val oldWork = getRepertoireById(workId)
        oldWork?.instrumentFiles?.values?.forEach { url ->
            try {
                storage.getReferenceFromUrl(url).delete().await()
            } catch (_: Exception) {
                // Ignoramos si el archivo no existe
            }
        }

        // Subimos los archivos nuevos
        val newInstrumentUrls = mutableMapOf<String, String>()
        for ((instrument, uri) in instrumentFiles) {
            val fileId = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("repertoire/${Constants.GROUP_ID}/$fileId.pdf")

            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            newInstrumentUrls[instrument] = downloadUrl
        }

        // Actualizamos los datos en la base de datos
        val updatedData = mapOf(
            "title" to title,
            "composer" to composer,
            "videoUrl" to (videoUrl ?: ""),
            "instrumentFiles" to newInstrumentUrls,
            "dateSaved" to oldWork?.dateSaved
        )
        workRef.updateChildren(updatedData).await()
    }

    override suspend fun deleteRepertoire(id: String) {
        FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)
        val workRef = database.reference.child("repertoire").child(Constants.GROUP_ID).child(id)
        val work = getRepertoireById(id)

        // Borramos los archivos de Storage
        work?.instrumentFiles?.values?.forEach { url ->
            try {
                storage.getReferenceFromUrl(url).delete().await()
            } catch (_: Exception) {
                // Ignoramos si el archivo no existe
            }
        }

        // Borramos la entrada de la base de datos
        workRef.removeValue().await()
    }
}