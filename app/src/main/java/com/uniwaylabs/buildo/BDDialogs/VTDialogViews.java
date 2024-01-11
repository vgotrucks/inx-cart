package com.uniwaylabs.buildo.BDDialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationRequest;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.uniwaylabs.buildo.R;
import com.uniwaylabs.buildo.VTAppUpdate;
import com.uniwaylabs.buildo.userPermissions.UserPermissionHandler;
import com.uniwaylabs.buildo.userPermissions.UserPermissionHandlerInterface;

public class VTDialogViews {
    public static VTDialogViews shared = new VTDialogViews();

    private final int LOCATION_REQUEST_CODE = 1;
    public Dialog dialog;

//    public void showDialogForDatePicker(Activity activity,VTDialogViewDatePickerInterface mInterface) {
//        Dialog dialog = new Dialog(activity);
//        dialog.setContentView(R.layout.dialog_date_picker);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().getAttributes().windowAnimations =
//                android.R.style.Animation_Dialog;
//
//        ImageView saveBtn = dialog.findViewById(R.id.img_save_date);
//        DatePicker datePicker = dialog.findViewById(R.id.date_picker_account);
//        saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
//                mInterface.getDate(calendar.getTime());
//                dialog.cancel();
//            }
//        });
//
//        dialog.show();
//    }

    public void showDialogForLocationPermissionNotGranted(Activity activity){
        if(this.dialog != null)
            dialog.cancel();
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_location_permission);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations =
                android.R.style.Animation_Dialog;
        dialog.findViewById(R.id.ask_me_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPermissionHandler.permissionHandler.checkPermissionForLocation(activity, LOCATION_REQUEST_CODE, new UserPermissionHandlerInterface() {
                    @Override
                    public void permissionGranted() {
                        dialog.cancel();
                    }
                });
            }
        });
        this.dialog = dialog;
        dialog.show();
    }

    public void showDialogForLocationNotEnabled(Activity activity){
        if(this.dialog != null)
            dialog.cancel();
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_location_not_enabled);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations =
                android.R.style.Animation_Dialog;
        dialog.findViewById(R.id.ask_me_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateEnableGPSDialog(activity);
            }
        });
        this.dialog = dialog;
        dialog.show();
    }

    private void generateEnableGPSDialog(Activity activity){

        com.google.android.gms.location.LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });
        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(activity,51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void showDialogForUpdate(Activity activity, String messageText, String buttonText, AppUpdateManager appUpdateManager){
        if(this.dialog != null)
            dialog.cancel();
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_app_update);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations =
                android.R.style.Animation_Dialog;
        TextView message = dialog.findViewById(R.id.text_view_message_upd_dialog);
        Button button = dialog.findViewById(R.id.ask_me_btn);
        message.setText(messageText);
        button.setText(buttonText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonText.equals(VTAppUpdate.RESTART_BTN_TEXT)){
                    appUpdateManager.completeUpdate();
                }
                if(buttonText.equals(VTAppUpdate.OKAY_BTN_TEXT)){
                    activity.startActivity(activity.getPackageManager().getLaunchIntentForPackage("com.uniwaylabs.buildo"));
                }
            }
        });
        this.dialog = dialog;
        dialog.show();
    }
}
