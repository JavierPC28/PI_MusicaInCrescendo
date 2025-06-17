package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.NotificationRepository

/**
 * Caso de uso para añadir una nueva notificación.
 * @param repo El repositorio de notificaciones.
 */
class AddNotificationUseCase(private val repo: NotificationRepository) {
    /**
     * Ejecuta el caso de uso para añadir una notificación con el texto proporcionado.
     * La marca de tiempo se genera automáticamente.
     * @param text El contenido de la notificación.
     */
    suspend operator fun invoke(text: String) {
        repo.addNotification(text, System.currentTimeMillis())
    }
}