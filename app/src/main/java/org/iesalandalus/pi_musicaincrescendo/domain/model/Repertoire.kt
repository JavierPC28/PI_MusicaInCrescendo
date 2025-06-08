package org.iesalandalus.pi_musicaincrescendo.domain.model

data class Repertoire(
    val id: String = "",
    val title: String = "",
    val composer: String = "",
    val videoUrl: String? = null,
    val dateSaved: Long = 0,
    val instrumentFiles: Map<String, String> = emptyMap()
)