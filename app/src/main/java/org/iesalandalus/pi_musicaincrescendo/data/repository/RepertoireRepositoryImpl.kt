package org.iesalandalus.pi_musicaincrescendo.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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

    override suspend fun addRepertoire(
        title: String,
        composer: String,
        videoUrl: String?,
        instrumentFiles: Map<String, Uri>,
        dateSaved: Long
    ) {
        // Obtenemos el UID del usuario actual.
        val firebaseUser = FirebaseAuth.getInstance().currentUser
            ?: throw Exception("Usuario no autenticado")

        val uid = firebaseUser.uid

        // 1) Subimos cada PDF a Storage y obtenemos la URL de descarga.
        val instrumentUrls = mutableMapOf<String, String>()
        for ((instrument, uri) in instrumentFiles) {
            // Se genera un ID aleatorio para el fichero
            val fileId = UUID.randomUUID().toString()
            // Ruta en Storage: "repertoire/{uid}/{fileId}.pdf"
            val storageRef = storage.reference.child("repertoire/$uid/$fileId.pdf")
            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            instrumentUrls[instrument] = downloadUrl
        }

        // 2) Preparamos metadata de la obra
        // Creamos un nodo único en Realtime Database: "repertoire/{uid}/{repertoireId}"
        val repertoireRef = database.reference
            .child("repertoire")
            .child(uid)
            .push()
        val repertoireId = repertoireRef.key ?: UUID.randomUUID().toString()

        // Estructura que guardaremos
        val data = mapOf(
            "title" to title,
            "composer" to composer,
            "videoUrl" to (videoUrl ?: ""),
            "instrumentFiles" to instrumentUrls,
            "dateSaved" to dateSaved
        )

        // 3) Guardamos metadata en Realtime Database
        database.reference
            .child("repertoire")
            .child(uid)
            .child(repertoireId)
            .setValue(data)
            .await()
    }

    override fun getRepertoireRealTime(): Flow<List<Repertoire>> = callbackFlow {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            close(Exception("Usuario no autenticado"))
            return@callbackFlow
        }

        val repertoireRef = database.reference.child("repertoire").child(uid)

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
}