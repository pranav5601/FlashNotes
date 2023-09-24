package com.nav.noteit.room_models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "note_table")
class Note(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "timeStamp") val timeStamp: Long,
    @TypeConverters(ListToStringTypeConverter::class)
    @ColumnInfo(name = "noteImages") val imageList: String,
    @PrimaryKey(autoGenerate = true) val id: Int?

)

