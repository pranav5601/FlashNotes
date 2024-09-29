package com.nav.noteit.remote.local_data

import androidx.datastore.preferences.core.Preferences
import com.nav.noteit.models.UserData
import com.nav.noteit.remote.repositories.UserPrefRepo
import kotlinx.coroutines.flow.Flow

class UserPrefRepoImpl(private val userPrefs: UserPreferences): UserPrefRepo {

    override suspend fun writePrefsData(user: UserData) {
        userPrefs.saveToDataStore(user)
    }

     override suspend fun readPrefsData(): Flow<UserData> {
        return userPrefs.getFromDataStore()
    }

    override suspend fun getUserId(): Flow<String> {
        return userPrefs.getUserId()
    }

    override suspend fun clearData(): Preferences {
        return userPrefs.clearDataStore()
    }

    override suspend fun setRememberMe(tick: Boolean) {
        return userPrefs.setRememberMe(tick)
    }

    override suspend fun isRememberMeSet(): Flow<Boolean> {
        return userPrefs.isRememberMeSet()
    }

}