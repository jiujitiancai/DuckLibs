package com.actduck.ducklibs

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.actduck.libad.AdLib

class App : MultiDexApplication() {

  override fun onCreate() {
    super.onCreate()
    INSTANCE = this

    AdLib.install(this)

  }

  companion object {

    @Volatile
    private lateinit var INSTANCE: App

    fun getInstance(): App = INSTANCE
  }
}
