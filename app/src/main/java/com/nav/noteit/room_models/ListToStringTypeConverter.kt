package com.nav.noteit.room_models

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections

class ListToStringTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun stringToList(data: String?): List<String> {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<String>>() {

        }.type

        return gson.fromJson<List<String>>(data, listType)
    }

    @TypeConverter
    fun listToString(data: List<String>): String{
        return gson.toJson(data)
    }
}