package com.nav.noteit.remote

sealed class NetWorkResult<T>(val status: ApiStatus, val data: T?=null, val message: String?=null) {

    data class Success<T>(val _data: T?) : NetWorkResult<T>(status = ApiStatus.SUCCESS, data = _data, message = null)
    data class Error<T>(val exception: String) : NetWorkResult<T>(status = ApiStatus.ERROR, message = exception)
    data class Loading<T>(val isLoading: Boolean) : NetWorkResult<T>(status = ApiStatus.LOADING)

}

enum class ApiStatus {
    SUCCESS,
    ERROR,
    LOADING
}