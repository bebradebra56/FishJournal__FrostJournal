package com.fishjorunal.sofircl.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noteId: Long,
    val text: String,
    val isCompleted: Boolean = false,
    val position: Int = 0
)
