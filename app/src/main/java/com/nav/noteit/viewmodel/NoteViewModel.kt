package com.nav.noteit.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.nav.noteit.databaseRelations.NoteWithReminder
import com.nav.noteit.remote.repositories.NoteListRepo
import com.nav.noteit.repositories.NoteRepo
import com.nav.noteit.room_models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class NoteViewModel(private val noteRepo: NoteRepo, private val noteListRepo: NoteListRepo): ViewModel() {


    val allNotes: LiveData<List<Note>> = noteRepo.allNotes
    val allNoteData: MutableLiveData<List<com.nav.noteit.models.Note>> = MutableLiveData()
    var note : com.nav.noteit.models.Note? = null
    var gson = Gson()


    suspend fun createNoteOnline(note:com.nav.noteit.models.Note)= viewModelScope.launch(Dispatchers.IO){

        try {
            val noteRes = async { noteListRepo.createNote(note) }.await()
            Log.e("Success: createNote",gson.toJson(noteRes) )
        }catch (e: Exception){

            Log.e("Error: createNote", e.message.toString())
        }


    }

    suspend fun getAllNotes(userId: String) = viewModelScope.launch {

        try {
            val allNotes = async { noteListRepo.getAllNotes(userId) }
            allNoteData.postValue(allNotes.await())

            Log.e("Success: getAllNotes", gson.toJson(allNoteData))

        }catch (e: IOException){
            Log.e("Error: getAllNotes", e.message.toString())
        }catch (e: Exception){
            Log.e("Error: getAllNotes", e.message.toString())
        }

    }



//    private var _allNotesWithReminder:MutableLiveData<List<NoteWithReminder>> = MutableLiveData()
//    val allNotesWithReminder: LiveData<List<NoteWithReminder>>
//        get() = _allNotesWithReminder

//    suspend fun allNotesWithReminder(userId: String, noteReminderCoroutineScope: CoroutineContext) = withContext(noteReminderCoroutineScope){
//
//        val notes = async{ noteRepo.allNotesWithReminder(userId) }
//        _allNotesWithReminder.postValue( notes.await())
//    }


    fun allNotesWithReminder(userId: String) :LiveData<List<NoteWithReminder>>{

       return noteRepo.allNotesWithReminder(userId)

    }

    fun findNoteById(noteId: String): LiveData<NoteWithReminder>{
        return noteRepo.getNoteDataById(noteId)
    }

    suspend fun deleteNote(note:Note) = viewModelScope.launch (Dispatchers.IO) {
        noteRepo.delete(note)
    }

    suspend fun updateNote(note: Note) = viewModelScope.launch (Dispatchers.IO) {
        noteRepo.update(note)
    }

    suspend fun addNote(note: Note) = viewModelScope.launch (Dispatchers.IO){
        noteRepo.insert(note)
    }


}