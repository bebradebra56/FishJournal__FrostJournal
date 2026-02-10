package com.fishjorunal.sofircl.data.repository

import com.fishjorunal.sofircl.data.dao.ChecklistItemDao
import com.fishjorunal.sofircl.data.dao.NoteDao
import com.fishjorunal.sofircl.data.model.ChecklistItem
import com.fishjorunal.sofircl.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val checklistItemDao: ChecklistItemDao
) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    
    fun getNotesByType(type: String): Flow<List<Note>> = noteDao.getNotesByType(type)
    
    fun getNotesByTag(tag: String): Flow<List<Note>> = noteDao.getNotesByTag(tag)
    
    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)
    
    suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)
    
    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)
    
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    
    suspend fun deleteNote(note: Note) {
        checklistItemDao.deleteItemsByNoteId(note.id)
        noteDao.deleteNote(note)
    }
    
    fun getAllTags(): Flow<List<String>> = noteDao.getAllTags()
    
    fun getChecklistItems(noteId: Long): Flow<List<ChecklistItem>> = 
        checklistItemDao.getChecklistItemsByNoteId(noteId)
    
    suspend fun insertChecklistItem(item: ChecklistItem): Long = 
        checklistItemDao.insertItem(item)
    
    suspend fun updateChecklistItem(item: ChecklistItem) = 
        checklistItemDao.updateItem(item)
    
    suspend fun deleteChecklistItem(item: ChecklistItem) = 
        checklistItemDao.deleteItem(item)
}
