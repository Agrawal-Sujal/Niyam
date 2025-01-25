package com.project.niyam.domain.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.project.niyam.presentation.screens.view.preview.TaskPreview
import com.project.niyam.utils.Constants.CANCEL_REQUEST_CODE
import com.project.niyam.utils.Constants.CLICK_REQUEST_CODE
import com.project.niyam.utils.Constants.RESUME_REQUEST_CODE
import com.project.niyam.utils.Constants.STOPWATCH_STATE
import com.project.niyam.utils.Constants.STOP_REQUEST_CODE

object ServiceHelper {

    private const val FLAG =
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE


    fun strictClickPendingIntent(context: Context, id: String): PendingIntent {
        val clickIntent = Intent(context, TaskPreview::class.java).apply {
            Log.d("ServiceHelper", id)
            putExtra("id", id)
            putExtra("Strict", "true")
        }
        clickIntent.action = "subTask"
        return PendingIntent.getActivity(
            context,
            CLICK_REQUEST_CODE,
            clickIntent,
            FLAG,
        )
    }

    fun clickPendingIntent(
        context: Context,
        id: String,
    ): PendingIntent {
        val clickIntent = Intent(context, TaskPreview::class.java).apply {
            Log.d("ServiceHelper", id)
            putExtra(STOPWATCH_STATE, StopwatchState.Started.name)
            putExtra("id", id)
            putExtra("Strict", "false")
        }

        clickIntent.action = "subTask"
        return PendingIntent.getActivity(
            context,
            CLICK_REQUEST_CODE,
            clickIntent,
            FLAG,
        )
    }

    fun stopPendingIntent(context: Context): PendingIntent {
        val stopIntent = Intent(context, StopWatchService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Stopped.name)
        }
        stopIntent.action = "subTaskPreview"
        return PendingIntent.getService(
            context,
            STOP_REQUEST_CODE,
            stopIntent,
            FLAG,
        )
    }

    fun resumePendingIntent(context: Context): PendingIntent {
        val resumeIntent = Intent(context, StopWatchService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Started.name)
        }
        resumeIntent.action = "subTaskPreview"
        return PendingIntent.getService(
            context,
            RESUME_REQUEST_CODE,
            resumeIntent,
            FLAG,
        )
    }

    fun cancelPendingIntent(context: Context): PendingIntent {
        val cancelIntent = Intent(context, StopWatchService::class.java).apply {
            putExtra(STOPWATCH_STATE, StopwatchState.Canceled.name)
        }
        cancelIntent.action = "subTaskPreview"
        return PendingIntent.getService(
            context,
            CANCEL_REQUEST_CODE,
            cancelIntent,
            FLAG,
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun triggerForegroundService(context: Context, msg: String) {
        Intent(context, StopWatchService::class.java).apply {
            putExtra(STOPWATCH_STATE, msg)
            action = "subTaskPreview"
            context.startForegroundService(this)
        }
    }
}
