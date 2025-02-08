package com.mcash.client.core.utils

import android.app.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mcash.domain.model.NotificationEntity
import com.mcash.domain.usecases.NotificationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

//@Module
@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationUseCase: NotificationUseCase

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val id = message.messageId
        val title = message.notification?.title
        val data = message.notification?.body
        val date = message.notification?.eventTime ?:1000000
        val d = Date(date)
        val x = SimpleDateFormat("dd-mm-yyyy").format(d)

        CoroutineScope(Dispatchers.IO).launch {
            notificationUseCase.insertNotifications(NotificationEntity(id?:"empty", title, data, x))

            withContext(Dispatchers.Main) {
                Notification.Builder(this@MyFirebaseMessagingService).setContentTitle(message.notification?.title).setContentText(message.notification?.body)
            }
        }



    }
}