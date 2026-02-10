package com.fishjorunal.sofircl.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fishjorunal.sofircl.data.dao.ChecklistItemDao
import com.fishjorunal.sofircl.data.dao.NoteDao
import com.fishjorunal.sofircl.data.model.ChecklistItem
import com.fishjorunal.sofircl.data.model.Note

@Database(
    entities = [Note::class, ChecklistItem::class],
    version = 1,
    exportSchema = false
)
abstract class FishJournalDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun checklistItemDao(): ChecklistItemDao
    
    companion object {
        @Volatile
        private var INSTANCE: FishJournalDatabase? = null
        
        fun getDatabase(context: Context): FishJournalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FishJournalDatabase::class.java,
                    "fish_journal_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
