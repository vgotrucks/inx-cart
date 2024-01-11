package com.uniwaylabs.buildo.utility;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.uniwaylabs.buildo.R;


public class CustomToast {

    public static void showToast(Activity context, String message,int duration){
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customToast = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) context.findViewById(R.id.custom_toast_linear_layout));
        TextView tvMessage = (TextView) customToast.findViewById(R.id.custom_message_text);
        tvMessage.setText(message);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.TOP |Gravity.CENTER,0,50);
        toast.setDuration(duration);
        toast.setView(customToast);
        toast.show();
    }
}
