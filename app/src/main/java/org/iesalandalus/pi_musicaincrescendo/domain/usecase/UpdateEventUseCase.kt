package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

/**
 * Caso de uso para actualizar los datos de un evento existente.
 * @param repo El repositorio de eventos.
 */
class UpdateEventUseCase(private val repo: EventRepository) {
    /**
     * Ejecuta la actualización del evento.
     * @param event El objeto [Event] con los datos actualizados.
     */
    suspend operator fun invoke(event: Event) = repo.updateEvent(event)
}