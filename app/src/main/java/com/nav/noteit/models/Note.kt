package com.nav.noteit.models

import com.google.gson.annotations.SerializedName

data class Note(
    @SerializedName("id")
    var id: Int,
    @SerializedName("title")
    var title: Int,
    @SerializedName("timeStamp")
    var timeStamp: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("description")
    var description: String
)