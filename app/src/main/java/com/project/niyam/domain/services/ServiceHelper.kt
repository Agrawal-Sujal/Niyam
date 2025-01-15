package com.project.niyam.domain.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.project.niyam.presentation.screens.view.preview.SubTaskPreview
import com.project.niyam.utils.Constants.CANCEL_REQUEST_CODE
import com.project.niyam.utils.Constants.CLICK_REQUEST_CODE
import com.project.niyam.utils.Constants.RESUME_REQUEST_CODE
import com.project.niyam.utils.Constants.STOPWATCH_STATE
import com.project.niyam.utils.Constants.STOP_REQUEST_CODE

object ServiceHelper {

    private const val flag =
        PendingIntent.FLAG_IMMUTABLE

    fun clickPendingIntent(context: Context, id: String, endTime: String): PendingIntent {
        val clickIntent = Intent(context, SubTaskPreview::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Started.name)
            putExtra("id", id)
            putExtra("endTime", endTime)
        }
        clickIntent.action = "subTask"
        return PendingIntent.getActivity(
            context,
            CLICK_REQUEST_CODE,
            clickIntent,
            flag,
        )
    }

    fun stopPendingIntent(context: Context): PendingIntent {
        val stopIntent = Intent(context, StopWatchService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Stopped.name)
        }
        return PendingIntent.getService(
            context,
            STOP_REQUEST_CODE,
            stopIntent,
            flag,
        )
    }

    fun resumePendingIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, StopWatchService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Started.name)
        }
        return PendingIntent.getService(
            context,
            RESUME_REQUEST_CODE,
            resumeIntent,
            flag,
        )
    }

    fun cancelPendingIntent(context: Context): PendingIntent {
        val cancelIntent = Intent(context, StopWatchService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Canceled.name)
        }
        return PendingIntent.getService(
            context,
            CANCEL_REQUEST_CODE,
            cancelIntent,
            flag,
        )
    }

    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, StopWatchService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}
