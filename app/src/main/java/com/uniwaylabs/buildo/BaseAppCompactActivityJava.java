package com.uniwaylabs.buildo;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;

public class BaseAppCompactActivityJava extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Configuration configuration = new Configuration(newBase.getResources().getConfiguration());
        configuration.fontScale = 0.9f;
        applyOverrideConfiguration(configuration);
    }
}
