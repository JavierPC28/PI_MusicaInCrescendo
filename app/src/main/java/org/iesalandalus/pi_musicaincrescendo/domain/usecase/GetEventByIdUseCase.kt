package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

class GetEventByIdUseCase(private val repo: EventRepository) {
    suspend operator fun invoke(eventId: String): Event? = repo.getEventById(eventId)
}