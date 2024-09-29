package com.nav.noteit.room_models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_table")
class Reminder(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    @ColumnInfo(name = "noteId") val noteId: Int,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "reminderTime") val reminderTime: String,
    @ColumnInfo(name = "reminderDate") val reminderDate: String,
    @ColumnInfo(name = "reminderRepetition") val reminderRepetition: Long,
    @ColumnInfo(name = "reminderTimestamp") val reminderTimestamp: Long,
    @ColumnInfo(name = "reminderLat") val reminderLat: Long,
    @ColumnInfo(name = "reminderLong") val reminderLong: Long,
)
