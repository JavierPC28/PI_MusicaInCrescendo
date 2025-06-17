package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

/**
 * Caso de uso para obtener la lista de eventos en tiempo real.
 * @param repo El repositorio de eventos.
 */
class GetEventsUseCase(private val repo: EventRepository) {
    /**
     * Ejecuta el caso de uso.
     * @return Un [Flow] que emite la lista de eventos cada vez que hay cambios.
     */
    operator fun invoke(): Flow<List<Event>> = repo.getEventsRealTime()
}