package com.gulehri.androidtask.screenshort

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gulehri.androidtask.R
import com.gulehri.androidtask.utils.CHANNEL_ID


object NotificationHelper {

    @SuppressLint("MissingPermission")
    fun getNotification(context: Context): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Your SS Buddy")
            .setContentText("The task is running")
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                R.drawable.baseline_power_settings_new_24,
                "Cancel",
                createActionIntent(context, Actions.CANCEL.toString())
            )
            .addAction(
                R.drawable.round_add_24,
                "Screenshot",
                createActionIntent(context, Actions.SCREENSHOT.toString())
            )
            .setAutoCancel(true)

        return builder.build()
    }

    private fun createActionIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, MyNotificationReceiver::class.java)
        intent.action = action

        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}