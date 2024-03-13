package com.nav.noteit.models

import com.google.gson.annotations.SerializedName

data class Note(
    @SerializedName("id")
    var id: String,
    @SerializedName("title")
    var title: Int,
    @SerializedName("timeStamp")
    var timeStamp: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("isReminderSet")
    var isReminderSet: Boolean,

)


data class Reminder(
    @SerializedName("id")
    var id: String,
    @SerializedName("note_id")
    var noteId: String,
    @SerializedName("reminderTime")
    var reminderTime: String,
    @SerializedName("reminderDate")
    var reminderDate: String,
    @SerializedName("reminderRepetition")
    var reminderRepetition: String,
    @SerializedName("reminderLat")
    var reminderLat: String,
    @SerializedName("reminderLong")
    var reminderLong: String,
)