package com.nav.noteit.helper

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.nav.noteit.R
import com.nav.noteit.activities.ActMain

class ReminderManager: BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {



            // Set the alarm here.
            Log.e("alarm_triggered", "alarm_triggered")
            intent.let {
                val title = it?.getStringExtra("reminder_title")
                val description = it?.getStringExtra("reminder_desc")
                val notificationId = it?.getIntExtra("note_id", 0)
                val reminderId = it?.getIntExtra("reminder_id", 0)
                val reminderRepetition = it?.getIntExtra("reminder_repetition", 0)
                Log.e("alarm_triggered", "$title, $description, $notificationId")
                setNotificationBuilder(context,title!!, description!!,notificationId!!)
            }

    }

    private fun setNotificationBuilder(
        context: Context?,
        title: String,
        description: String,
        notificationId: Int
    ) {

        val noteIntent = Intent(context, ActMain::class.java)
        noteIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        noteIntent.putExtra("redirectNote", notificationId.toString())

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack.
            addNextIntentWithParentStack(noteIntent)
            // Get the PendingIntent containing the entire back stack.
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }


        context?.let { ctx ->
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(ctx, "reminder_notification_id")
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(title)
                .setContentText(description)
                .setShowWhen(true)
                .setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notificationManager
                .notify(notificationId, builder.build())
        }
    }

}