package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants

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

    // Obtenemos número total de usuarios registrados
    suspend fun getUserCount(): Int

    suspend fun getAllUserProfiles(): List<Pair<String, UserProfile>>

    fun getUserCountRealTime(): Flow<Int>

    fun getUsersRealTime(): Flow<List<Pair<String, UserProfile>>>
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

        val sanitizedInstruments =
            if (isDirector && !instruments.contains(Constants.DIRECCION_MUSICAL)) {
                listOf(Constants.DIRECCION_MUSICAL) + instruments
            } else {
                instruments
            }.take(Constants.MAX_INSTRUMENTS)

        val profileData = mapOf(
            "displayName" to displayName,
            "gender" to gender,
            "isDirector" to isDirector,
            "instruments" to sanitizedInstruments
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
        val instruments =
            snapshot.child("instruments").children.mapNotNull { it.getValue(String::class.java) }
        return UserProfile(displayName, gender, isDirector, instruments)
    }

    override suspend fun getUserCount(): Int {
        val snapshot = database.getReference("users").get().await()
        return snapshot.childrenCount.toInt()
    }

    override suspend fun getAllUserProfiles(): List<Pair<String, UserProfile>> {
        val snapshot = database.getReference("users").get().await()
        val result = mutableListOf<Pair<String, UserProfile>>()
        for (child in snapshot.children) {
            val uid = child.key ?: continue
            val displayName = child.child("displayName").getValue(String::class.java) ?: ""
            val gender = child.child("gender").getValue(String::class.java) ?: ""
            val isDirector = child.child("isDirector").getValue(Boolean::class.java) == true
            val instruments =
                child.child("instruments").children.mapNotNull { it.getValue(String::class.java) }
            val profile = UserProfile(displayName, gender, isDirector, instruments)
            result.add(uid to profile)
        }
        return result
    }

    override fun getUserCountRealTime(): Flow<Int> = callbackFlow {
        val usersRef = database.getReference("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    override fun getUsersRealTime(): Flow<List<Pair<String, UserProfile>>> = callbackFlow {
        val usersRef = database.getReference("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<Pair<String, UserProfile>>()
                for (child in snapshot.children) {
                    val uid = child.key ?: continue
                    val profile = parseUserProfile(child)
                    users.add(uid to profile)
                }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    private fun parseUserProfile(snapshot: DataSnapshot): UserProfile {
        return UserProfile(
            displayName = snapshot.child("displayName").getValue(String::class.java) ?: "",
            gender = snapshot.child("gender").getValue(String::class.java) ?: "",
            isDirector = snapshot.child("isDirector").getValue(Boolean::class.java) == true,
            instruments = snapshot.child("instruments").children.mapNotNull { it.getValue(String::class.java) }
        )
    }
}