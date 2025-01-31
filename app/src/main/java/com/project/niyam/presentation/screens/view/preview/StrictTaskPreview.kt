package com.project.niyam.presentation.screens.view.preview

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.colorResource
import androidx.core.app.ActivityCompat
import com.project.niyam.R
import com.project.niyam.domain.services.StrictTaskService
import com.project.niyam.ui.theme.NiyamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StrictTaskPreview : ComponentActivity() {
    private var isBound by mutableStateOf(false)
    private lateinit var strictTaskService: StrictTaskService
    private lateinit var intent1: Intent
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as StrictTaskService.StrictTaskBinder
            strictTaskService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, StrictTaskService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0,
            )
        }

        setContent {
            NiyamTheme {
                Surface(color = colorResource(R.color.BackGroundColor)) {
                    intent1 = intent
                    if (intent1.action == "subTask") {
                        val id: String? = intent1.getStringExtra("id")

                        if (isBound) {
                            Intent(this, StrictTaskService::class.java).also { intent ->
                                intent.action = "subTaskPreview"
                                intent.putExtra("id", id)
                                this.startService(intent)
                            }
                            PreviewScreen(
                                stopWatchService = strictTaskService,
                                id = id.toString().toInt(),
                            )
                        }
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
