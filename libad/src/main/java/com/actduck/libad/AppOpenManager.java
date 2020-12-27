package com.actduck.libad;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import java.util.Date;
import timber.log.Timber;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

/** Prefetches App Open Ads. */
public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
  private AppOpenAd appOpenAd = null;

  private AppOpenAd.AppOpenAdLoadCallback loadCallback;

  private final Application myApplication;
  private Activity currentActivity;
  private long loadTime = 0;

  /** Constructor */
  public AppOpenManager(Application myApplication) {
    this.myApplication = myApplication;
    this.myApplication.registerActivityLifecycleCallbacks(this);
    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
  }

  /** LifecycleObserver methods */
  @OnLifecycleEvent(ON_START)
  public void onStart() {
    showAdIfAvailable();
    Timber.d("onStart");
  }

  /** Request an ad */
  public void fetchAd() {
    // Have unused ad, no need to fetch another.
    if (isAdAvailable()) {
      return;
    }

    loadCallback =
        new AppOpenAd.AppOpenAdLoadCallback() {
          /**
           * Called when an app open ad has loaded.
           *
           * @param ad the loaded app open ad.
           */
          @Override
          public void onAppOpenAdLoaded(AppOpenAd ad) {
            AppOpenManager.this.appOpenAd = ad;
            AppOpenManager.this.loadTime = (new Date()).getTime();
          }

          /**
           * Called when an app open ad has failed to load.
           *
           * @param loadAdError the error.
           */
          @Override
          public void onAppOpenAdFailedToLoad(LoadAdError loadAdError) {
            // Handle the error.
          }
        };

    int orientation = AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT;
    if (myApplication.getResources().getConfiguration().orientation
        != Configuration.ORIENTATION_PORTRAIT) {
      //横屏
      orientation = AppOpenAd.APP_OPEN_AD_ORIENTATION_LANDSCAPE;
    }

    AdRequest request = getAdRequest();
    AppOpenAd.load(
        myApplication, myApplication.getString(R.string.admob_splash), request,
        orientation, loadCallback);
  }

  private static boolean isShowingAd = false;

  /** Shows the ad if one isn't already showing. */
  public void showAdIfAvailable() {
    // Only show ad if there is not already an app open ad currently showing
    // and an ad is available.
    if (!isShowingAd && isAdAvailable()) {
      Timber.d("Will show ad.");

      FullScreenContentCallback fullScreenContentCallback =
          new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
              // Set the reference to null so isAdAvailable() returns false.
              AppOpenManager.this.appOpenAd = null;
              isShowingAd = false;
              fetchAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
            }

            @Override
            public void onAdShowedFullScreenContent() {
              isShowingAd = true;
            }
          };

      appOpenAd.show(currentActivity, fullScreenContentCallback);
    } else {
      Timber.d("Can not show ad.");
      fetchAd();
    }
  }

  /** Creates and returns ad request. */
  private AdRequest getAdRequest() {
    return new AdRequest.Builder().build();
  }

  /** Utility method to check if ad was loaded more than n hours ago. */
  private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
    long dateDifference = (new Date()).getTime() - this.loadTime;
    long numMilliSecondsPerHour = 3600000;
    return (dateDifference < (numMilliSecondsPerHour * numHours));
  }

  /** Utility method that checks if ad exists and can be shown. */
  public boolean isAdAvailable() {
    return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
  }

  /** ActivityLifecycleCallback methods */
  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
  }

  @Override
  public void onActivityStarted(Activity activity) {
    currentActivity = activity;
  }

  @Override
  public void onActivityResumed(Activity activity) {
    currentActivity = activity;
  }

  @Override
  public void onActivityStopped(Activity activity) {
  }

  @Override
  public void onActivityPaused(Activity activity) {
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
  }

  @Override
  public void onActivityDestroyed(Activity activity) {
    Timber.e("app死掉了");
    currentActivity = null;
  }
}
