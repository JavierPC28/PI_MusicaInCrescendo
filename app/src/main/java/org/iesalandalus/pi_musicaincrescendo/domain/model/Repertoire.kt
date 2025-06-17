package org.iesalandalus.pi_musicaincrescendo.domain.model

/**
 * Modelo de datos que representa una obra del repertorio.
 * @param id Identificador único de la obra.
 * @param title Título de la obra.
 * @param composer Compositor de la obra.
 * @param videoUrl URL opcional a un vídeo de YouTube relacionado.
 * @param dateSaved Marca de tiempo de cuándo se añadió o actualizó la obra.
 * @param instrumentFiles Mapa que asocia un instrumento con la URL de su partitura en PDF.
 */
data class Repertoire(
    val id: String = "",
    val title: String = "",
    val composer: String = "",
    val videoUrl: String? = null,
    val dateSaved: Long = 0,
    val instrumentFiles: Map<String, String> = emptyMap()
)