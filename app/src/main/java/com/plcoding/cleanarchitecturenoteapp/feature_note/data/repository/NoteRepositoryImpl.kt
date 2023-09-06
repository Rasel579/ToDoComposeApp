package com.plcoding.cleanarchitecturenoteapp.feature_note.data.repository

import com.plcoding.cleanarchitecturenoteapp.feature_note.data.data_source.NoteDao
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val source: NoteDao
): NoteRepository {
    override fun getNotes(): Flow<List<Note>> = source.getNotes()

    override suspend fun getNoteById(id: Int): Note? = source.getNoteById(id)

    override suspend fun insertNote(note: Note) = source.insertNote(note)

    override suspend fun deleteNote(note: Note) = source.deleteNote(note)
}