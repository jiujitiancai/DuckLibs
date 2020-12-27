package com.actduck.libad

import android.content.Context

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import timber.log.Timber

object AdReward {

  private var mGGAd: RewardedVideoAd? = null
  private var mListener: AdClosedListener? = null

  /**
   * 第一步，加载广告
   * @param context 上下文
   */
  fun load(context: Context) {
    loadGGAd(context)
  }

  private fun loadGGAd(context: Context) {
    if (mGGAd == null) {
      mGGAd = MobileAds.getRewardedVideoAdInstance(context)
          .apply {
            rewardedVideoAdListener =
              object : RewardedVideoAdListener {
                override fun onRewarded(reward: RewardItem) {
                  Timber.e("onRewarded! currency: ${reward.type} amount: ${reward.amount}")
                  // Reward the user.
                  load(context)
                  mListener?.shouldReword()
                }

                override fun onRewardedVideoAdLeftApplication() {
                  Timber.e("onRewardedVideoAdLeftApplication")
                }

                override fun onRewardedVideoAdClosed() {
                  Timber.e("onRewardedVideoAdClosed")
                  mListener?.shouldHideAd()
                  load(context)

                }

                override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
                  Timber.e("onRewardedVideoAdFailedToLoad")
                  load(context)

                }

                override fun onRewardedVideoAdLoaded() {
                  Timber.e("onRewardedVideoAdLoaded")
                  mListener?.shouldShowAd()
                }

                override fun onRewardedVideoAdOpened() {
                  Timber.e("onRewardedVideoAdOpened")
                }

                override fun onRewardedVideoStarted() {
                  Timber.e("onRewardedVideoStarted")
                }

                override fun onRewardedVideoCompleted() {
                  Timber.e("onRewardedVideoCompleted")
                }
              }

          }
    }

    if (mGGAd?.isLoaded == true) {
      Timber.e("谷歌广告已经在加载或正在加载 不用加载了")
      mListener?.shouldShowAd()
      return
    }

    mGGAd?.loadAd(
        context.getString(R.string.admob_reward), AdRequest.Builder()
        .build()
    )
    Timber.e("加载谷歌广告")

  }

  /**
   * 第二步，显示广告
   */
  fun showAd() {

    // Check if mFBAd has been loaded successfully
    if (mGGAd?.isLoaded == true) {
      mGGAd?.show()
    } else {
      mListener?.shouldReword()
    }
  }

  fun destroy() {

    mGGAd?.destroy()
    mGGAd = null
  }

  interface AdClosedListener {
    fun shouldReword()

    fun shouldShowAd()
    fun shouldHideAd()
  }

  fun setListener(listener: AdClosedListener) {
    mListener = listener
  }

}
