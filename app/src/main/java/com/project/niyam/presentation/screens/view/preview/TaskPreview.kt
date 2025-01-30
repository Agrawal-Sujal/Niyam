package com.project.niyam.presentation.screens.view.preview

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
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
import com.project.niyam.domain.services.StopWatchService
import com.project.niyam.domain.services.StopwatchState
import com.project.niyam.domain.services.StrictTaskService
import com.project.niyam.ui.theme.NiyamTheme
import com.project.niyam.utils.Constants.STOPWATCH_STATE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskPreview : ComponentActivity() {
    private var isBound by mutableStateOf(false)
    private lateinit var stopwatchService: StopWatchService
    private lateinit var strictTaskService: StrictTaskService
    private lateinit var intent1: Intent
    private lateinit var isStrict: String
    private lateinit var connection: ServiceConnection

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
                        isStrict = intent1.getStringExtra("Strict")!!
                        val strict: Boolean = isStrict == "true"
                        Log.d("Niyam UI", id!!)
                        connection = if (strict) {
                            object : ServiceConnection {
                                override fun onServiceConnected(
                                    className: ComponentName,
                                    service: IBinder
                                ) {
                                    val binder = service as StrictTaskService.StrictTaskBinder
                                    strictTaskService = binder.getService()
                                    isBound = true
                                }

                                override fun onServiceDisconnected(arg0: ComponentName) {
                                    isBound = false
                                }
                            }
                        } else {
                            object : ServiceConnection {
                                override fun onServiceConnected(
                                    className: ComponentName,
                                    service: IBinder
                                ) {
                                    val binder = service as StopWatchService.StopwatchBinder
                                    stopwatchService = binder.getService()
                                    isBound = true
                                }

                                override fun onServiceDisconnected(arg0: ComponentName) {
                                    isBound = false
                                }
                            }
                        }
                        if (strict) {
                            Intent(this, StrictTaskService::class.java).also { intent ->
                                bindService(intent, connection, Context.BIND_AUTO_CREATE)
                            }

                            Intent(this, StrictTaskService::class.java).also { intent ->
                                intent.action = "subTaskPreview"
                                intent.putExtra("id", id)
                                this.startService(intent)
                            }
                        } else {
                            Intent(this, StopWatchService::class.java).also { intent ->
                                bindService(intent, connection, Context.BIND_AUTO_CREATE)
                            }

                            Intent(this, StopWatchService::class.java).also { intent ->
                                intent.action = "subTaskPreview"
                                intent.putExtra("id", id)
                                intent.putExtra(STOPWATCH_STATE, StopwatchState.Entered.name)
                                this.startService(intent)
                            }
                        }


                        if (isBound) {
                            if (strict)
                                PreviewScreen(
                                    stopWatchService = strictTaskService,
                                    id = id.toString().toInt()
                                )
                            else
                                TaskPreviewScreen(
                                    stopWatchService = stopwatchService,
                                    id = id.toString().toInt()
                                )
                        }
                    }
                }
            }
        }
    }

        override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}

//package com.project.niyam.presentation.screens.view.preview
//
//import android.Manifest
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.content.ServiceConnection
//import android.os.Build
//import android.os.Bundle
//import android.os.IBinder
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.annotation.RequiresApi
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.res.colorResource
//import androidx.core.app.ActivityCompat
//import com.project.niyam.R
//import com.project.niyam.domain.services.StopWatchService
//import com.project.niyam.domain.services.StopwatchState
//import com.project.niyam.domain.services.StrictTaskService
//import com.project.niyam.ui.theme.NiyamTheme
//import com.project.niyam.utils.Constants.STOPWATCH_STATE
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class TaskPreview : ComponentActivity() {
//    private var isBound by mutableStateOf(false)
//    private var stopwatchService: StopWatchService? = null
//    private var strictTaskService: StrictTaskService? = null
//    private var connection: ServiceConnection? = null
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                0,
//            )
//        }
//
//        setContent {
//            NiyamTheme {
//                Surface(color = colorResource(R.color.BackGroundColor)) {
//                    val intent1 = intent
//                    if (intent1.action == "subTask") {
//                        val id: String? = intent1.getStringExtra("id")
//                        val isStrict = intent1.getStringExtra("Strict") == "true"
//
//                        if (id != null) {
//                            Log.d("Niyam UI", id)
//
//                            connection = object : ServiceConnection {
//                                override fun onServiceConnected(className: ComponentName, service: IBinder) {
//                                    if (isStrict) {
//                                        val binder = service as StrictTaskService.StrictTaskBinder
//                                        strictTaskService = binder.getService()
//                                    } else {
//                                        val binder = service as StopWatchService.StopwatchBinder
//                                        stopwatchService = binder.getService()
//                                    }
//                                    isBound = true
//                                }
//
//                                override fun onServiceDisconnected(arg0: ComponentName) {
//                                    isBound = false
//                                    strictTaskService = null
//                                    stopwatchService = null
//                                }
//                            }
//
//                            val serviceIntent = if (isStrict) {
//                                Intent(this, StrictTaskService::class.java)
//                            } else {
//                                Intent(this, StopWatchService::class.java)
//                            }
//
//                            bindService(serviceIntent, connection!!, Context.BIND_AUTO_CREATE)
//
//                            serviceIntent.action = "subTaskPreview"
//                            serviceIntent.putExtra("id", id)
//                            if (!isStrict) {
//                                serviceIntent.putExtra(STOPWATCH_STATE, StopwatchState.Entered.name)
//                            }
//                            startService(serviceIntent)
//
//                            if (isBound) {
//                                if (isStrict) {
//                                    PreviewScreen(
//                                        stopWatchService = strictTaskService!!,
//                                        id = id.toInt()
//                                    )
//                                } else {
//                                    TaskPreviewScreen(
//                                        stopWatchService = stopwatchService!!,
//                                        id = id.toInt()
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (isBound && connection != null) {
//            unbindService(connection!!)
//            isBound = false
//        }
//        connection = null
//    }
//}
