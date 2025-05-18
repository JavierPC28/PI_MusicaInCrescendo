package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

interface UserRepository {
    suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean
    )

    suspend fun updateDisplayName(uid: String, displayName: String)
}

class UserRepositoryImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : UserRepository {

    override suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean
    ) {
        val userRef = database.getReference("users").child(uid)
        val profileData = mapOf(
            "displayName" to displayName,
            "gender" to gender,
            "isDirector" to isDirector
        )
        userRef.setValue(profileData).await()
    }

    override suspend fun updateDisplayName(uid: String, displayName: String) {
        database.getReference("users")
            .child(uid)
            .child("displayName")
            .setValue(displayName)
            .await()
    }
}