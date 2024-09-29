package com.nav.noteit.repositories

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.view.ContentInfoCompat.Flags
import com.nav.noteit.dao.AlarmRepo
import com.nav.noteit.helper.ReminderManager
import com.nav.noteit.models.AlarmItem
import java.util.Calendar

class AlarmRepoImpl(val context: Context) : AlarmRepo {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    override fun schedule(alarmItem: AlarmItem) {
        val intent = Intent(context, ReminderManager::class.java).apply {
            putExtra("reminder_title", alarmItem.title)
            putExtra("reminder_desc", alarmItem.description)
            putExtra("note_id", alarmItem.noteId)
            putExtra("reminder_id", alarmItem.reminderId)
            putExtra("reminder_repetition", alarmItem.alarmRepetition)

            flags = Intent.FLAG_RECEIVER_FOREGROUND
        }

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (alarmItem.alarmRepetition == 0L) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmItem.alarmTime, alarmPendingIntent)

        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmItem.alarmTime,
                alarmItem.alarmRepetition,
                alarmPendingIntent
            )
        }

        val receiver = ComponentName(context, ReminderManager::class.java)

        context.packageManager?.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        val date = Calendar.getInstance()
        date.timeInMillis = alarmItem.alarmTime



        Log.e("repetition duration", alarmItem.alarmRepetition.toString())
        Log.e(
            "Alarm",
            "Alarm set at ${date.get(Calendar.HOUR_OF_DAY)} : ${date.get(Calendar.MINUTE)}"
        )
    }

    override fun cancel(alarmItem: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmItem.hashCode(),
                Intent(context, ReminderManager::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}