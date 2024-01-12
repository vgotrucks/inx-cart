package com.uniwaylabs.buildo.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.uniwaylabs.buildo.BaseAppCompactActivity
import com.uniwaylabs.buildo.R

class SuccessActivity : BaseAppCompactActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_successful_delivery)
        findViewById<View>(R.id.back_button_successful_delivery).setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            //Runtime.getRuntime().exit(0)
            finishAffinity()
        }
    }

}
