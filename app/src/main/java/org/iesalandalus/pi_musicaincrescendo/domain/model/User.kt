package org.iesalandalus.pi_musicaincrescendo.domain.model

/**
 * Modelo de datos que combina el UID de un usuario con su perfil.
 * @param uid Identificador único del usuario (de Firebase Auth).
 * @param profile Objeto UserProfile con los detalles del usuario.
 */
data class User(
    val uid: String,
    val profile: UserProfile
)