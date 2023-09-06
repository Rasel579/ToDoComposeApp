package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.AddEditNoteEvent
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.NoteTextFieldState
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _noteTitleState = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter title..."
        )
    )
    val noteTitleState: State<NoteTextFieldState> = _noteTitleState

    private val _noteContentState = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter content..."
        )
    )
    val noteContentState: State<NoteTextFieldState> = _noteContentState

    private val _noteColorState = mutableStateOf<Int>(Note.noteColors.random().toArgb())
    val noteColorState: State<Int> = _noteColorState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow

    private var currentNoteId: Int? = null

    init {
        savedStateHandle.get<Int>("noteId").let { noteId ->
            if (noteId != -1 ){
                viewModelScope.launch {
                    if (noteId != null) {
                        noteUseCases.getNoteUseCase(noteId)?.also { note ->
                            currentNoteId = note.id
                            _noteTitleState.value = noteTitleState.value.copy(
                                text = note.title,
                                isHintVisible = false
                            )
                            _noteContentState.value = noteContentState.value.copy(
                                text = note.content,
                                isHintVisible = false
                            )
                            _noteColorState.value = note.color
                        }
                    }
                }
            }
        }
    }
    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitleState.value = noteTitleState.value.copy(
                    text = event.title
                )
            }

            is AddEditNoteEvent.EnteredContent -> {
                _noteContentState.value = noteContentState.value.copy(
                    text = event.content
                )
            }

            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitleState.value = noteTitleState.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteTitleState.value.text.isBlank()
                )
            }

            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContentState.value = noteContentState.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteContentState.value.text.isBlank()
                )
            }

            is AddEditNoteEvent.ChangeColor -> {
                _noteColorState.value = event.color
            }

            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNoteUseCase(
                            Note(
                                title = noteTitleState.value.text,
                                content = noteContentState.value.text,
                                timestamp = System.currentTimeMillis(),
                                color = noteColorState.value,
                                id = currentNoteId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveEvent)
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackBar(
                                message = e.message ?: "Unknown error, couldn't save note"
                            )
                        )
                    }
                }
            }

        }
    }
}