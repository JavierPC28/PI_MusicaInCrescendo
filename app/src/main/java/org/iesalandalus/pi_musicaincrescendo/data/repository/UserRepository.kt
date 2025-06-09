package org.iesalandalus.pi_musicaincrescendo.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.User
import org.iesalandalus.pi_musicaincrescendo.domain.model.UserProfile

interface UserRepository {
    suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean,
        instruments: List<String> = emptyList(),
        photoUrl: String? = null
    )

    suspend fun updateInstruments(uid: String, instruments: List<String>)

    suspend fun updateDisplayName(uid: String, displayName: String)

    suspend fun updatePhotoUrl(uid: String, photoUrl: String)

    suspend fun uploadProfileImage(uid: String, imageUri: Uri): String

    suspend fun getUserProfile(uid: String): UserProfile

    suspend fun getUserCount(): Int

    suspend fun getAllUserProfiles(): List<User>

    fun getUserCountRealTime(): Flow<Int>

    fun getUsersRealTime(): Flow<List<User>>

    suspend fun deleteUserProfile(uid: String)

    suspend fun userExists(uid: String): Boolean
}