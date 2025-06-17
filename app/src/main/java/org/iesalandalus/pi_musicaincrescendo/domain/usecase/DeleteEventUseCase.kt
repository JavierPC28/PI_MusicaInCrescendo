package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

/**
 * Caso de uso para eliminar un evento específico.
 * @param repo El repositorio de eventos.
 */
class DeleteEventUseCase(private val repo: EventRepository) {
    /**
     * Ejecuta la eliminación del evento.
     * @param eventId El ID del evento a eliminar.
     */
    suspend operator fun invoke(eventId: String) = repo.deleteEvent(eventId)
}