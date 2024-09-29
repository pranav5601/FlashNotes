package com.nav.noteit.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nav.noteit.dao.NoteDao
import com.nav.noteit.dao.ReminderDao
import com.nav.noteit.dao.UserDao
import com.nav.noteit.databaseRelations.NoteWithReminderDao
import com.nav.noteit.room_models.ListToStringTypeConverter
import com.nav.noteit.room_models.Note
import com.nav.noteit.room_models.Reminder
import com.nav.noteit.room_models.User

@Database(entities = [Note::class, Reminder::class, User::class], version = 7, exportSchema = false)
@TypeConverters(ListToStringTypeConverter::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao
    abstract fun getReminderDao(): ReminderDao
    abstract fun getNoteWithReminderDao(): NoteWithReminderDao

    abstract fun getUserDao(): UserDao



    /*companion object {
        @Volatile
        private var databaseInstance: NoteDatabase? = null

        fun getDatabaseInstance(context: Context): NoteDatabase{
            return databaseInstance?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                ).build()
                databaseInstance = instance
                instance
            }
        }

    }*/

}