package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.NotificationRepository

class DeleteAllNotificationsUseCase(private val repo: NotificationRepository) {
    suspend operator fun invoke() = repo.deleteAllNotifications()
}