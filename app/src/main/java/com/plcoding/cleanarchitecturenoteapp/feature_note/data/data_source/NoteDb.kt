package com.plcoding.cleanarchitecturenoteapp.feature_note.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note

@Database(
    entities = [Note::class],
    version = 2
)
abstract class NoteDb: RoomDatabase() {
    abstract val noteDao: NoteDao

    companion object {
        const val NOTE_DB_NAME = "notes_db"
    }
}