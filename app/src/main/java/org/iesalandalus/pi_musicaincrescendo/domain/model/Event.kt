package org.iesalandalus.pi_musicaincrescendo.domain.model

/**
 * Modelo de datos que representa un evento.
 * @param id Identificador único del evento.
 * @param title Título del evento.
 * @param description Descripción opcional del evento.
 * @param type Tipo de evento (p. ej., "Concierto", "Ensayo").
 * @param date Fecha del evento en formato "dd/MM/yyyy".
 * @param startTime Hora de inicio en formato "HH:mm".
 * @param endTime Hora de finalización en formato "HH:mm".
 * @param location Lugar del evento.
 * @param coordinates Coordenadas geográficas opcionales ("lat,lon").
 * @param repertoireIds Mapa de IDs de las obras del repertorio para el evento.
 * @param asistencias Mapa que registra la asistencia de los usuarios (userId -> "IRÉ"/"NO IRÉ").
 */
data class Event(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val type: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val coordinates: String? = null,
    val repertoireIds: Map<String, String> = emptyMap(),
    val asistencias: Map<String, String> = emptyMap()
)