package com.fishjorunal.sofircl.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val tags: String = "", // Comma-separated tags
    val color: String = FishColor.BLUE.name,
    val type: String = NoteType.NOTE.name,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
