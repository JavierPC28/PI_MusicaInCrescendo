package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

// Modelo de datos del perfil de usuario
data class UserProfile(
    val displayName: String = "",
    val gender: String = "",
    val isDirector: Boolean = false,
    val instruments: List<String> = emptyList()
)

interface UserRepository {
    suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean,
        instruments: List<String> = emptyList()
    )

    suspend fun updateInstruments(uid: String, instruments: List<String>)

    suspend fun updateDisplayName(uid: String, displayName: String)

    // Obtenemos el perfil completo del usuario
    suspend fun getUserProfile(uid: String): UserProfile
}

class UserRepositoryImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : UserRepository {

    override suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean,
        instruments: List<String>
    ) {
        val userRef = database.getReference("users").child(uid)
        val profileData = mapOf(
            "displayName" to displayName,
            "gender" to gender,
            "isDirector" to isDirector,
            "instruments" to instruments
        )
        userRef.setValue(profileData).await()
    }

    override suspend fun updateInstruments(uid: String, instruments: List<String>) {
        database.getReference("users")
            .child(uid)
            .child("instruments")
            .setValue(instruments)
            .await()
    }

    override suspend fun updateDisplayName(uid: String, displayName: String) {
        database.getReference("users")
            .child(uid)
            .child("displayName")
            .setValue(displayName)
            .await()
    }

    override suspend fun getUserProfile(uid: String): UserProfile {
        val snapshot = database.getReference("users").child(uid).get().await()
        val displayName = snapshot.child("displayName").getValue(String::class.java) ?: ""
        val gender = snapshot.child("gender").getValue(String::class.java) ?: ""
        val isDirector = snapshot.child("isDirector").getValue(Boolean::class.java) == true
        val instruments = snapshot.child("instruments").children.mapNotNull { it.getValue(String::class.java) }
        return UserProfile(displayName, gender, isDirector, instruments)
    }
}