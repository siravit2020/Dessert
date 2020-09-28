package com.jabirdeveloper.tinderswipe

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createCHANNEL()
    }

    private fun createCHANNEL() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, "Example", importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        val CHANNEL_ID: String? = "exampleCHANNEL"
    }
}