package org.iesalandalus.pi_musicaincrescendo.domain.model

data class Event(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val repertoireIds: Map<String, String> = emptyMap()
)