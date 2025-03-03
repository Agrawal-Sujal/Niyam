package com.project.niyam.presentation.screens.view.setting

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.niyam.domain.export.ExportStatus
import com.project.niyam.domain.export.ImportStatus
import com.project.niyam.presentation.screens.viewmodels.setting.SettingScreenViewModel

@Composable
fun Setting(viewModel: SettingScreenViewModel = hiltViewModel()) {
    val exportState = viewModel.exportState
    val importState = viewModel.importStatus
    val selectLocation = remember { mutableStateOf(false) }
    val selectFile = remember { mutableStateOf(false) }
    if (selectLocation.value) {
        SelectLocation(onUriSelected = {
            viewModel.export(it)
        })
    }
    if (selectFile.value) {
        SelectFile(onFileSelected = { viewModel.import(it) })
    }
    Column {
        Row {
            Button(onClick = {
                selectLocation.value = true
            }) {
                Text("Export")
            }
            if (exportState.value == ExportStatus.RUNNING)
                CircularProgressIndicator()
            else if (exportState.value == ExportStatus.COMPLETED) Icon(Icons.Default.Check, null)
            else if (exportState.value == ExportStatus.ERROR) {
                Icon(Icons.Default.Close, null)
            }
        }
        Row {
            Button(onClick = {
                selectFile.value = true
            }) {
                Text("Import")
            }
            if (importState.value == ImportStatus.RUNNING)
                CircularProgressIndicator()
            else if (importState.value == ImportStatus.COMPLETED) Icon(Icons.Default.Check, null)
            else if (importState.value == ImportStatus.ERROR) {
                Icon(Icons.Default.Close, null)
            }
        }
    }
}

@Composable
fun SelectLocation(onUriSelected: (Uri) -> Unit) {
    val context = LocalContext.current
    var folderUri by remember { mutableStateOf<Uri?>(null) }
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                folderUri = uri

                // Persist permission for future access
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                onUriSelected(uri)
                Log.d("FolderPicker", "Selected Folder: $uri")
            }
        }
    }
    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        folderPickerLauncher.launch(intent)
    }
}

@Composable
fun SelectFile(onFileSelected: (Uri) -> Unit) {

    val context = LocalContext.current
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher to open file picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedFileUri = uri
                onFileSelected(uri)
                Log.d("FilePicker", "Selected File URI: $uri")
            }
        }
    }
    LaunchedEffect(Unit) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type =
                "text/plain"
        }
        filePickerLauncher.launch(intent)
    }
}