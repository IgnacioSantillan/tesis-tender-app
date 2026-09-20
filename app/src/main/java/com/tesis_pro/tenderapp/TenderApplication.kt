package com.tesis_pro.tenderapp

import android.app.Application
import com.tesis_pro.tenderapp.data.notification.LaundryNotificationChannels

class TenderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LaundryNotificationChannels.ensureCreated(this)
    }
}
