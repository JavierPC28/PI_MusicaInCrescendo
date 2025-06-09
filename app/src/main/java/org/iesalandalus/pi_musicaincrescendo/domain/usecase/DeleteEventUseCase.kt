package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

class DeleteEventUseCase(private val repo: EventRepository) {
    suspend operator fun invoke(eventId: String) = repo.deleteEvent(eventId)
}