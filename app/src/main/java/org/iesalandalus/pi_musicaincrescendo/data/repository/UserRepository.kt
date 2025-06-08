package org.iesalandalus.pi_musicaincrescendo.data.repository

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.User
import org.iesalandalus.pi_musicaincrescendo.domain.model.UserProfile

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

    suspend fun getAllUserProfiles(): List<User>

    fun getUserCountRealTime(): Flow<Int>

    fun getUsersRealTime(): Flow<List<User>>
}