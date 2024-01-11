package com.uniwaylabs.buildo.utility;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.NotificationsModel;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.UserAccountModel;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel;


import java.util.Map;

public class DatabaseDeserializer {

    public static DatabaseDeserializer shared = new DatabaseDeserializer();

    public long convertDataToLong(DataSnapshot data){
        long mData;
        try {
            mData = (long)data.getValue();
        }
        catch (Exception e){
            mData = 0;
        }
        return mData;
    }

    public String convertDataToString(DataSnapshot data){
        String mData;
        try {
            mData = (String) data.getValue();
        }
        catch (Exception e){
            mData = null;
        }
        return mData;
    }


    public UserAccountModel convertDataToUserInfo(DataSnapshot data){
        UserAccountModel mData;
        try {
            mData = (UserAccountModel) data.getValue(UserAccountModel.class);
        }
        catch (Exception e){
            mData = null;
        }
        return mData;
    }


    public Map<String, NotificationsModel> convertDataToNotifications(DataSnapshot data){
        Map<String, NotificationsModel> mData;
        GenericTypeIndicator<Map<String, NotificationsModel>> arrayData = new GenericTypeIndicator<Map<String, NotificationsModel>>() {
        };
        try {
            mData = data.getValue(arrayData);
        }
        catch (Exception e){
            mData = null;
        }
        return mData;
    }

    public Map<String, MaterialListItemModel> convertDataToHome(DataSnapshot data){
        Map<String, MaterialListItemModel> mData;
        GenericTypeIndicator<Map<String, MaterialListItemModel>> arrayData = new GenericTypeIndicator<Map<String, MaterialListItemModel>>() {
        };
        try {
            mData = data.getValue(arrayData);
        }
        catch (Exception e){
            mData = null;
        }
        return mData;
    }



}
