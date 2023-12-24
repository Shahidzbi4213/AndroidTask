package com.gulehri.androidtask.utils

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.gulehri.androidtask.R
import java.io.File

object Extensions {

    fun Any?.debug() {
        Log.d("find", "$this")
    }

    fun infoLog(tag: String, message: String) = Log.i(tag, message)


    fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        return result
    }


    @RequiresApi(26)
    fun createNotificationChannel(context: Context, notificationManager: NotificationManager) {

        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.apply {
            description = context.getString(R.string.notification)
            enableVibration(true)
        }

        notificationManager.createNotificationChannel(notificationChannel)

    }

    fun Context.isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


    fun View.hide() {
        visibility = View.GONE
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun appDirectory(): String {
        val myDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + "Android Task"
        val myDir = File(myDirectory)
        if (!myDir.exists()) {
            try {
                myDir.mkdirs()
            } catch (e: Exception) {
                myDir.mkdir()
            }
        }
        return myDirectory
    }


}