package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * Implementación de ProfileRepository usando Firebase Realtime Database.
 */
class ProfileRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : ProfileRepository {

    override suspend fun updateDisplayName(uid: String, displayName: String) {
        // Ruta: /users/{uid}/displayName
        database
            .getReference("users")
            .child(uid)
            .child("displayName")
            .setValue(displayName)
            .await()
    }
}