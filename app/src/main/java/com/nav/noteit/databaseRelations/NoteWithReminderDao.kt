package com.nav.noteit.databaseRelations

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface NoteWithReminderDao {

    @Transaction
    @Query("SELECT * FROM note_table WHERE user_id = :userId order by timeStamp DESC")
    fun getNoteWithReminder(userId: String): LiveData<List<NoteWithReminder>>

    @Transaction
    @Query("SELECT * FROM note_table WHERE noteId = :noteId")
    fun getNoteWithReminderByNoteId(noteId: Int): LiveData<NoteWithReminder>


}