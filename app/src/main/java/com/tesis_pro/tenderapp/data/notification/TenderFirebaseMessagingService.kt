package com.tesis_pro.tenderapp.data.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TenderFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        FirebasePushTokenStore(applicationContext).saveLatestToken(token)
        PushRegistrationWorkScheduler.enqueue(applicationContext)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        FcmNotificationRenderer(applicationContext).show(message)
    }
}
