package com.project.niyam.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.project.niyam.R
import com.project.niyam.data.StrictTaskNotification
import com.project.niyam.data.TaskNotification
import com.project.niyam.utils.Constants.NOTIFICATION_CHANNEL_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule {

    @ServiceScoped
    @Provides
    @TaskNotification
    fun provideNotificationBuilder(
        @ApplicationContext context: Context,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Stopwatch")
            .setContentText("Task is Running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
//            .addAction(0, "Stop", ServiceHelper.stopPendingIntent(context))
//            .addAction(0, "Cancel", ServiceHelper.cancelPendingIntent(context))
//            .setContentIntent(
//                ServiceHelper.clickPendingIntent(
//                    context,
//                    "1",
//                    "17:45",
//                    isStrict = "true"
//                )
//            )
    }

    @ServiceScoped
    @Provides
    @StrictTaskNotification
    fun provideStrictNotificationBuilder(
        @ApplicationContext context: Context,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Stopwatch")
            .setContentText("00:00:00")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
    }

    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context,
    ): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}
