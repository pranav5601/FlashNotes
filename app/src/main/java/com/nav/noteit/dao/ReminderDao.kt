package com.nav.noteit.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nav.noteit.room_models.Reminder
@Dao
interface ReminderDao {


    @Query("SELECT * FROM reminder_table where user_id = :userId order by reminderTimestamp DESC")
    fun getAllReminders(userId: String): List<Reminder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun removeReminder(reminder: Reminder?)

    @Query("DELETE FROM reminder_table WHERE id = :reminderId")
    suspend fun removeReminderById(reminderId: Int?)
}