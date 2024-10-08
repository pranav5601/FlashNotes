package com.nav.noteit.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nav.noteit.room_models.Note

@Dao
interface NoteDao {

    @Query("SELECT * from note_table order by noteId ASC")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert()
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note:Note)

    @Delete
    suspend fun delete(note:Note)
}