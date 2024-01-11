package com.uniwaylabs.buildo.NotificationService.FirebaseMessaging;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences;
import com.uniwaylabs.buildo.NotificationService.NotificationSender;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseHandler;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls;

public class FirebaseMessageHandler extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
            DatabaseHandler.getInstance().saveDataToDatabase(this, DatabaseUrls.token_path,s);
        saveData(BDSharedPreferences.shared.getPUSH_TOKEN(),s);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null){
            NotificationSender.shared.sendNotification(remoteMessage.getNotification().getTitle()
                    ,remoteMessage.getNotification().getBody(),this);
        }
    }

    public  void saveData(String suitName,String data){
        SharedPreferences sharedPreferences= getSharedPreferences(suitName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Code",data);
        editor.apply();
    }

}
