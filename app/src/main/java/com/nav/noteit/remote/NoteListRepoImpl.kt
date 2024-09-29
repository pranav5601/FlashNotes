package com.nav.noteit.remote

import android.util.Log
import androidx.lifecycle.LiveData
import com.nav.noteit.database.NoteDatabase
import com.nav.noteit.models.Note
import com.nav.noteit.remote.repositories.NoteListRepo
import java.lang.Exception

class NoteListRepoImpl(
    private val api: ApiInterface,
    private val db: NoteDatabase): NoteListRepo {

        val noteDao = db.getNoteDao()
    override suspend fun getAllNotes(userID: String): List<Note>{
        return try {
            api.getNotes()
        }catch (e: Exception){
            Log.e("Error: getAllNotes", e.message.toString())
            listOf<Note>()
        }


    }

    override suspend fun createNote(note: Note): String {
        return try {
            api.createNote(note)
        }catch (e: Exception){
            Log.e("Error: createNote", e.message.toString())
            return e.message.toString()
        }
    }


}