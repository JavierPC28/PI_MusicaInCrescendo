package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * Implementación de ProfileRepository usando Firebase Realtime Database.
 */
class ProfileRepositoryImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : ProfileRepository {

    /**
     * Actualiza el nombre de usuario en la ruta: /users/{uid}/displayName
     */
    override suspend fun updateDisplayName(uid: String, displayName: String) {
        database
            .getReference("users")
            .child(uid)
            .child("displayName")
            .setValue(displayName)
            .await()
    }
}