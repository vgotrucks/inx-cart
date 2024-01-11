package com.uniwaylabs.buildo.NotificationService;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class AppNotificationChannels extends Application {

    public static final String CHANNEL_ID1 = "Channel ID 1";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificatiionChannels();
    }

    private void createNotificatiionChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel1 = new NotificationChannel(CHANNEL_ID1, "Order alerts",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel1);
        }
    }

}
