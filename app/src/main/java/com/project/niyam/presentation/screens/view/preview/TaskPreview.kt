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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
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


//    override fun onStart() {
//        super.onStart()
//        intent1 = intent
//        if (intent1.action == "subTask") {
//            val endTime = intent1.getStringExtra("endTime")
//            val id: String? = intent1.getStringExtra("id")
//            Intent(this, StopWatchService::class.java).also { intent ->
//                intent.action = "subTaskPreview"
//                intent.putExtra("endTime", endTime)
//                intent.putExtra("id", id)
//                bindService(intent, connection, Context.BIND_AUTO_CREATE)
//            }
//        }
//    }

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
                intent1 = intent
                if (intent1.action == "subTask") {
//                    val endTime = intent1.getStringExtra("endTime")
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
//                if (intent1.action == "subTask") {
//                    val id: String? = intent1.getStringExtra("id")
//                    Log.d("Niyam UI",id.toString())
//                    if (isBound) {
//                        PreviewScreen(
//                            stopWatchService = stopwatchService,
//                            id = id.toString().toInt(),
//                        )
//                    }
//                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}

//@AndroidEntryPoint
//class SubTaskPreview : ComponentActivity() {
//    private var isBound by mutableStateOf(false)
//    private var stopwatchService: StopWatchService? = null
//    private var strictTaskService: StrictTaskService? = null
//    private lateinit var intent1: Intent
//    private lateinit var isStrict: String
//    private lateinit var id: String
//
//    private var connection: ServiceConnection? = object : ServiceConnection {
//        override fun onServiceConnected(className: ComponentName, service: IBinder) {
//            val binder = service as StrictTaskService.StrictTaskBinder
//            strictTaskService = binder.getService()
//            isBound = true
//        }
//
//        override fun onServiceDisconnected(arg0: ComponentName) {
//            strictTaskService = null
//            isBound = false
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        intent1 = intent
//        if (intent1.action == "subTask") {
//            id = intent1.getStringExtra("id")!!
//            isStrict = intent1.getStringExtra("Strict")!!
//            val strict = isStrict == "true"
////            init()
//            Log.d("Niyam UI", "$id, $connection,$isBound,$isStrict")
//            if (strict) {
//                Intent(this, StrictTaskService::class.java).also { intent ->
//                    bindService(intent, connection!!, Context.BIND_AUTO_CREATE)
//                }
//                Intent(this, StrictTaskService::class.java).also { intent ->
//                    intent.action = "subTaskPreview"
//                    intent.putExtra("id", id)
//                    this.startForegroundService(intent)
//                }
//            } else {
//                Intent(this, StopWatchService::class.java).also { intent ->
//                    intent.action = "subTaskPreview"
////                        intent.putExtra("endTime", endTime)
//                    intent.putExtra("id", id)
////                            intent.putExtra("isStrict", isStrict.toString())
//                    bindService(intent, connection!!, Context.BIND_AUTO_CREATE)
//                }
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                0
//            )
//        }
//
//        setContent {
//            NiyamTheme {
//                if (isBound) {
//                    if (isStrict == "true") {
//                        PreviewScreen(
//                            stopWatchService = strictTaskService!!,
//                            id = id.toInt()
//                        )
//                    } else {
//                        TaskPreviewScreen(
//                            stopWatchService = stopwatchService!!,
//                            id = id.toInt()
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//
//    private fun init() {
//        if (isStrict == "false") {
//            connection = object : ServiceConnection {
//                override fun onServiceConnected(className: ComponentName, service: IBinder) {
//                    val binder = service as StopWatchService.StopwatchBinder
//                    stopwatchService = binder.getService()
//                    isBound = true
//                }
//
//                override fun onServiceDisconnected(arg0: ComponentName) {
//                    stopwatchService = null
//                    isBound = false
//                }
//            }
//
//        } else {
//            connection = object : ServiceConnection {
//                override fun onServiceConnected(className: ComponentName, service: IBinder) {
//                    val binder = service as StrictTaskService.StrictTaskBinder
//                    strictTaskService = binder.getService()
//                    isBound = true
//                }
//
//                override fun onServiceDisconnected(arg0: ComponentName) {
//                    strictTaskService = null
//                    isBound = false
//                }
//            }
//        }
//    }
//
//
//    override fun onStop() {
//        super.onStop()
//        if (isBound) {
//            unbindService(connection!!)
//            isBound = false
//        }
//    }
//}
