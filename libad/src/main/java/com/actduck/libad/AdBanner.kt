package com.actduck.libad

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import timber.log.Timber

class AdBanner {

  private var mGGBanner: AdView? = null

  private var mParent: ViewGroup? = null
  private var mContext: Context? = null

  fun loadBanner(
    context: Context,
    parent: ViewGroup,
    onClick: () -> Unit
  ) {

    if (!AdLib.shouldShowAd()) {
      Timber.e("无需加载广告")
      return
    }

    mContext = context
    mParent = parent
    loadGGAd()
    val view = LayoutInflater.from(context)
        .inflate(R.layout.ad_place_ad, null, false)

    val rl = view.findViewById<RelativeLayout>(R.id.next_ads_view)
    rl.setOnClickListener {
      onClick.invoke()
    }

    bindAd(view)
  }

  private fun loadGGAd() {

    if (mGGBanner == null) {
      mGGBanner = AdView(mContext).apply {
        adSize = AdSize.BANNER
        adUnitId = mContext?.getString(R.string.admob_banner)
        adListener = object : com.google.android.gms.ads.AdListener() {
          override fun onAdLoaded() {
            bindAd(this@apply)
            Timber.e("GGBanner加载成功")
          }
        }
      }

    }

    val adRequest = AdRequest.Builder()
        .build()


    Timber.e("是测试设备吗${adRequest.isTestDevice(AdLib.context)}")
    mGGBanner?.loadAd(adRequest)
  }

  private fun bindAd(view: View) {
    mParent?.removeAllViews()
    if (view.parent != null) {
      (view.parent as ViewGroup).removeView(view)
    }
    mParent?.addView(view)
  }

  fun destroy() {

  }
}
