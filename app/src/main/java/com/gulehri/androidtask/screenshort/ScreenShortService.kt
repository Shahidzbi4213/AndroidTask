package com.gulehri.androidtask.screenshort

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

/*
 * Created by Shah Saud on 12/24/2023.
 */

class ScreenShortService : Service() {

    companion object {
        var code: Int = 0
        var data: Intent? = null
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            Actions.START.toString() -> {

                code = intent.getIntExtra("code", 0)
                data = intent.getParcelableExtra("data")

                startForeground(79, NotificationHelper.getNotification(this))
            }

            Actions.SCREENSHOT.toString() -> {
                data?.let { ScreenshotHelper(context = this).captureAndSaveScreenshot(code, it) }
            }

            Actions.CANCEL.toString() -> stopSelf()
        }

        return START_NOT_STICKY
    }
}