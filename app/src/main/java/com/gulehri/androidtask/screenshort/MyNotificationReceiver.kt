package com.gulehri.androidtask.screenshort

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gulehri.androidtask.utils.Extensions.debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MyNotificationReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Actions.CANCEL.toString() -> {
                context.startService(Intent(context, ScreenShortService::class.java).apply {
                    action = Actions.CANCEL.toString()
                })
            }

            Actions.SCREENSHOT.toString() -> {

                CoroutineScope(Dispatchers.IO).launch {
                    delay(2000)

                    withContext(Dispatchers.Default) {
                        context.startService(Intent(context, ScreenShortService::class.java).apply {
                            action = Actions.SCREENSHOT.toString()
                        })
                    }
                }


                try {
                    val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                    context.sendBroadcast(it)
                } catch (e: Exception) {
                    e.debug()
                }


            }
        }
    }


}