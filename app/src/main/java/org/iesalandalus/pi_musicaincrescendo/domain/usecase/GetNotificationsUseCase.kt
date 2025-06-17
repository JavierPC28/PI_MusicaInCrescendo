package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Notification
import org.iesalandalus.pi_musicaincrescendo.domain.repository.NotificationRepository

/**
 * Caso de uso para obtener la lista de notificaciones en tiempo real.
 * @param repo El repositorio de notificaciones.
 */
class GetNotificationsUseCase(private val repo: NotificationRepository) {
    /**
     * Ejecuta el caso de uso.
     * @return Un [Flow] que emite la lista de notificaciones cada vez que hay cambios.
     */
    operator fun invoke(): Flow<List<Notification>> = repo.getNotificationsRealTime()
}