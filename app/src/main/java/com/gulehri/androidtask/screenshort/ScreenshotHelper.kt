package com.gulehri.androidtask.screenshort

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import com.gulehri.androidtask.R
import com.gulehri.androidtask.utils.Extensions
import com.gulehri.androidtask.utils.Extensions.debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID


class ScreenshotHelper(private val context: Context) {


    fun captureAndSaveScreenshot(
        code: Int, intent: Intent
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            withContext(Dispatchers.Main) {

                val mediaProjectionManager =
                    context.applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                val windowManager =
                    context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val mediaProjection = mediaProjectionManager.getMediaProjection(code, intent)


                val display = windowManager.defaultDisplay
                val displayMetrics = DisplayMetrics()
                display.getMetrics(displayMetrics)


                // Create an ImageReader to capture the screen content
                val imageReader = ImageReader.newInstance(
                    displayMetrics.widthPixels,
                    displayMetrics.heightPixels,
                    android.graphics.PixelFormat.RGBA_8888,
                    2
                )


                // Create a virtual display
                val virtualDisplay = mediaProjection?.createVirtualDisplay(
                    "Screenshot",
                    displayMetrics.widthPixels,
                    displayMetrics.heightPixels,
                    displayMetrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    imageReader.surface,
                    null,
                    null
                )
                imageReader.setOnImageAvailableListener({
                    try {
                        val image = it.acquireLatestImage()
                        val planes = image.planes
                        val buffer = planes[0].buffer.rewind()
                        val pixelStride = planes[0].pixelStride
                        val rowStride = planes[0].rowStride
                        val rowPadding: Int = rowStride - pixelStride * displayMetrics.widthPixels

                        // create bitmap
                        val bitmap = Bitmap.createBitmap(
                            displayMetrics.widthPixels + rowPadding / pixelStride,
                            displayMetrics.heightPixels,
                            Bitmap.Config.ARGB_8888
                        )
                        bitmap.copyPixelsFromBuffer(buffer)

                        saveScreenshot(bitmap)


                        mediaProjection?.stop()
                        virtualDisplay?.release()
                        it.close()

                    } catch (e: Exception) {
                        e.localizedMessage.debug()
                    }

                }, null)
            }
        }

    }


    private fun saveScreenshot(bitmap: Bitmap) {
        val myPath: File?
        val directory = Extensions.appDirectory()
        myPath = File(directory, "${UUID.randomUUID()}.png")

        kotlin.runCatching {
            FileOutputStream(myPath).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            bitmap.recycle()
        }.onFailure {
            try {
                bitmap.recycle()
            } catch (e: IOException) {
                e.message.toString().debug()
            }
        }.onSuccess {
            MediaScannerConnection.scanFile(context, arrayOf(myPath.absolutePath), null) { _, _ -> }
            playSaveSound()
            Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playSaveSound() {
        try {
            val mediaPlayer = MediaPlayer.create(context, R.raw.camera)

            mediaPlayer.setOnCompletionListener {
                it.release()
            }

            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
