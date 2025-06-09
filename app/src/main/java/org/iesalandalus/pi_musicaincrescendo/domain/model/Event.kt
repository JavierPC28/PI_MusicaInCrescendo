package org.iesalandalus.pi_musicaincrescendo.domain.model

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