package com.uniwaylabs.buildo.NotificationService.FirebaseMessaging;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.uniwaylabs.buildo.ToastMessages;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SendPushNotification {

    public static SendPushNotification shared = new SendPushNotification();

    private final String URL = "https://fcm.googleapis.com/fcm/send";
    private final String SERVER_KEY = "Key=AAAAW053sHA:APA91bHbxORVBDL464ypZyvT9iXoxr3GZITtpCsbsBkt-Hl4YisPMsJOe-QpCCR_ffTRlD8Fnz4-7icFnWojZf0NUS0Ppl4_QU54hto10nLd_7Be85Y5vthEFYywqL3aFwM8tT18JeI0";
    private RequestQueue mRequestQueue;
    private Context context;

    public void sendMessage(Context context, String to, String title, String body){
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
        configurePostMethod(to,title,body);
    }

     private void configurePostMethod(String to,String title,String body){
         JSONObject mainBody = new JSONObject();
         try{
             mainBody.put("to",to);
             JSONObject notificationBody = new JSONObject();
             notificationBody.put("title",title);
             notificationBody.put("body",body);
             mainBody.put("notification",notificationBody);
             JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, mainBody, new Response.Listener<JSONObject>() {
                 @Override
                 public void onResponse(JSONObject response) {

                 }
             }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {

                 }
             }){
                 @Override
                 public Map<String, String> getHeaders() throws AuthFailureError {
                     Map<String,String> header = new HashMap<>();
                     header.put("content-type","application/json");
                     header.put("authorization",SERVER_KEY);
                     return  header;
                 }
             };
             mRequestQueue.add(request);
         }catch (Exception e){
             Toast.makeText(context, ToastMessages.somethingWentWrong,Toast.LENGTH_SHORT).show();
         }


     }


}


