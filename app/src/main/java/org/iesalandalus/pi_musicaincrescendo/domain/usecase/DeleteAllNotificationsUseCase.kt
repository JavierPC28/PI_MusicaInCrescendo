package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.NotificationRepository

/**
 * Caso de uso para eliminar todas las notificaciones.
 * @param repo El repositorio de notificaciones.
 */
class DeleteAllNotificationsUseCase(private val repo: NotificationRepository) {
    /**
     * Ejecuta la eliminación de todas las notificaciones.
     */
    suspend operator fun invoke() = repo.deleteAllNotifications()
}