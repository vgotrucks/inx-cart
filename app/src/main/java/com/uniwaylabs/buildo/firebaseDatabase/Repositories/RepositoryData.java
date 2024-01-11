package com.uniwaylabs.buildo.firebaseDatabase.Repositories;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RepositoryData {
    private static RepositoryData instance = null;
    private DatabaseReference reference;
    private FirebaseUser user;
    private String userId;

    private RepositoryData(){
        try{
            user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null) {
                userId = user.getUid();
                reference = FirebaseDatabase.getInstance().getReference("customers").child(userId);
            }
        }
        catch (Exception e){

        }
    }


    public static RepositoryData getInstance(){
        if(instance == null){
            instance = new RepositoryData();
        }
        return instance;
    }
    public DatabaseReference getReferenceOfDB(){
        if(reference == null)
            return FirebaseDatabase.getInstance().getReference();
        return reference;
    }
    public String getUserId(){
        return userId;
    }

}
