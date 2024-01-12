package com.uniwaylabs.buildo.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.uniwaylabs.buildo.BaseAppCompactActivityJava;
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences;
import com.uniwaylabs.buildo.R;
import com.uniwaylabs.buildo.ToastMessages;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseHandler;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.UserAccountModel;
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls;
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryData;
import com.uniwaylabs.buildo.ui.MainActivity;
import com.uniwaylabs.buildo.ui.SplashActivity;


public class SignInActivity extends BaseAppCompactActivityJava {
    AppCompatButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInAccount googleSignInAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // request
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        signInButton = findViewById(R.id.SignInButton);
        //ConfigureGoogleButton();
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        findViewById(R.id.SignInWithPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, GenerateOTPActivity.class);
                startActivity(intent);
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try{
            GoogleSignInAccount acc = task.getResult(ApiException.class);
          //  gotoPhoneAuth(acc);
            saveAccountDataToDatabase();
        }
        catch(ApiException e){
            Toast.makeText(SignInActivity.this,"Error SignIn",Toast.LENGTH_LONG).show();
        }
    }


    private void gotoPhoneAuth(GoogleSignInAccount acc) {
        if(acc != null) {
            String imgUrl = "";
            if(acc.getPhotoUrl() != null)
                imgUrl = acc.getPhotoUrl().toString();
            UserAccountModel userAccountModel = new UserAccountModel(acc.getDisplayName(), "",imgUrl, acc.getEmail(),acc.getGivenName(),acc.getFamilyName(),null,0, 0);
            BDSharedPreferences.shared.saveAccountData(SignInActivity.this,userAccountModel);
            Intent intent = new Intent(SignInActivity.this, GenerateOTPActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(SignInActivity.this,"Opps ! Something went wrong",Toast.LENGTH_LONG).show();
        }
    }


    private void moveToNextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void saveAccountDataToDatabase() {
        try {
            googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        } catch (Exception e) {
            Toast.makeText(this, ToastMessages.signInError, Toast.LENGTH_LONG)
                .show();
        }
        if (googleSignInAccount != null) {
            String imgUrl = "";
            if (googleSignInAccount.getPhotoUrl() != null)
                imgUrl =
                    googleSignInAccount.getPhotoUrl().toString();

            UserAccountModel userAccountModel = new UserAccountModel(
                    googleSignInAccount.getDisplayName(),
                    googleSignInAccount.getEmail(),
                    imgUrl,
                    googleSignInAccount.getEmail(),
                    googleSignInAccount.getGivenName(),
                    googleSignInAccount.getFamilyName(),
                    null,
                    0.0,
                    0.0
            );
            BDSharedPreferences.shared.saveAccountData(SignInActivity.this,userAccountModel);
            DatabaseHandler.getInstance().saveDataToDatabase(this, DatabaseUrls.account_path, userAccountModel);
            DatabaseHandler.getInstance()
                    .saveDataToDatabase(this, DatabaseUrls.token_path, "");
            registerToCustomerSearch(
                    googleSignInAccount.getId(),
                    RepositoryData.getInstance().getUserId()
            );
            moveToNextActivity();
        } else {
            Toast.makeText(this, ToastMessages.signInError, Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void registerToCustomerSearch(String mail, String uid) {

        if (mail != null) {
            FirebaseDatabase.getInstance().getReference().child(DatabaseUrls.customer_search_path)
                    .child(mail).setValue(uid);
        }
    }


}
