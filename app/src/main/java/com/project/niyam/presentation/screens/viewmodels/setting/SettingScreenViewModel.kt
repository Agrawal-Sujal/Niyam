package com.project.niyam.presentation.screens.viewmodels.setting

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.project.niyam.domain.export.Export
import com.project.niyam.domain.import.Import2
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingScreenViewModel @Inject constructor(private val exportClass: Export) : ViewModel() {
    val exportState = exportClass.state
    val importStatus = exportClass.importState
    fun export(uri: Uri) {
        exportClass.export(uri)
    }

    fun import(uri: Uri) {
        exportClass.import(uri)
    }


}