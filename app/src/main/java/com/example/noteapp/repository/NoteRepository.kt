package com.example.noteapp.repository

import com.example.noteapp.data.Note
import com.example.noteapp.data.NoteDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(private val dao: NoteDao) {
    fun getNotes(): Flow<List<Note>> = dao.getAllNotes()

    fun searchNotes(query: String): Flow<List<Note>> =
        if (query.isBlank()) dao.getAllNotes()
        else dao.searchNotes(query)

    suspend fun insert(note: Note) = dao.insert(note)

    suspend fun update(note: Note) = dao.update(note)

    suspend fun delete(note: Note) = dao.delete(note)
}