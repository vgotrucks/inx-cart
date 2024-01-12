package com.uniwaylabs.buildo.ui.welcomeUI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.uniwaylabs.buildo.BaseAppCompactActivityJava;
import com.uniwaylabs.buildo.R;
import com.uniwaylabs.buildo.ToastMessages;
import com.uniwaylabs.buildo.ui.MainActivity;
import com.uniwaylabs.buildo.userPermissions.UserPermissionHandler;
import com.uniwaylabs.buildo.userPermissions.UserPermissionHandlerInterface;

import java.lang.reflect.Field;

public class GetStartedActivity extends BaseAppCompactActivityJava {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Button getStartedBtn;
    private GetStartedViewPagerAdapter getStartedViewPagerAdapter;

    private int[] images = {R.drawable.poster3,R.drawable.poster3};
    private final int locationRequestCode = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstarted);
        viewPager = (ViewPager) findViewById(R.id.pager_view_getStarted);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_getstarted);
        getStartedBtn = (Button) findViewById(R.id.getstarted_btn);
        getStartedViewPagerAdapter = new GetStartedViewPagerAdapter(GetStartedActivity.this,images);
        viewPager.setAdapter(getStartedViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager,true);

        try {
            Field customScroller = ViewPager.class.getDeclaredField("mScroller");
            customScroller.setAccessible(true);
            customScroller.set(viewPager,new GetStartedViewPagerScroller(this));

        } catch (NoSuchFieldException | IllegalAccessException e) {

        }

        setViewPagerListener();
        setGetStartedBtnListener();
    }

    private void setViewPagerListener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0)
                    getStartedBtn.setText(R.string.next);
                else if (position == (images.length-1))
                    getStartedBtn.setText(R.string.get_started);

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setGetStartedBtnListener(){
        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getStartedBtn.getText() == getString(R.string.next))
                    tabLayout.selectTab(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()+1),true);
                else {
                    getUserPermissions();
                }
            }
        });
    }

    private void getUserPermissions(){
        UserPermissionHandler.permissionHandler.checkPermissionForLocation(GetStartedActivity.this, locationRequestCode, new UserPermissionHandlerInterface() {
            @Override
            public void permissionGranted() {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == locationRequestCode && grantResults.length > 0){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, ToastMessages.locationPermissionNotGranted,Toast.LENGTH_LONG).show();
            }
            moveToNextActivity();
        }
    }

    private void moveToNextActivity(){
        Intent intent= new Intent(GetStartedActivity.this , MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
