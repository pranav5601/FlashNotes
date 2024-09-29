package com.nav.noteit.remote

import androidx.lifecycle.LiveData

import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import com.nav.noteit.models.Note
import com.nav.noteit.models.Reminder
import com.nav.noteit.models.UserLogin
import com.nav.noteit.models.UserReq
import com.nav.noteit.models.UserResponse
import com.nav.noteit.models.UserSignupRes
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {

    @GET("notes/get_notes")
    suspend fun getNotes(
//        @Query("userID") userId: String
    ): List<Note>

    @GET("reminders/get_reminders")
    suspend fun getReminders(
        @Query ("noteId") noteId: Int
    ):Reminder

    @POST("reminders/create_reminder")
    suspend fun createReminder(
        @Body reminder: Reminder
    ): String

    @POST("notes/create_note")
    suspend fun createNote(
        @Body note: Note
    ): String

    @POST("users/login")
    suspend fun loginUser(
        @Body userLogin: UserLogin
    ): Response<UserSignupRes>

    @POST("users/signup")
    suspend fun signUpUser(
        @Body user: UserReq
    ): Response<UserSignupRes>

    @POST("users/signup")
    suspend fun signUpUserTest(
        @Body user: UserReq
    ): Response<UserSignupRes>







    companion object {
        const val BASE_URL = "https://noteit-backend.netlify.app/.netlify/functions/api/"
    }
}