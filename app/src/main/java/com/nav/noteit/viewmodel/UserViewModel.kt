package com.nav.noteit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nav.noteit.models.UserData
import com.nav.noteit.models.UserLogin
import com.nav.noteit.models.UserReq
import com.nav.noteit.models.UserResponse
import com.nav.noteit.models.UserSignupRes
import com.nav.noteit.remote.NetWorkResult
import com.nav.noteit.remote.repositories.UserPrefRepo
import com.nav.noteit.remote.repositories.UserRemoteRepo
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserViewModel(
    private val userPrefs: UserPrefRepo,
    private val userRemoteRepo: UserRemoteRepo
) : ViewModel() {


    private val _signUpRes: MutableLiveData<NetWorkResult<UserSignupRes>> = MutableLiveData()
    val signUpRes: LiveData<NetWorkResult<UserSignupRes>>
        get() = _signUpRes

    private val _userId: MutableLiveData<String> = MutableLiveData()
    val userId: LiveData<String>
        get() = _userId

    private val _userData: MutableLiveData<UserData> = MutableLiveData()
    val userData: LiveData<UserData>
        get() = _userData

    private val _rememberMe: MutableLiveData<Boolean> = MutableLiveData()
    val isRememberMeSet: LiveData<Boolean>
        get() = _rememberMe




    fun signUpUser(user: UserReq, context: Context) = viewModelScope.launch {


        userRemoteRepo.signupUser(user, context).collect { values ->
            _signUpRes.value = values

        }

//         _signUpRes.value = userRemoteRepo.signUpTest(user)

    }

     fun loginUser(userLogin: UserLogin, context: Context)= viewModelScope.launch {
         userRemoteRepo.loginUser(userLogin, context).collect{
            _signUpRes.value = it
        }
    }

     fun getUserId() = viewModelScope.launch {
        userPrefs.getUserId().collect {
            _userId.value = it
        }
    }

    fun writeUserDataToPrefs(userData: UserData) = viewModelScope.launch {

        userPrefs.writePrefsData(userData)
    }

    fun setRememberMe(tick: Boolean) = viewModelScope.launch {
        userPrefs.setRememberMe(tick)
    }

    fun isRememberMeSet() = viewModelScope.launch {
        userPrefs.isRememberMeSet().collect {
            _rememberMe.value = it
        }
    }



     fun getUserDataFromPrefs() = viewModelScope.launch{
        userPrefs.readPrefsData().collect { user ->
            _userData.value = user

        }
    }

    fun clearData() = viewModelScope.launch {
        userPrefs.clearData()
    }


}