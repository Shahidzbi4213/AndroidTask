package com.gulehri.androidtask.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gulehri.androidtask.ads.InterstitialHelper
import com.gulehri.androidtask.databinding.ActivityMainBinding
import com.gulehri.androidtask.screenshort.Actions
import com.gulehri.androidtask.screenshort.ScreenShortService
import com.gulehri.androidtask.utils.Extensions.isMyServiceRunning
import com.gulehri.androidtask.utils.PermissionUtils

class MainActivity : AppCompatActivity() {

    companion object {
        var isActivityPause = false
    }

    private lateinit var binding: ActivityMainBinding

    private val mediaProjectionManager: MediaProjectionManager by lazy {
        application.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Request permission to capture the screen
        if (!this.isMyServiceRunning(ScreenShortService::class.java)) {
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(),
            77
        )
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            PermissionUtils.checkAndRequestPermissions(this) {
                if (data != null) {
                        startScreenShortService(resultCode, data)
                }
            }
        }
    }

    private fun startScreenShortService(code: Int, data: Intent?) {
        if (!this.isMyServiceRunning(ScreenShortService::class.java)) {
            startService(Intent(this, ScreenShortService::class.java).apply {
                action = Actions.START.toString()
                putExtra("code", code)
                putExtra("data", data)
            })
        }
    }


    override fun onResume() {
        super.onResume()
        isActivityPause = false
    }

    override fun onPause() {
        super.onPause()
        isActivityPause = true
    }

    override fun onDestroy() {
        super.onDestroy()

        InterstitialHelper.destroyAll(this)
    }
}