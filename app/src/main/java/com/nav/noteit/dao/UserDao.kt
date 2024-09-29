package com.nav.noteit.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nav.noteit.room_models.Reminder
import com.nav.noteit.room_models.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user_data")
    suspend fun getUserInfo(): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT userId FROM user_data")
    suspend fun getUserId(): String?

    @Update
    suspend fun updateUser(reminder: User)

    @Delete
    suspend fun removeUser(reminder: User)

}