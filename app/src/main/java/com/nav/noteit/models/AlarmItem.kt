package com.nav.noteit.models

import java.time.LocalDateTime

data class AlarmItem (
    val alarmTime: Long,
    val title: String,
    val description: String,
    val alarmRepetition: Long,
    val noteId: Int
)