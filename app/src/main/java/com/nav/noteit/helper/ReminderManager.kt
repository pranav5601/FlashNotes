package com.nav.noteit.helper

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.nav.noteit.R

class ReminderManager: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.e("alarm_triggered", "alarm_triggered")
        intent?.let {
            val title = it.getStringExtra("reminder_title")
            val description = it.getStringExtra("reminder_desc")
            val notificationId = it.getIntExtra("note_id", 0)
            Log.e("alarm_triggered", "$title, $description, $notificationId")
            setNotificationBuilder(context,title!!, description!!,notificationId)
        }

    }

    private fun setNotificationBuilder(
        context: Context?,
        title: String,
        description: String,
        notificationId: Int
    ) {
        context?.let { ctx ->
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val builder = NotificationCompat.Builder(ctx, "reminder_notification_id")
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(title)

                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notificationManager.notify(notificationId, builder.build())
        }
    }

}