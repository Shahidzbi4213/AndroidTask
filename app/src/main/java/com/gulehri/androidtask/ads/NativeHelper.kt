package com.gulehri.androidtask.ads

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.gulehri.androidtask.R
import com.gulehri.androidtask.utils.Extensions
import com.gulehri.androidtask.utils.Extensions.hide
import com.gulehri.androidtask.utils.Extensions.show

private const val TAG = "native_ad_log"


class NativeHelper(private val context: Context) {

    private lateinit var runnable: Runnable

    fun loadAdsWithConfiguration(
        nativeContainer: ConstraintLayout,
        adMobContainer: FrameLayout,
        height: Int,
        adMobId: String,
        adClosePressed: (Boolean) -> Unit
    ) {

        loadAdMob(
            nativeContainer,
            adMobContainer,
            height,
            adMobId,
            adClosePressed
        )
    }


    private fun showNative(
        nativeContainer: ConstraintLayout,
        adMobContainer: FrameLayout,
        adMobId: String,
        adClosePressed: (Boolean) -> Unit
    ) {

        if (!isNativeLoading) {

            isNativeLoading = true
            if (adMobNativeAd == null) {
                Extensions.infoLog(TAG, "adMobId: $adMobId")
                val builder: AdLoader.Builder =
                    AdLoader.Builder(context.applicationContext, adMobId)
                val adLoader = builder.forNativeAd { unifiedNativeAd: NativeAd? ->
                    adMobNativeAd = unifiedNativeAd
                }.withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(i: LoadAdError) {
                        super.onAdFailedToLoad(i)
                        isNativeLoading = false
                        Extensions.infoLog(
                            TAG,
                            "Failed; code: " + i.code + ", message: " + i.message
                        )
                        Extensions.infoLog(TAG, "$nativeContainer")
                        nativeContainer.hide()
                        adClosePressed(false)
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        isNativeLoading = false
                        Extensions.infoLog(TAG, "Loaded")
                        nativeContainer.show()
                        adMobContainer.show()
                        adClosePressed(true)

                    }
                }).withNativeAdOptions(
                    com.google.android.gms.ads.nativead.NativeAdOptions.Builder()
                        .setAdChoicesPlacement(com.google.android.gms.ads.formats.NativeAdOptions.ADCHOICES_TOP_RIGHT)
                        .build()
                ).build()
                adLoader.loadAd(AdRequest.Builder().build())
            } else {
                nativeContainer.show()
                adMobContainer.show()
                adClosePressed(true)
            }
        } else {
            val handler = Handler(Looper.getMainLooper())

            runnable = Runnable {

                if (isNativeLoading) {
                    runnable.let { handler.postDelayed(it, 10) }
                } else {
                    adClosePressed(true)
                }
            }

            handler.postDelayed(runnable, 1)
        }

    }

    private fun loadAdMob(
        nativeContainer: ConstraintLayout,
        adMobContainer: FrameLayout,
        height: Int,
        adMobId: String,
        adClosePressed: (Boolean) -> Unit
    ) {
        if (Extensions.isNetworkAvailable(context)) {
            if (height == -1) {
                //Check the Height
                val vto = nativeContainer.viewTreeObserver
                vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val heightPx = nativeContainer.measuredHeight
                        val heightDp = (heightPx / context.resources.displayMetrics.density).toInt()
                        nativeContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        if (heightDp < 49) {
                            nativeContainer.hide()
                        } else {
                            showNative(nativeContainer, adMobContainer, adMobId, adClosePressed)
                        }
                    }
                })
            } else {
                if (height < 49) {
                    nativeContainer.hide()
                } else {
                    showNative(nativeContainer, adMobContainer, adMobId, adClosePressed)
                }
            }
        } else nativeContainer.hide()
    }

    @SuppressLint("InflateParams")
    fun populateUnifiedNativeAdView(
        nativeAd: NativeAd?,
        nativeContainer: ConstraintLayout,
        adMobNativeContainer: FrameLayout,
        height: Int,
        adClosePressed: (Boolean) -> Unit
    ) {
        if (nativeAd != null) {

            Extensions.infoLog(TAG, "Screen Visible")
            nativeContainer.findViewById<TextView>(R.id.loadingadtext)?.hide()
            val inflater = LayoutInflater.from(context)

            val adView: NativeAdView = when (height) {
                in 60..110 -> {
                    //7a
                    inflater.inflate(R.layout.native_7a_design, null) as NativeAdView
                }

                in 111..120 -> {
                    //7b
                    inflater.inflate(R.layout.dialog_loading, null) as NativeAdView
                }

                in 401..500 -> {
                    //2a
                    inflater.inflate(R.layout.admob_native_2_a, null) as NativeAdView
                }

                in 501..600 -> {
                    //2b
                    inflater.inflate(R.layout.admob_native_2_b, null) as NativeAdView
                }

                else -> {
                    //7a
                    inflater.inflate(R.layout.native_7a_design, null) as NativeAdView
                }
            }


            adClosePressed(true)

            adMobNativeContainer.show()
            adMobNativeContainer.removeAllViews()
            adMobNativeContainer.addView(adView)

            if (adView.findViewById<com.google.android.gms.ads.formats.MediaView>(R.id.ad_media) != null) {
                val mediaView: MediaView = adView.findViewById(R.id.ad_media)
                //  val bgView: ImageView = adView.findViewById(R.id.ad_media_bg)
                adView.mediaView = mediaView

                /*bgView.load(nativeAd.mediaContent?.mainImage) {
                    transformations(BlurTransformation(context, 25f, 10f))
                }*/
            }

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)

            //Headline
            if (adView.headlineView != null) {
                if (nativeAd.headline == null) {
                    (adView.headlineView as TextView).visibility = View.INVISIBLE
                } else {
                    (adView.headlineView as TextView).text = nativeAd.headline
                    (adView.headlineView as TextView).isSelected = true
                }
            }


            //Body
            if (adView.bodyView != null) {
                if (nativeAd.body == null) {
                    adView.bodyView?.visibility = View.INVISIBLE
                } else {
                    adView.bodyView?.show()
                    (adView.bodyView as TextView).text = nativeAd.body
                }
            }

            //Call to Action
            if (adView.callToActionView != null) {
                if (nativeAd.callToAction == null) {
                    adView.callToActionView?.visibility = View.INVISIBLE
                    adView.findViewById<ImageFilterView>(R.id.call_to_action_bg).visibility =
                        View.INVISIBLE
                } else {
                    adView.callToActionView?.show()
                    (adView.callToActionView as TextView).text = nativeAd.callToAction
                }

            }

            //Icon
            if (adView.iconView != null) {
                if (nativeAd.icon == null)
                    adView.iconView?.hide()
                else {
                    (adView.iconView as ImageView).setImageDrawable(nativeAd.icon?.drawable)
                    adView.iconView?.show()
                }
            }

            //price
            if (adView.priceView != null) {
                if (nativeAd.price == null)
                    adView.priceView?.hide()
                else {
                    adView.priceView?.hide()
                    (adView.priceView as TextView).text = nativeAd.price
                }
            }

            //Store
            if (adView.storeView != null) {
                if (nativeAd.store == null)
                    adView.storeView?.hide()
                else {
                    adView.storeView?.hide()
                    (adView.storeView as TextView).text = nativeAd.store
                }
            }

            //Rating
            if (adView.starRatingView != null) {
                if (nativeAd.starRating != null)
                    (adView.starRatingView as RatingBar).rating =
                        nativeAd.starRating.toString().toFloat()
                else
                    (adView.starRatingView as RatingBar).visibility = View.INVISIBLE

                adView.starRatingView?.hide()
            }

            //Advertiser
            if (adView.advertiserView != null) {
                if (nativeAd.advertiser != null)
                    (adView.advertiserView as TextView).text = nativeAd.advertiser
                else
                    (adView.advertiserView as TextView).visibility = View.INVISIBLE

                adView.advertiserView?.hide()
            }

            setRoundAndColor(
                adView,
                0.3f,
                "#0000ff"
            )
            adView.setNativeAd(nativeAd)
        } else
            nativeContainer.hide()

        isNativeLoading = false
        adMobNativeAd = null

    }

    companion object {
        var adMobNativeAd: NativeAd? = null
        var isNativeLoading = false
    }

    private fun setRoundAndColor(adView: NativeAdView, round: Float, color: String) {
        val callToActionBg = adView.findViewById<ImageFilterView>(R.id.call_to_action_bg)
        callToActionBg.roundPercent = round
        if (color.isNotBlank()) {
            try {
                callToActionBg.setBackgroundColor(Color.parseColor(color))
            } catch (e: Exception) {
                callToActionBg.setBackgroundColor(Color.parseColor("#FA5711"))
            } catch (e: java.lang.Exception) {
                callToActionBg.setBackgroundColor(Color.parseColor("#FA5711"))
            }
        }
    }
}