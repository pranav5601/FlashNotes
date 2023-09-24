package com.nav.noteit.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.lang.Exception

public class ImageBitmapString {

    @TypeConverter
    fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        val temp = Base64.encodeToString(b, Base64.DEFAULT)
        return temp
    }

    @TypeConverter
    fun stringToBitMap(str: String): Bitmap? {
        return try {
            val encodeByte = Base64.decode(str, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            bitmap
        } catch (e: Exception) {
            e.message
            null
        }
    }
}