package com.example.noteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.Note
import com.example.noteapp.repository.NoteRepository
import com.example.noteapp.uistate.AddEditUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AddEditViewmodel @Inject constructor(private val repository: NoteRepository): ViewModel() {
    private val _uiState = MutableStateFlow<AddEditUiState>(AddEditUiState.Loading)
    val uiState: StateFlow<AddEditUiState> = _uiState

    fun saveNote(note: Note){
        _uiState.value = AddEditUiState.Loading
        viewModelScope.launch {
            try{
                if (note.title.isBlank()) {
                    _uiState.value = AddEditUiState.Error("Title required")
                    return@launch
                }
                val note = Note(
                    id = note.id ?: 0,
                    title = note.title,
                    description = note.description
                )
                if (note.id == 0) repository.insert(note)
                else repository.update(note)

                _uiState.value = AddEditUiState.Saved

            }catch (e: Exception){
                _uiState.value = AddEditUiState.Error(e.message ?: "Error")
            }

        }
    }
    fun delete(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
            _uiState.value = AddEditUiState.Saved
        }
    }
}