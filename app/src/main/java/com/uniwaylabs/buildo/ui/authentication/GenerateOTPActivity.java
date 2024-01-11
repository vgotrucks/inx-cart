package com.uniwaylabs.buildo.ui.authentication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.uniwaylabs.buildo.R;


public class GenerateOTPActivity extends AppCompatActivity{

    AppCompatButton sendButton;
    EditText phoneNumber;
    ImageView imageViewPoster;
    LinearLayout phoneEditLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        sendButton = (AppCompatButton) findViewById(R.id.phoneNextbutton);
        phoneNumber = (EditText) findViewById(R.id.phone);
        imageViewPoster = (ImageView) findViewById(R.id.poster2_imageview);
        phoneEditLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_phone);


        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(phoneNumber.getText().toString().trim().length() == 10) {
                    InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(phoneNumber.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        phoneNumber.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if(isKeyboardShown(phoneNumber.getRootView())){
                    hidePoster();
                }
                else {
                    showPoster();
                }

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phoneNumber.getText().toString().trim();
                if(number.contains(" ") || number.length()!=10 || number.contains(".") || number.contains("#") || number.contains("*")){
                    phoneNumber.setError("Invalid number format");
                    phoneNumber.requestFocus();
                    return;
                }
                number = "+91"+number;
                Intent intent= new Intent(GenerateOTPActivity.this , OTPValidationActivity.class);
                intent.putExtra("PhoneNumber",number);
                startActivity(intent);
            }
        });

    }

    private boolean isKeyboardShown(View rootView){
        final int softKeyboardHeight = 100;
        Rect rec = new Rect();
        rootView.getWindowVisibleDisplayFrame(rec);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - rec.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    void hidePoster(){
        TranslateAnimation imageAnim = new TranslateAnimation(0,imageViewPoster.getWidth(),0,0);
        imageAnim.setDuration(600);
        imageAnim.setFillAfter(true);
        imageViewPoster.startAnimation(imageAnim);
        imageViewPoster.setVisibility(View.GONE);
       // phoneEditLinearLayout.offsetTopAndBottom(height - 100);
        Animation phoneEditTextAnim = AnimationUtils.loadAnimation(this,R.anim.top_anim_phone_verification);
        phoneEditLinearLayout.setAnimation(phoneEditTextAnim);
    }
    void showPoster (){
        if(imageViewPoster.getVisibility() != View.VISIBLE) {
            TranslateAnimation imageAnim = new TranslateAnimation(imageViewPoster.getWidth(), 0, 0, 0);
            imageAnim.setDuration(600);
            imageAnim.setFillAfter(true);
            imageViewPoster.startAnimation(imageAnim);
            imageViewPoster.setVisibility(View.VISIBLE);


            Animation phoneEditTextAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim_phone_verification);
            phoneEditLinearLayout.setAnimation(phoneEditTextAnim);
        }
    }

    private void setImageHeight(){
        Point outPoint = new Point();
        getWindowManager().getDefaultDisplay().getSize(outPoint);
        final float density = getResources().getDisplayMetrics().density;
        final int width = (int) (outPoint.x*density);
        final int height = (int) ((outPoint.y/3)*density);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
        params.topMargin = (int)(150*density);
        imageViewPoster.setLayoutParams(params);
        imageViewPoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }




}
