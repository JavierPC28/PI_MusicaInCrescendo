package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * Implementación de UserProfileRepository usando Firebase Realtime Database.
 */
class UserProfileRepositoryImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : UserProfileRepository {

    override suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean
    ) {
        val userRef = database
            .getReference("users")
            .child(uid)

        val profileData = mapOf(
            "displayName" to displayName,
            "gender" to gender,
            "isDirector" to isDirector
        )

        userRef.setValue(profileData).await()
    }
}