package com.example.noteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.data.Note
import com.example.noteapp.repository.NoteRepository
import com.example.noteapp.uistate.NoteUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesListViewmodel @Inject constructor(private val repository: NoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteUiState>(NoteUiState.Loading)
    val uiState: StateFlow<NoteUiState> = _uiState


    private var currentQuery: String = ""
    private var searchJob: Job? = null

    init {
        loadNotes()
    }

    private fun loadNotes() {
        searchJob?.cancel()
        _uiState.value = NoteUiState.Loading
        searchJob = viewModelScope.launch {
            try {
                val notesList = if (currentQuery.isBlank()) {
                    repository.getNotes()
                } else {
                    repository.searchNotes(currentQuery)
                }
                notesList.collect { notes ->
                    _uiState.value =
                        if (notes.isEmpty()) NoteUiState.Empty
                        else NoteUiState.Success(notes)
                }
            } catch (e: Exception) {
                _uiState.value = NoteUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun searchNotes(query: String) {
        currentQuery = query.trim()
        loadNotes()
    }


}