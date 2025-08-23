package com.project.niyam.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_task",
    foreignKeys = [
        ForeignKey(
            entity = TimeBoundTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["timeBoundTaskId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = FlexibleTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["flexibleTaskId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("timeBoundTaskId"), Index("flexibleTaskId")],
)
data class SubTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timeBoundTaskId: Int? = null, // FK to TimeBoundTask
    val flexibleTaskId: Int? = null, // FK to FlexibleTask
    val isCompleted: Boolean = false,
    val subTaskName: String = "",
    val subTaskDescription: String? = null,
    val remoteId: Int? = null,
    val remoteTaskId: Int? = null,
    val isSynced: Boolean = false,
)
