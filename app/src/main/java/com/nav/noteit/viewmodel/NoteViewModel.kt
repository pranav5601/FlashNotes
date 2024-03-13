package com.nav.noteit.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nav.noteit.dao.NoteDao
import com.nav.noteit.databaseRelations.NoteWithReminder
import com.nav.noteit.repositories.NoteRepo
import com.nav.noteit.room_models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(private val noteRepo: NoteRepo): ViewModel() {


    val allNotes: LiveData<List<Note>> = noteRepo.allNotes


    val allNotesWithReminder: LiveData<List<NoteWithReminder>>? = noteRepo.allNotesWithReminder


    fun deleteNote(note:Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepo.delete(note)
    }

    fun updateNote(note:Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepo.update(note)
    }

    fun addNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepo.insert(note)
    }
}