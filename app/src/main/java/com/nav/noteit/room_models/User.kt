package com.nav.noteit.room_models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_data")
data class User(
    @ColumnInfo(name = "full_name")val fullName: String,
    @ColumnInfo(name = "email")val emailId: String,
    @ColumnInfo(name = "reminderTime")val password: String,
    @PrimaryKey(autoGenerate = false)val userId: String,
    @ColumnInfo(name = "api_token")val apiToken: String
)
