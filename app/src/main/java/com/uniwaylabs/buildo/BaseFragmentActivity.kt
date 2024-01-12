package com.uniwaylabs.buildo

import android.content.Context
import android.content.res.Configuration
import androidx.fragment.app.FragmentActivity

open class BaseFragmentActivity: FragmentActivity() {


    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val config = Configuration(newBase?.resources?.configuration)
        config.fontScale = 1.0f
        applyOverrideConfiguration(config)
    }
}