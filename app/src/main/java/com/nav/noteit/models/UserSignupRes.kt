package com.nav.noteit.models

import com.google.gson.annotations.SerializedName





data class UserSignupRes (


    @SerializedName("code")val code: Int?,
    @SerializedName("status")val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("userData") val userData: UserData?

)


data class UserData(
    @SerializedName("full_name")val fullName: String?,
    @SerializedName("email")val emailId: String?,
    @SerializedName("password")val password: String?,
    @SerializedName("country")val country: String?,
    @SerializedName("userId")val userId: String?,
    @SerializedName("api_token")val apiToken: String?,
)

