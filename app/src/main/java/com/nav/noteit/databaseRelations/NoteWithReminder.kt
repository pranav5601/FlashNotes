package com.nav.noteit.databaseRelations

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.nav.noteit.room_models.Note
import com.nav.noteit.room_models.Reminder


data class NoteWithReminder (
    @Embedded val note: Note,
    @Relation(
        parentColumn = "noteId",
        entityColumn = "noteId"
    ) val reminder: Reminder?
)