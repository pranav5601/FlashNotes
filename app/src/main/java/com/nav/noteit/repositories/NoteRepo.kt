package com.nav.noteit.repositories

import androidx.lifecycle.LiveData
import com.nav.noteit.dao.NoteDao
import com.nav.noteit.database.NoteDatabase
import com.nav.noteit.room_models.Note

class NoteRepo(private val noteDatabase: NoteDatabase) {


    private val noteDao = noteDatabase.getNoteDao()

    val allNotes:LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note:Note){
        noteDao.insertNote(note)
    }

    suspend fun delete(note: Note){
        noteDao.delete(note)
    }
    suspend fun update(note:Note){
        noteDao.updateNote(note)
    }

}