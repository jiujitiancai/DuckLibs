package com.actduck.libad

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import timber.log.Timber

object AdNative {

  private var mListener: AdLoadListener? = null

  /**
   * 第一步，加载广告
   * @param context 上下文
   */
  fun load(context: Context) {
    if (!AdLib.shouldShowAd()) {
      Timber.e("无需加载广告")
      return
    }
    loadGgNativeAD(context)
    Timber.e("加载原生广告")
  }

  internal fun loadGgNativeAD(context: Context) {

    val adLoader = AdLoader.Builder(context, context.getString(R.string.admob_native))
        .forUnifiedNativeAd(UnifiedNativeAd.OnUnifiedNativeAdLoadedListener { unifiedNativeAd ->
          // OnUnifiedNativeAdLoadedListener implementation.

          mListener?.onAdLoad(unifiedNativeAd)

        })
        .withAdListener(object : AdListener() {
          override fun onAdFailedToLoad(errorCode: Int) {
            // Handle the failure by logging, altering the UI, and so on.
            Timber.e("GG Native 加载错误,重新加载")
          }

        })
        .withNativeAdOptions(
            NativeAdOptions.Builder()
                // Methods in the NativeAdOptions.Builder class can be
                // used here to specify individual options settings.
                .build()
        )
        .build()

    adLoader.loadAd(
        AdRequest.Builder()
            .build()
    )
  }

  interface AdLoadListener {
    fun onAdLoad(ad: UnifiedNativeAd)
  }

  fun setListener(listener: AdLoadListener) {
    mListener = listener
  }
}