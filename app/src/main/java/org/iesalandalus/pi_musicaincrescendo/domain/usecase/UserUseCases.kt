package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepository
import org.iesalandalus.pi_musicaincrescendo.domain.model.User
import org.iesalandalus.pi_musicaincrescendo.domain.model.UserProfile

class UserUseCases(
    private val repo: UserRepository
) {
    suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean,
        instruments: List<String> = emptyList(),
        photoUrl: String? = null
    ) = repo.createUserProfile(uid, displayName, gender, isDirector, instruments, photoUrl)

    suspend fun updateDisplayName(uid: String, newName: String) =
        repo.updateDisplayName(uid, newName)

    suspend fun updateInstruments(uid: String, instruments: List<String>) =
        repo.updateInstruments(uid, instruments)

    suspend fun updatePhotoUrl(uid: String, photoUrl: String) = repo.updatePhotoUrl(uid, photoUrl)

    suspend fun uploadProfileImage(uid: String, imageUri: Uri): String =
        repo.uploadProfileImage(uid, imageUri)

    suspend fun getUserProfile(uid: String): UserProfile =
        repo.getUserProfile(uid)

    fun getUserCountRealTime(): Flow<Int> = repo.getUserCountRealTime()

    fun getUsersRealTime(): Flow<List<User>> = repo.getUsersRealTime()

    suspend fun userExists(uid: String): Boolean = repo.userExists(uid)
}