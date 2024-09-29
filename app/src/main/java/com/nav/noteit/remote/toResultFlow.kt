package com.nav.noteit.remote


import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.nav.noteit.helper.Constants.API_INTERNET_MESSAGE
import com.nav.noteit.helper.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import retrofit2.Response

inline fun <reified T> toResultFlow(
    context: Context,
    crossinline call: suspend () -> Response<T>
): Flow<NetWorkResult<T>> {
    return flow {
        val isInternetConnected = Utils.hasInternetConnection(context)
        if (isInternetConnected) {
            emit(NetWorkResult.Loading(true))
            try {
                val c = call()


                if (c.isSuccessful && c.body() != null) {

                    Log.e("code", c.code().toString())
                    emit(NetWorkResult.Success(c.body()))


                } else {
                    if (c.body() != null) {
                        emit(NetWorkResult.Error(c.message()))
                    }

                    when (c.code()) {

                        (404) -> {
                            emit(NetWorkResult.Error(c.message()))
                        }

                        else -> {
                            emit(NetWorkResult.Error(c.message()))
                        }

                    }


                }
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> {
                        emit(NetWorkResult.Error(e.message()))
                    }
                }
            } catch (e: Exception) {
                emit(NetWorkResult.Error(e.toString()))
            }
        } else {
            emit(NetWorkResult.Error(API_INTERNET_MESSAGE))
        }
    }.flowOn(Dispatchers.IO)
}