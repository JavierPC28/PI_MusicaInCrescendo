package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserProfile
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepository

class UserUseCases(
    private val repo: UserRepository
) {
    suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean,
        instruments: List<String> = emptyList()
    ) = repo.createUserProfile(uid, displayName, gender, isDirector, instruments)

    suspend fun updateDisplayName(uid: String, newName: String) =
        repo.updateDisplayName(uid, newName)

    suspend fun updateInstruments(uid: String, instruments: List<String>) =
        repo.updateInstruments(uid, instruments)

    suspend fun getUserProfile(uid: String): UserProfile =
        repo.getUserProfile(uid)

    suspend fun getUserCount(): Int =
        repo.getUserCount()

    fun getUserCountRealTime(): Flow<Int> = repo.getUserCountRealTime()

    fun getUsersRealTime(): Flow<List<Pair<String, UserProfile>>> = repo.getUsersRealTime()
}