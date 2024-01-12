package com.uniwaylabs.buildo

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity

open class BaseAppCompactActivity: AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val config = Configuration(newBase?.resources?.configuration)
        config.fontScale = 0.9f
        applyOverrideConfiguration(config)
    }
}