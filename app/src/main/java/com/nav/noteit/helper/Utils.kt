package com.nav.noteit.helper

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import java.io.FileNotFoundException
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utils {


    fun addFrag(fragmentManager: FragmentManager, containerId:Int=0, fragment: Fragment, anim_enter: Int, anim_exit: Int,  pop_enter: Int, pop_exit: Int){
        val fragTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragTransaction.replace(containerId,fragment)
        fragTransaction.setCustomAnimations(anim_enter, anim_exit, pop_enter, pop_exit)
        fragTransaction.addToBackStack(fragment.tag)
        fragTransaction.commit()
    }

    fun replaceFrag(fragmentManager: FragmentManager, containerId:Int=0, fragment: Fragment,anim_enter: Int, anim_exit: Int,  pop_enter: Int, pop_exit: Int){
        val fragTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragTransaction.setCustomAnimations(anim_enter, anim_exit, pop_enter, pop_exit)
        fragTransaction.replace(containerId,fragment)
        fragTransaction.commit()
    }

    fun isValidEmailAddress(hex: String): Boolean {
        val pattern: Pattern
        val emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(emailPattern)
        val matcher: Matcher = pattern.matcher(hex)
        return matcher.matches()
    }


    fun uriToBitmap(uri: Uri?, context: Context): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri!!))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun isInternetConnectedCheck(mContext: Context?): Boolean {
        var outcome = false

        try {
            if (mContext != null) {
                val cm = mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val networkInfos = cm.allNetworkInfo

                for (tempNetworkInfo in networkInfos) {

                    if (tempNetworkInfo.isConnected) {
                        outcome = true
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return outcome
    }

    fun hideSoftKeyboard(activity: Activity) {


        try {
            val view = activity.currentFocus
            if (view != null) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (e: Exception) {
        }

    }

}