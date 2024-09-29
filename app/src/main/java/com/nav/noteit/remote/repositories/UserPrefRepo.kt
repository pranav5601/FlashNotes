package com.nav.noteit.remote.repositories

import androidx.datastore.preferences.core.Preferences
import com.nav.noteit.models.UserData
import kotlinx.coroutines.flow.Flow

interface UserPrefRepo {

    suspend fun writePrefsData(user: UserData)
    suspend fun readPrefsData(): Flow<UserData>
    suspend fun getUserId(): Flow<String>
    suspend fun clearData(): Preferences
    suspend fun setRememberMe(tick: Boolean)
    suspend fun isRememberMeSet(): Flow<Boolean>

}