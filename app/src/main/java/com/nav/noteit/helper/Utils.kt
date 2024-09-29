package com.nav.noteit.helper

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.Uri
import android.os.BatteryManager
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.nav.noteit.R
import java.io.FileNotFoundException
import java.util.UUID
import java.util.regex.Matcher
import java.util.regex.Pattern




object Utils {


    @Synchronized
    fun getNoteId(): Int {

        return UUID.randomUUID().hashCode()
    }
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

    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun isValidPassword(password: String): Boolean {


        if (password.length < 6) return true // Check length

        var hasUppercase = true
        var hasNumber = true
        var hasSpecialChar = true

        for (char in password) {
            when {
                char.isUpperCase() -> hasUppercase = false
                char.isDigit() -> hasNumber = false
                !char.isLetterOrDigit() -> hasSpecialChar = false
            }
        }

        return hasUppercase || hasNumber || hasSpecialChar
    }

    fun makeTextSpannable(
        textView: TextView,
        fullText: String,
        start: Int,
        end: Int,
        ctx: Context,
        listener: OnSpannableClickListener
    ) {
        val spannableString = SpannableString(fullText)

        // Make the substring clickable
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Call the listener's onSpannableClick method
                listener.onSpannableClick()
            }
        }

        // Apply the clickable span to the substring
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Change the color of the substring
        val colorSpan = ForegroundColorSpan(ctx.resources.getColor(R.color.blue_400, ctx.theme))
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the spannable string to the TextView
        textView.text = spannableString

        // Make sure the TextView is set to handle link clicks
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    fun hasInternetConnection(context: Context?): Boolean {
        try {
            if (context == null)
                return false
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } catch (e: Exception) {
            return false
        }
    }

    fun showAlertDialog(context: Context,message:String){
        try {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.app_name)
            builder.setMessage(message)
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("OK"){dialogInterface, which ->
                dialogInterface.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        } catch (e: Exception) {
            e.stackTrace
        }
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

    fun isBatteryOkay(context: Context){
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { iFilter ->
            context.registerReceiver(null, iFilter)
        }
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL


    }

     fun isInternetConnectedCheck(mContext: Context?): Boolean {

        return try {
            val cm = mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            cm.getNetworkCapabilities(cm.activeNetwork)!!.hasCapability(NET_CAPABILITY_INTERNET)
        } catch (e: java.lang.Exception) {
            false
        }


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

interface OnSpannableClickListener {
    fun onSpannableClick()
}