package org.iesalandalus.pi_musicaincrescendo.domain.model

data class UserProfile(
    val displayName: String = "",
    val gender: String = "",
    val isDirector: Boolean = false,
    val instruments: List<String> = emptyList(),
    val photoUrl: String? = null
)