package com.example.noteapp.uistate

import com.example.noteapp.data.Note

sealed class NoteUiState {
    object Loading : NoteUiState()
    data class Success(val notes: List<Note>) : NoteUiState()
    object Empty : NoteUiState()
    data class Error(val message: String) : NoteUiState()
}