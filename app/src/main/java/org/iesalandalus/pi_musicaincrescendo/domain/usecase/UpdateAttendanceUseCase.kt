package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

class UpdateAttendanceUseCase(private val repo: EventRepository) {
    suspend operator fun invoke(eventId: String, userId: String, status: String) =
        repo.updateAttendance(eventId, userId, status)
}