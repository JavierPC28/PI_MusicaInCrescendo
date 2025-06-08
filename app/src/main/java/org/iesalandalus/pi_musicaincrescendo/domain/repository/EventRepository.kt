package org.iesalandalus.pi_musicaincrescendo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event

interface EventRepository {
    suspend fun addEvent(
        type: String,
        date: String,
        startTime: String,
        endTime: String,
        location: String,
        repertoire: Map<String, String>
    )

    fun getEventsRealTime(): Flow<List<Event>>
}