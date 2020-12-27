package com.actduck.libad

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import timber.log.Timber
import java.util.Arrays

object AdLib {
  var context: Application? = null

  var appOpenManager: AppOpenManager? = null

  private var sp: SharedPreferences? = null

  fun install(app: Application) {
    context = app
    sp = PreferenceManager.getDefaultSharedPreferences(context)

    MobileAds.initialize(
        context,
        OnInitializationCompleteListener {
          Timber.e("MobileAds注册完成")
        })
    
    val testDeviceIds = Arrays.asList("9BD71A0F392981CB2C4834B7E60A982E")
    val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
    MobileAds.setRequestConfiguration(configuration)
    appOpenManager = AppOpenManager(context)

  }

  fun setForChildDirected() {
    Timber.e("是小孩子")

    val requestConfiguration = MobileAds.getRequestConfiguration()
        .toBuilder()
        .setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
        .build()
    MobileAds.setRequestConfiguration(requestConfiguration)

  }

  fun shouldShowAd(): Boolean {

    var playTimes = 0
    var showAd = false

    context?.let {
      playTimes = it.resources.getInteger(R.integer.play_times)
      showAd = it.resources.getBoolean(R.bool.show_ads)
    }

    sp?.let {
      Timber.e("玩耍次数：${it.getInt(PLAY_TIMES, 0)}")
      Timber.e("买了去广告吗：${it.getBoolean(PAID_AD, false)}")

      return (it.getInt(PLAY_TIMES, 0) > playTimes
          && !it.getBoolean(PAID_AD, false)
          && showAd)
    }

    return false
  }

  fun addOnePlayTime() {
    sp?.let {
      var times = it.getInt(PLAY_TIMES, 0)
      it.edit()
          .putInt(PLAY_TIMES, ++times)
          .apply()
    }

  }

  fun payingAd() {
    sp?.edit()
        ?.putBoolean(PAID_AD, true)
        ?.apply()

  }

}