package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.iesalandalus.pi_musicaincrescendo.domain.model.Notification
import org.iesalandalus.pi_musicaincrescendo.domain.repository.NotificationRepository

class GetNotificationsUseCase(private val repo: NotificationRepository) {
    operator fun invoke(): Flow<List<Notification>> = repo.getNotificationsRealTime()
}