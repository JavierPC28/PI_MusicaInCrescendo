package org.iesalandalus.pi_musicaincrescendo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Notification

/**
 * Interfaz que define las operaciones para la gestión de notificaciones.
 */
interface NotificationRepository {
    /**
     * Añade una nueva notificación.
     */
    suspend fun addNotification(text: String, timestamp: Long)

    /**
     * Obtiene un Flow con la lista de notificaciones en tiempo real.
     */
    fun getNotificationsRealTime(): Flow<List<Notification>>

    /**
     * Elimina todas las notificaciones.
     */
    suspend fun deleteAllNotifications()
}