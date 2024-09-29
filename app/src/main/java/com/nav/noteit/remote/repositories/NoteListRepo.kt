package com.nav.noteit.remote.repositories

import androidx.lifecycle.LiveData
import com.nav.noteit.models.Note

interface NoteListRepo {

    suspend fun getAllNotes(
        userID: String
    ): List<Note>

    suspend fun createNote(
        note: Note
    ): String
}