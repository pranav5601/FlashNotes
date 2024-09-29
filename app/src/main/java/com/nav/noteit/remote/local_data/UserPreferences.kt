package com.nav.noteit.remote.local_data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nav.noteit.models.UserData
import com.nav.noteit.models.UserSignupRes
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")

class UserPreferences(
    val context: Context
) {


    companion object {
        val EMAIL = stringPreferencesKey("EMAIL")
        val USER_ID = stringPreferencesKey("USER_ID")
        val FULL_NAME = stringPreferencesKey("FULL_NAME")
        val PASSWORD = stringPreferencesKey("PASSWORD")
        val API_TOKEN = stringPreferencesKey("API_TOKEN")
        val COUNTRY = stringPreferencesKey("COUNTRY")
        val REMEMBER_ME_TICK = booleanPreferencesKey("REMEMBER_ME_TICK")
    }

    suspend fun saveToDataStore(user: UserData) {
        context.dataStore.edit {

            it[EMAIL] = user.emailId!!
            it[USER_ID] = user.userId!!
            it[FULL_NAME] = user.fullName!!
            it[PASSWORD] = user.password!!
            it[API_TOKEN] = user.apiToken!!
            it[COUNTRY] = user.country!!

        }


    }

    suspend fun setRememberMe(tick: Boolean) {
        context.dataStore.edit {
            it[REMEMBER_ME_TICK] = tick
        }
    }

    fun isRememberMeSet() = context.dataStore.data.map {
        it[REMEMBER_ME_TICK] ?: false
    }

    fun getFromDataStore() = context.dataStore.data.map {

        UserData(
            emailId = it[EMAIL] ?: "",
            password = it[PASSWORD] ?: "",
            fullName = it[FULL_NAME] ?: "",
            userId = it[USER_ID] ?: "",
            apiToken = it[API_TOKEN] ?: "",
            country = it[COUNTRY] ?: "",

            )

    }


    fun getUserId() = context.dataStore.data.map {
        it[USER_ID] ?: ""
    }


    fun apiToken() = context.dataStore.data.map {
        it[API_TOKEN] ?: ""
    }

    suspend fun clearDataStore() = context.dataStore.edit {
        it.clear()
    }


}