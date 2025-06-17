package org.iesalandalus.pi_musicaincrescendo.domain.model

/**
 * Modelo de datos que representa el perfil de un usuario.
 * @param displayName Nombre a mostrar del usuario.
 * @param gender Género del usuario.
 * @param isDirector `true` si el usuario tiene rol de director.
 * @param instruments Lista de instrumentos que toca el usuario.
 * @param photoUrl URL opcional de la foto de perfil del usuario.
 */
data class UserProfile(
    val displayName: String = "",
    val gender: String = "",
    val isDirector: Boolean = false,
    val instruments: List<String> = emptyList(),
    val photoUrl: String? = null
)