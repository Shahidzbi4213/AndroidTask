package com.gulehri.androidtask.ads

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.gulehri.androidtask.R
import com.gulehri.androidtask.utils.Extensions

class ExtraSmallNativeAdsHelper(private val activity: Context) {
    private var nativeAdExtraSmall: NativeAd? = null
    private var isAdLoaded: Boolean = false

    fun setNativeAdSmall(
        shimmerContainer: ShimmerFrameLayout?,
        frameLayout: FrameLayout, layoutId: Int,
        onFail: ((String?) -> Unit)? = null,
        onLoad: ((NativeAd?) -> Unit)? = null,
    ) {
        if (Extensions.isNetworkAvailable(activity)) {
            if (isAdLoaded) {
                // Ad is already loaded, populate the ad without loading it again
                populateAdView(frameLayout, layoutId)
            } else {
                shimmerContainer?.startShimmer()
                val builder =
                    AdLoader.Builder(
                        activity,
                        activity.getString(R.string.natives)
                    )
                builder.forNativeAd { unifiedNativeAd: NativeAd ->
                    if (nativeAdExtraSmall != null) {
                        nativeAdExtraSmall?.destroy()
                    }
                    nativeAdExtraSmall = unifiedNativeAd
                    isAdLoaded = true // Mark ad as loaded
                    populateAdView(frameLayout, layoutId)
                    onLoad?.invoke(nativeAdExtraSmall!!)
                }

                val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
                val adOptions =
                    NativeAdOptions.Builder().setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
                        .setVideoOptions(videoOptions).build()
                builder.withNativeAdOptions(adOptions)
                val adLoader = builder
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Log.e("Add Failed", loadAdError.message + "-ECode " + loadAdError.code)
                            super.onAdFailedToLoad(loadAdError)
                            nativeAdExtraSmall?.let {
                                Log.e("Native Error", "Error")
                                onFail?.invoke(loadAdError.message)
                            }
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            shimmerContainer?.stopShimmer()
                            shimmerContainer?.visibility = View.GONE
                            Log.e("Add Loaded", "LOda")
                        }
                    })
                    .withNativeAdOptions(adOptions)
                    .build()
                adLoader.loadAd(AdRequest.Builder().build())
            }
        } else {
            frameLayout.visibility = View.GONE
        }
    }

    private fun populateAdView(frameLayout: FrameLayout, layoutId: Int) {
        val adView =
            (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                layoutId,
                null
            ) as NativeAdView
        populateUnifiedNativeAdSmallView(nativeAdExtraSmall!!, adView)
        frameLayout.removeAllViews()
        frameLayout.addView(adView)
    }

    private fun populateUnifiedNativeAdSmallView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)

        if (nativeAd.headline != null) (adView.headlineView as TextView).text = nativeAd.headline
        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.GONE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon != null) {
            adView.iconView?.visibility = View.VISIBLE
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
        } else adView.iconView?.visibility = View.GONE

        adView.setNativeAd(nativeAd)
    }
}
