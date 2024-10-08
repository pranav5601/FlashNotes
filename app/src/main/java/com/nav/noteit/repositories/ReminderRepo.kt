package com.nav.noteit.repositories

import androidx.lifecycle.LiveData
import com.nav.noteit.database.NoteDatabase
import com.nav.noteit.room_models.Reminder

class ReminderRepo(private val noteDatabase: NoteDatabase) {

    private val reminderDao = noteDatabase.getReminderDao()

    fun getAllReminder(userId: String):List<Reminder>{
        return reminderDao.getAllReminders(userId)
    }

    suspend fun insertReminder(reminder: Reminder){
        reminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder){
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder?){
        reminderDao.removeReminder(reminder)
    }

    suspend fun removeReminderById(reminderId: Int?) {
        reminderDao.removeReminderById(reminderId)
    }


}