package org.iesalandalus.pi_musicaincrescendo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event

/**
 * Interfaz que define las operaciones para la gestión de eventos.
 */
interface EventRepository {
    /**
     * Añade un nuevo evento a la base de datos.
     */
    suspend fun addEvent(
        title: String,
        description: String?,
        type: String,
        date: String,
        startTime: String,
        endTime: String,
        location: String,
        coordinates: String?,
        repertoire: Map<String, String>
    )

    /**
     * Obtiene un Flow con la lista de eventos en tiempo real.
     */
    fun getEventsRealTime(): Flow<List<Event>>

    /**
     * Obtiene un evento específico por su ID.
     */
    suspend fun getEventById(eventId: String): Event?

    /**
     * Actualiza los datos de un evento existente.
     */
    suspend fun updateEvent(event: Event)

    /**
     * Elimina un evento por su ID.
     */
    suspend fun deleteEvent(eventId: String)

    /**
     * Actualiza el estado de asistencia de un usuario a un evento.
     */
    suspend fun updateAttendance(eventId: String, userId: String, status: String)
}