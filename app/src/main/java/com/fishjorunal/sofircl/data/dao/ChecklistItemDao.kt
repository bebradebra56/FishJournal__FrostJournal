package com.fishjorunal.sofircl.data.dao

import androidx.room.*
import com.fishjorunal.sofircl.data.model.ChecklistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDao {
    @Query("SELECT * FROM checklist_items WHERE noteId = :noteId ORDER BY position ASC")
    fun getChecklistItemsByNoteId(noteId: Long): Flow<List<ChecklistItem>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItem): Long
    
    @Update
    suspend fun updateItem(item: ChecklistItem)
    
    @Delete
    suspend fun deleteItem(item: ChecklistItem)
    
    @Query("DELETE FROM checklist_items WHERE noteId = :noteId")
    suspend fun deleteItemsByNoteId(noteId: Long)
}
