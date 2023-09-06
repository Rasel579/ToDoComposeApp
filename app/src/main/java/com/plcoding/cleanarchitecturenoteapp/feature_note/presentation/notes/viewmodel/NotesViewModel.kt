package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NoteOrder
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.OrderType
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.NoteEvent
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.NotesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val useCases: NoteUseCases
) : ViewModel() {
    private val _state = mutableStateOf<NotesState>(NotesState())
    private var getNotesJob: Job? = null
    val state: State<NotesState> = _state


    private var recentlyDeletedNote: Note? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun onEvent(event: NoteEvent) {
        when (event) {
            is NoteEvent.Order -> {
                if (state.value.noteOrder::class == event.noteOrder::class
                    && state.value.noteOrder.orderType == event.noteOrder.orderType
                ) {
                    return
                }
                getNotes(event.noteOrder)
            }

            is NoteEvent.DeleteNote -> {
                viewModelScope.launch {
                    useCases.deleteNoteUseCase(event.note)
                    recentlyDeletedNote = event.note
                }
            }

            is NoteEvent.RestoreNote -> {
                viewModelScope.launch {
                    useCases.addNoteUseCase.invoke(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }

            is NoteEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()

        getNotesJob = useCases.getNotesUseCase(noteOrder)
            .onEach { notes ->
                _state.value = state.value.copy(
                    notes = notes,
                    noteOrder = noteOrder
                )
            }
            .launchIn(viewModelScope)
    }
}