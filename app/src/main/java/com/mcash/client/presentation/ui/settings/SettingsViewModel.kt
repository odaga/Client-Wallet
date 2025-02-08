package com.mcash.client.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mcash.data.local.room.dao.FAQDao
import com.mcash.data.remote.model.FAQ
import com.mcash.domain.model.NotificationEntity
import com.mcash.domain.repository.PreferenceRepository
import com.mcash.domain.usecases.NotificationUseCase
import com.mcash.domain.usecases.user.ClearUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private var faqDao: FAQDao,
    private var preferenceRepository: PreferenceRepository,
    private val notificationUseCase: NotificationUseCase,
    private val clearUserUseCase: ClearUserUseCase
) : ViewModel() {

    suspend fun getFAQList(): List<FAQ> {
        return withContext(Dispatchers.IO) {
            faqDao.getFAQs()
        }

    }

    suspend fun getHelpInfo(): String {
        return preferenceRepository.getHelpInfo().first()
    }

    suspend fun updateNotification(notificationEntity: NotificationEntity) {
        notificationUseCase.updateNotification(notificationEntity)
    }

    suspend fun getNotificationsList(): List<NotificationEntity> {
        return withContext(Dispatchers.IO) {
            notificationUseCase.getNotifications()
        }
    }


    suspend fun signOut() {
        preferenceRepository.saveSignOutState()
        clearUserUseCase()
    }
}
