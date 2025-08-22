package com.project.niyam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.project.niyam.utils.Constants
import dagger.hilt.android.HiltAndroidApp
import kotlin.collections.forEach

@HiltAndroidApp
class ApplicationClass : Application(){
    override fun onCreate() {
        super.onCreate()
        createChannels()
    }
    private fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return

        val nm = ContextCompat.getSystemService(this, NotificationManager::class.java)!!

        nm.createNotificationChannel(
            NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
        )

    }
}
