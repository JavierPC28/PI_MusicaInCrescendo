package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

/**
 * Caso de uso para actualizar el estado de asistencia de un usuario a un evento.
 * @param repo El repositorio de eventos.
 */
class UpdateAttendanceUseCase(private val repo: EventRepository) {
    /**
     * Ejecuta la actualización de la asistencia.
     * @param eventId El ID del evento.
     * @param userId El ID del usuario.
     * @param status El nuevo estado de asistencia ("IRÉ" o "NO IRÉ").
     */
    suspend operator fun invoke(eventId: String, userId: String, status: String) =
        repo.updateAttendance(eventId, userId, status)
}