package com.uniwaylabs.buildo;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.uniwaylabs.buildo.BDDialogs.VTDialogViews;
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseHandler;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseHandlerInterface;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls;
import com.uniwaylabs.buildo.utility.DatabaseDeserializer;


public class VTAppUpdate {


    public static final int APP_UPDATE_REQUEST_CODE = 11;
    private static final String RESTART_MSG = "We have updated your app. Please click on restart to install updates.";
    private static final String OKAY_MSG = "You need to update app. Please update it from playstore";
    public static final String RESTART_BTN_TEXT = "RESTART";
    public static final String OKAY_BTN_TEXT = "OKAY";
    private AppUpdateManager appUpdateManager;

    private Activity activity;
    public static VTAppUpdate getInstance(){
        return new VTAppUpdate();
    }


    public void checkForUpdateAvailability(Activity activity){
        this.activity = activity;
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(activity.getApplicationContext());
        this.appUpdateManager = appUpdateManager;
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                handleUpdateInfo(appUpdateManager,result);
            }
        });
    }

    private void handleUpdateInfo(AppUpdateManager appUpdateManager,AppUpdateInfo result){
        switch (result.updateAvailability()){
            case UpdateAvailability
                    .UPDATE_AVAILABLE:{
                    startUpdatingApp(appUpdateManager,result);
            }
            break;
            case UpdateAvailability.UPDATE_NOT_AVAILABLE:
            case UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS:
            case UpdateAvailability.UNKNOWN: {

            }
            break;
        }
    }

    private void startUpdatingApp(AppUpdateManager appUpdateManager,AppUpdateInfo result){
        //Update type : IMIDIATE,FLEXIBLE

        DatabaseHandler instance = DatabaseHandler.getInstance();
        instance.getAdminDataFromDatabase(activity, DatabaseUrls.update_priority_path, new DatabaseHandlerInterface() {
            @Override
            public void handleData(DataSnapshot data) {
                long priority = DatabaseDeserializer.shared.convertDataToLong(data);
                if(priority == 1 && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, activity, APP_UPDATE_REQUEST_CODE);
                    }
                    catch (Exception e){

                    }

                }
                if (priority == 0 && result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.FLEXIBLE, activity, APP_UPDATE_REQUEST_CODE);
                    }
                    catch (Exception e){

                    }
                }
            }
        });
    }

    public void handleUpdateListener(){
        if(appUpdateManager != null && activity != null) {
            InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {

                @Override
                public void onStateUpdate(@NonNull InstallState state) {
                    switch (state.installStatus()) {
                        case InstallStatus.CANCELED:
                        case InstallStatus.FAILED: {
                            VTDialogViews.shared.showDialogForUpdate(activity, OKAY_MSG, OKAY_BTN_TEXT, appUpdateManager);
                            appUpdateManager.unregisterListener(this);
                        }
                        break;
                        case InstallStatus.DOWNLOADING: {

                        }
                        break;
                        case InstallStatus.DOWNLOADED: {
                            appUpdateManager.completeUpdate();
                            appUpdateManager.unregisterListener(this);
                        }
                        break;
                    }
                }
            };
            appUpdateManager.registerListener(listener);
        }
    }

}
