package com.nav.noteit.databaseRelations

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface NoteWithReminderDao {

    @Transaction
    @Query("SELECT * FROM note_table")
    fun getNoteWithReminder(): LiveData<List<NoteWithReminder>>?



}