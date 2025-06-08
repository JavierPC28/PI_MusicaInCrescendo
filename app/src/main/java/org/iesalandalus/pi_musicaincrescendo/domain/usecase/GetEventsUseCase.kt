package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

class GetEventsUseCase(private val repo: EventRepository) {
    operator fun invoke(): Flow<List<Event>> = repo.getEventsRealTime()
}