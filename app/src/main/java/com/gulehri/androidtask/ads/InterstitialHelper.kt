package com.gulehri.androidtask.ads

import android.app.Activity
import android.util.Log
import androidx.annotation.StringRes
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.gulehri.androidtask.R
import com.gulehri.androidtask.ui.MainActivity
import com.gulehri.androidtask.utils.Extensions
import com.gulehri.androidtask.utils.INTER_AD_CAPPING
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


object InterstitialHelper {
    private var TAG = "inter_ad_log"

    private var mInterstitialAd: InterstitialAd? = null
    private var isAdShowing = false
    private var isAdLoading = false
    private var lastInterstitialAdDismissTime = 0L
    private var adCappingBetweenInterstitialAndAppOpenAd = 0L

    private var job: CoroutineScope = CoroutineScope(Dispatchers.Main)


    fun loadInterAd(
        activity: Activity,
        @StringRes adId: Int = R.string.interstitial
    ) {

        if (Extensions.isNetworkAvailable(activity)) {

            try {
                isAdLoading = true
                Log.d(TAG, "loadInterAd: is Called ${activity.getString(adId)}")
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(
                    activity, activity.getString(adId), adRequest,
                    object : InterstitialAdLoadCallback() {

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            super.onAdFailedToLoad(error)
                            mInterstitialAd = null
                            isAdLoading = false
                            Log.d(TAG, "Failed to load")
                        }

                        override fun onAdLoaded(interAd: InterstitialAd) {
                            super.onAdLoaded(interAd)
                            mInterstitialAd = interAd
                            isAdLoading = false

                            Log.d(TAG, "Ad is  loaded ${activity.getString(adId)}")

                        }
                    })

            } catch (e: IOException) {
                mInterstitialAd = null
                isAdLoading = false
            }
        }


    }

    fun showAndLoadInterAd(
        activity: Activity,
        canLoadNext: Boolean = false,
        onDismiss: () -> Unit
    ) {


        if (!isAdLoading /*&& isTimeOfNextInterAd()*/ && mInterstitialAd != null
            && Extensions.isNetworkAvailable(activity) && !MainActivity.isActivityPause
        ) {


            job.launch {
                LoadingDialog.showLoadingDialog(activity)
                withContext(Dispatchers.IO) {
                    delay(1500.milliseconds)
                }
                LoadingDialog.hideLoadingDialog(activity)

                if (!MainActivity.isActivityPause)
                    mInterstitialAd?.show(activity)

            }.invokeOnCompletion {
                if (it == null) {
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {


                            override fun onAdDismissedFullScreenContent() {

                                Log.d(TAG, "Ad is Dismissed")

                                mInterstitialAd = null
                                isAdShowing = false
                                lastInterstitialAdDismissTime = System.currentTimeMillis()

                                fetchAnother(canLoadNext, activity)

                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)

                                isAdShowing = false
                                mInterstitialAd = null
                                isAdLoading = false
                                onDismiss()

                                fetchAnother(canLoadNext, activity)
                            }

                            override fun onAdImpression() {

                                isAdShowing = true
                                isAdLoading = false
                                onDismiss()
                            }

                            override fun onAdShowedFullScreenContent() {
                                isAdShowing = true
                                isAdLoading = false
                            }
                        }
                } else {

                    onDismiss()
                }
            }

        } else {
            onDismiss()
        }


    }

    private fun fetchAnother(next: Boolean, activity: Activity) {
        if (next) {
            job.launch {

                withContext(Dispatchers.IO) {
                    delay(5.seconds)
                }
                loadInterAd(activity, R.string.interstitial)
            }
        }
    }

    fun getCurrentInterAd(): InterstitialAd? = mInterstitialAd

    fun isInterAdShowing(): Boolean = isAdShowing

    fun isInterAdLoading(): Boolean = isAdLoading


    private fun isTimeOfNextInterAd(): Boolean =
        timeDifference(lastInterstitialAdDismissTime) >= (INTER_AD_CAPPING - 10)

    private fun timeDifference(adLoadedTime: Long): Int {
        val current = System.currentTimeMillis()
        val elapsedTime = current - adLoadedTime

        return TimeUnit.MILLISECONDS.toSeconds(elapsedTime).toInt()
    }


    fun destroyAll(activity: Activity) {
        LoadingDialog.hideLoadingDialog(activity = activity)
        isAdShowing = false
        mInterstitialAd = null
        lastInterstitialAdDismissTime = 0L
        job.cancel()
    }
}