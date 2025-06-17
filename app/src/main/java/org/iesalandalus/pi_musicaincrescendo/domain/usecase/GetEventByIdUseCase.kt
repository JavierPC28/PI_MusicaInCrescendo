package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

/**
 * Caso de uso para obtener un evento específico por su ID.
 * @param repo El repositorio de eventos.
 */
class GetEventByIdUseCase(private val repo: EventRepository) {
    /**
     * Ejecuta la obtención del evento.
     * @param eventId El ID del evento a buscar.
     * @return El objeto [Event] si se encuentra, o `null` si no.
     */
    suspend operator fun invoke(eventId: String): Event? = repo.getEventById(eventId)
}