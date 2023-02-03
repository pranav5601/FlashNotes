package com.nav.noteit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nav.noteit.dao.NoteDao
import com.nav.noteit.room_models.Note

@Database(entities = arrayOf(Note::class), version = 2, exportSchema = false)
abstract class NoteDatabase : RoomDatabase(){

    abstract fun getNoteDao(): NoteDao

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