package com.actduck.libad

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest

import timber.log.Timber

object AdInterstitial2 {

  var mAlwaysShow: Boolean = true //是否一直显示
  private var mGGAd: com.google.android.gms.ads.InterstitialAd? = null

  fun load(context: Context) {
    if (!AdLib.shouldShowAd()) {
      Timber.e("无需加载广告")
      return
    }

    if (mAlwaysShow) loadGGAd(context)
  }

  private fun loadGGAd(context: Context) {
    if (mGGAd == null) {
      mGGAd = com.google.android.gms.ads.InterstitialAd(context)
          .apply {
            adUnitId = context.getString(R.string.admob_interstitial2)

            adListener = object : AdListener() {
              override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Timber.e("谷歌 ad onAdLoaded!")
              }

              override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                Timber.e("谷歌 ad onAdFailedToLoad!")

                load(context)
              }

              override fun onAdOpened() {
                // Code to be executed when the ad is displayed.
                Timber.e("谷歌 ad onAdOpened!")
              }

              override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
              }

              override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
              }

              override fun onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Timber.e("谷歌 ad onAdClosed")

                load(context)
              }
            }
          }

    }

    if (mGGAd?.isLoaded == true || mGGAd?.isLoading == true) {
      Timber.e("谷歌广告已经在加载或正在加载 不用加载了")
      return
    }

    mGGAd?.loadAd(
        AdRequest.Builder()
            .build()
    )
    Timber.e("加载谷歌广告")
  }

  fun showAd() {

    // Check if mFBAd has been loaded successfully
    if (mGGAd?.isLoaded == true) {
      mGGAd?.show()
    }
  }

}
