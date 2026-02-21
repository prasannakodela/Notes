package com.example.noteapp.uistate

sealed class AddEditUiState {

    object Loading : AddEditUiState()
    object Saved : AddEditUiState()
    data class Error(val message: String) : AddEditUiState()
}