package org.iesalandalus.pi_musicaincrescendo.data.repository

import android.net.Uri
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants
import org.iesalandalus.pi_musicaincrescendo.domain.model.User
import org.iesalandalus.pi_musicaincrescendo.domain.model.UserProfile

class UserRepositoryImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : UserRepository {

    override suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean,
        instruments: List<String>,
        photoUrl: String?
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
            "instruments" to sanitizedInstruments,
            "photoUrl" to photoUrl
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

    override suspend fun updatePhotoUrl(uid: String, photoUrl: String) {
        database.getReference("users")
            .child(uid)
            .child("photoUrl")
            .setValue(photoUrl)
            .await()
    }

    override suspend fun uploadProfileImage(uid: String, imageUri: Uri): String {
        val storageRef = storage.reference.child("profile_images/$uid.jpg")
        storageRef.putFile(imageUri).await()
        return storageRef.downloadUrl.await().toString()
    }

    override suspend fun getUserProfile(uid: String): UserProfile {
        val snapshot = database.getReference("users").child(uid).get().await()
        return parseUserProfile(snapshot)
    }

    override suspend fun getUserCount(): Int {
        val snapshot = database.getReference("users").get().await()
        return snapshot.childrenCount.toInt()
    }

    override suspend fun getAllUserProfiles(): List<User> {
        val snapshot = database.getReference("users").get().await()
        val result = mutableListOf<User>()
        for (child in snapshot.children) {
            val uid = child.key ?: continue
            val profile = parseUserProfile(child)
            result.add(User(uid = uid, profile = profile))
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
                cancel(
                    message = "Firebase listener cancelled at user count",
                    cause = error.toException()
                )
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    override fun getUsersRealTime(): Flow<List<User>> = callbackFlow {
        val usersRef = database.getReference("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (child in snapshot.children) {
                    val uid = child.key ?: continue
                    val profile = parseUserProfile(child)
                    users.add(User(uid = uid, profile = profile))
                }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(
                    message = "Firebase listener cancelled at users",
                    cause = error.toException()
                )
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    override suspend fun deleteUserProfile(uid: String) {
        database.getReference("users").child(uid).removeValue().await()
    }

    override suspend fun userExists(uid: String): Boolean {
        val snapshot = database.getReference("users").child(uid).get().await()
        return snapshot.exists()
    }

    private fun parseUserProfile(snapshot: DataSnapshot): UserProfile {
        return UserProfile(
            displayName = snapshot.child("displayName").getValue(String::class.java) ?: "",
            gender = snapshot.child("gender").getValue(String::class.java) ?: "",
            isDirector = snapshot.child("isDirector").getValue(Boolean::class.java) == true,
            instruments = snapshot.child("instruments").children.mapNotNull { it.getValue(String::class.java) },
            photoUrl = snapshot.child("photoUrl").getValue(String::class.java)
        )
    }
}