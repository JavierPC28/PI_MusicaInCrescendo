package org.iesalandalus.pi_musicaincrescendo.domain.repository

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Notification

interface NotificationRepository {
    suspend fun addNotification(text: String, timestamp: Long)
    fun getNotificationsRealTime(): Flow<List<Notification>>
    suspend fun deleteAllNotifications()
}