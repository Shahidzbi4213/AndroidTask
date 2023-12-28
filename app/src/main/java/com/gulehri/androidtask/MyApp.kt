package com.gulehri.androidtask

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp
import com.gulehri.androidtask.utils.Extensions.createNotificationChannel

/*
 * Created by Shah Saud on 12/24/2023.
 */

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()


        init()


    }

    private fun init() {
        FirebaseApp.initializeApp(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this, getSystemService(NotificationManager::class.java))
        }
    }
}