package com.project.niyam.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GeneralInfo(
    @PrimaryKey
    val id: Int = 0,
    val strictTaskRunningId: Int,
    val normalTaskRunningId: Int,
)
