package org.iesalandalus.pi_musicaincrescendo.domain.model

/**
 * Modelo de datos que representa una notificación.
 * @param id Identificador único de la notificación.
 * @param text Contenido del mensaje de la notificación.
 * @param timestamp Marca de tiempo (en milisegundos) de cuándo se creó.
 */
data class Notification(
    val id: String = "",
    val text: String = "",
    val timestamp: Long = 0
)