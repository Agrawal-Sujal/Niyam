package com.project.niyam.presentation.screens.view.preview

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.project.niyam.domain.services.StopWatchService
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.app.AppCompatActivity;
import com.project.niyam.ui.theme.NiyamTheme

@AndroidEntryPoint
class SubTaskPreview : ComponentActivity() {
    private var isBound by mutableStateOf(false)
    private lateinit var stopwatchService: StopWatchService
    private lateinit var intent1: Intent
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as StopWatchService.StopwatchBinder
            stopwatchService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        intent1 = intent
        if (intent1.action == "subTask") {
            val endTime = intent1.getStringExtra("endTime")
            val id: String? = intent1.getStringExtra("id")
            Intent(this, StopWatchService::class.java).also { intent ->
                intent.action = "subTaskPreview"
                intent.putExtra("endTime", endTime)
                intent.putExtra("id", id)
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        setContent {
            NiyamTheme {

                if (intent1.action == "subTask") {
                    val id: String? = intent1.getStringExtra("id")
                    if (isBound) {

                        PreviewScreen(
                            stopWatchService = stopwatchService,
                            id = id.toString().toInt()
                        )
                    }
                }

            }
        }
    }


    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}