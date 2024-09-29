package com.nav.noteit.models

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Note(
    @SerializedName("noteId")
    var noteId: Int,
    @SerializedName("user_id")
    var userId: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("timeStamp")
    var timeStamp: Long,
    @SerializedName("type")
    var type: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("isReminderSet")
    var isReminderSet: Boolean,
    @SerializedName("noteImages")
    var noteImages: String

)


data class Reminder(
    @SerializedName("id")
    var id: Int,
    @SerializedName("user_id")
    var userId: String,
    @SerializedName("noteId")
    var noteId: Int,
    @SerializedName("reminderTime")
    var reminderTime: String,
    @SerializedName("reminderDate")
    var reminderDate: String,
    @SerializedName("reminderRepetition")
    var reminderRepetition: String,
    @SerializedName("reminderLat")
    var reminderLat: Long,
    @SerializedName("reminderLong")
    var reminderLong: Long,
    @SerializedName("reminderTimestamp")
    var reminderTimestamp: Long,
)

data class UserResponse(
    @SerializedName("full_name")val fullName: String,
    @SerializedName("email")val emailId: String,
    @SerializedName("password")val password: String,
    @SerializedName("country")val country: String,
    @SerializedName("userId")val userId: String,
    @SerializedName("api_token")val apiToken: String
)

data class UserReq(
    @SerializedName("full_name")val fullName: String,
    @SerializedName("email")val emailId: String,
    @SerializedName("password")val password: String,
    @SerializedName("country")val country: String
)


data class UserLogin(
    @SerializedName("email")val emailId: String,
    @SerializedName("password")val password: String,
)
