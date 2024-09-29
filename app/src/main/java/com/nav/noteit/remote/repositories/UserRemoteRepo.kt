package com.nav.noteit.remote.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.nav.noteit.models.UserLogin
import com.nav.noteit.models.UserReq
import com.nav.noteit.models.UserResponse
import com.nav.noteit.models.UserSignupRes
import com.nav.noteit.remote.NetWorkResult
import kotlinx.coroutines.flow.Flow

interface UserRemoteRepo {

    suspend fun loginUser(userLogin: UserLogin, context: Context): Flow<NetWorkResult<UserSignupRes>>

    suspend fun signupUser(user: UserReq, context: Context): Flow<NetWorkResult<UserSignupRes>>

    suspend fun signUpTest(user: UserReq): UserSignupRes?

}