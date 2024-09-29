package com.nav.noteit.models


data class AlarmItem (
    val alarmTime: Long,
    val title: String,
    val description: String,
    val alarmRepetition: Long,
    val noteId: Int,
    val reminderId: Int?
)