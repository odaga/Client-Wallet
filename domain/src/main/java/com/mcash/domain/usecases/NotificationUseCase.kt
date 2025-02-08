package com.mcash.domain.usecases

import com.mcash.domain.model.NotificationEntity
import com.mcash.domain.repository.LocalRepository
import javax.inject.Inject

class NotificationUseCase @Inject constructor(
     val localRepository: LocalRepository
) {

    suspend fun insertNotifications(notificationEntity: NotificationEntity) = localRepository.insertNotification(notificationEntity)

    suspend fun getNotifications() = localRepository.getNotifications()

    suspend fun updateNotification(notificationEntity: NotificationEntity) = localRepository.updateNotification(notificationEntity)
}