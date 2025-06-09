package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

class AddEventUseCase(private val repo: EventRepository) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        type: String,
        date: String,
        startTime: String,
        endTime: String,
        location: String,
        coordinates: String?,
        repertoire: Map<String, String>
    ) = repo.addEvent(
        title,
        description,
        type,
        date,
        startTime,
        endTime,
        location,
        coordinates,
        repertoire
    )
}