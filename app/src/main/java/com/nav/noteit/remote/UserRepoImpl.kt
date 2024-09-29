package com.nav.noteit.remote

import android.content.Context
import com.nav.noteit.models.UserLogin
import com.nav.noteit.models.UserReq
import com.nav.noteit.models.UserSignupRes
import com.nav.noteit.remote.repositories.UserRemoteRepo
import kotlinx.coroutines.flow.Flow

class UserRepoImpl( private val api: ApiInterface) :
    UserRemoteRepo {


    override suspend fun loginUser(userLogin: UserLogin, context: Context): Flow<NetWorkResult<UserSignupRes>> {
        return toResultFlow(context){
            api.loginUser(userLogin)
        }
    }

    override suspend fun signupUser(user: UserReq, context: Context): Flow<NetWorkResult<UserSignupRes>> {

            return toResultFlow(context){
                api.signUpUser(user)

            }

    }

    override suspend fun signUpTest(user: UserReq): UserSignupRes? {
        val res = api.signUpUserTest(user)

        return res.body()
    }


}