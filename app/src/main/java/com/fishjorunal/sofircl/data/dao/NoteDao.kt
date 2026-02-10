package com.fishjorunal.sofircl.data.dao

import androidx.room.*
import com.fishjorunal.sofircl.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): Note?
    
    @Query("SELECT * FROM notes WHERE type = :type ORDER BY updatedAt DESC")
    fun getNotesByType(type: String): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE tags LIKE '%' || :tag || '%' ORDER BY updatedAt DESC")
    fun getNotesByTag(tag: String): Flow<List<Note>>
    
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchNotes(query: String): Flow<List<Note>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long
    
    @Update
    suspend fun updateNote(note: Note)
    
    @Delete
    suspend fun deleteNote(note: Note)
    
    @Query("SELECT DISTINCT tags FROM notes WHERE tags != ''")
    fun getAllTags(): Flow<List<String>>
}
