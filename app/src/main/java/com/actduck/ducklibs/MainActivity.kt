package com.actduck.ducklibs

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.actduck.ducklibs.R.id
import com.actduck.libad.AdBanner
import com.actduck.libad.AdInterstitial2

class MainActivity : AppCompatActivity() {

    private var mRepoBanner: AdBanner? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btShowBannerAd = findViewById<Button>(R.id.button)
        val btShowInterAd = findViewById<Button>(R.id.button2)
        AdInterstitial2.load(this)


        btShowBannerAd.setOnClickListener {
            loadBannerAd()
        }

        btShowInterAd.setOnClickListener {
            AdInterstitial2.showAd()
        }
    }

    private fun loadBannerAd() {
        if (mRepoBanner == null) {
            mRepoBanner = AdBanner()
        }
        val ad: FrameLayout = findViewById(id.adContainer)
        mRepoBanner?.loadBanner(this, ad) { null }
    }
}