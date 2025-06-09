package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import org.iesalandalus.pi_musicaincrescendo.domain.repository.NotificationRepository

class AddNotificationUseCase(private val repo: NotificationRepository) {
    suspend operator fun invoke(text: String) {
        repo.addNotification(text, System.currentTimeMillis())
    }
}