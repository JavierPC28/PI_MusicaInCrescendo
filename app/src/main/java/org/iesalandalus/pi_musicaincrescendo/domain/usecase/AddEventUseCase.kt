package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

/**
 * Clase de datos para encapsular los parámetros necesarios para crear un evento.
 */
data class AddEventParams(
    val title: String,
    val description: String?,
    val type: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val location: String,
    val coordinates: String?,
    val repertoire: Map<String, String>
)

/**
 * Caso de uso para añadir un nuevo evento.
 * @param repo El repositorio de eventos.
 */
class AddEventUseCase(private val repo: EventRepository) {
    /**
     * Ejecuta el caso de uso para añadir un evento.
     * @param params Los parámetros del evento a añadir.
     */
    suspend operator fun invoke(params: AddEventParams) = repo.addEvent(
        title = params.title,
        description = params.description,
        type = params.type,
        date = params.date,
        startTime = params.startTime,
        endTime = params.endTime,
        location = params.location,
        coordinates = params.coordinates,
        repertoire = params.repertoire
    )
}