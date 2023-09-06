package com.plcoding.cleanarchitecturenoteapp.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.plcoding.cleanarchitecturenoteapp.feature_note.data.data_source.NoteDb
import com.plcoding.cleanarchitecturenoteapp.feature_note.data.repository.NoteRepositoryImpl
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.AddNoteUseCase
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.DeleteNoteUseCase
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.GetNoteUseCase
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.GetNotesUseCase
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDb {
        return Room.databaseBuilder(
            app,
            NoteDb::class.java,
            NoteDb.NOTE_DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(db: NoteDb): NoteRepository = NoteRepositoryImpl(db.noteDao)

    @Provides
    @Singleton
    fun provideUseCase(repository: NoteRepository): NoteUseCases =
        NoteUseCases(
            getNotesUseCase = GetNotesUseCase(
                repository
            ),
            deleteNoteUseCase = DeleteNoteUseCase(
                repository
            ),
            addNoteUseCase = AddNoteUseCase(
                repository
            ),
            getNoteUseCase = GetNoteUseCase(
                repository
            )
        )

}