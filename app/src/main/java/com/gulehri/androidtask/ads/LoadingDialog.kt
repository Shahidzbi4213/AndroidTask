package com.gulehri.androidtask.ads

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.gulehri.androidtask.R

object LoadingDialog {

    var dialog: Dialog? = null

    @JvmStatic
    fun showLoadingDialog(context: Activity, @StringRes title: Int? = R.string.loadingAds) {
        if (!context.isFinishing && !context.isDestroyed) {

            if (dialog == null) {
                try {
                    dialog = Dialog(context)
                    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog?.window?.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.loading_dialog_bg
                        )
                    )
                    dialog?.setCancelable(false)
                    dialog?.setContentView(R.layout.dialog_loading)
                    val textView = dialog?.findViewById<TextView>(R.id.progress_text)
                    if (title != null) {
                        textView?.text = context.getString(title)
                    }
                    dialog?.show()
                } catch (_: Exception) {
                } catch (_: java.lang.Exception) {
                } catch (_: IllegalArgumentException) {
                }
            } else dialog?.show()
        }
    }

    @JvmStatic
    fun hideLoadingDialog(activity: Activity) {
        try {
            if (!activity.isFinishing && !activity.isDestroyed && dialog != null) {
                dialog?.dismiss()
            }
        } catch (_: Exception) {
        } catch (_: java.lang.Exception){}
    }
}







