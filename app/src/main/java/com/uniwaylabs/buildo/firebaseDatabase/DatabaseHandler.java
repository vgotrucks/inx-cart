package com.uniwaylabs.buildo.firebaseDatabase;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences;
import com.uniwaylabs.buildo.ToastMessages;
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase;
import com.uniwaylabs.buildo.firebaseDatabase.Database.DBService.DatabaseErrorHandlerInterface;
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryData;
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryItems;


public class  DatabaseHandler {

    private static DatabaseHandler shared = new DatabaseHandler();

    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    public static DatabaseHandler getInstance(){
        if (shared == null)
            shared = new DatabaseHandler();
        return shared;
    }


    /** fetching from Database*/

    public void getUserDataFromDatabase(Context context,String path,DatabaseHandlerInterface mInterface){
        RepositoryData.getInstance().getReferenceOfDB().child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mInterface.handleData(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context.getApplicationContext(), ToastMessages.dataLoadError,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getUserDataFromDatabaseOnce(Context context,String path,DatabaseHandlerInterface mInterface){
        RepositoryData.getInstance().getReferenceOfDB().child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mInterface.handleData(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context.getApplicationContext(), ToastMessages.dataLoadError,Toast.LENGTH_SHORT).show();
            }
        });
    }


//    public void getDriverDataFromDatabase(Context context,String driverId,String path,DatabaseHandlerInterface mInterface){
//        RepositoryDriver.getInstance(driverId).getReferenceOfDB().child(path).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    mInterface.handleData(snapshot);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(context.getApplicationContext(),"Couldn't load data,Please check your internet connectivity",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public void getAdminDataFromDatabase(Context context,String path,DatabaseHandlerInterface mInterface){
        RepositoryItems.getInstance().getReferenceOfDB().child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mInterface.handleData(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context.getApplicationContext(),"Couldn't load data,Please check your internet connectivity",Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void saveDataToDatabase(Context context,String path,Object value){
        RepositoryData.getInstance().getReferenceOfDB().child(path).setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error != null){
                    Toast.makeText(context.getApplicationContext(),ToastMessages.dataSaveError,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


//    public void saveDriverDataToDatabase(Context context,String driverId,String path,Object value){
//        RepositoryDriver.getInstance(driverId).getReferenceOfDB().child(path).setValue(value, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                if(error != null){
//                    Toast.makeText(context.getApplicationContext(),ToastMessages.dataSaveError,Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

//    public void savePostDataToDatabase(Context context,String path,Object value){
//        RepositoryPosts.getInstance().getReferenceOfDB().child(path).setValue(value, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                if(error != null){
//                    Toast.makeText(context.getApplicationContext(),ToastMessages.dataSaveError,Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

//    public void savePostDataToAdminDatabase(Context context,String path,Object value){
//        path = DatabaseUrls.admin_posts_path + "/"+path;
//        RepositoryAdmin.getInstance().getReferenceOfDB().child(path).push().setValue(value, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                if(error != null){
//                    Toast.makeText(context.getApplicationContext(),ToastMessages.dataSaveError,Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    /** Deleting from database*/

//    public void DeleteFromPostDatabase(Context context,String path){
//        RepositoryPosts.getInstance().getReferenceOfDB().child(path).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists())
//                    RepositoryPosts.getInstance().getReferenceOfDB().child(path).removeValue();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//


    ///ITEMS DATA ADMIN



    public void getItemsDataInSelectedPostalArea(Activity context, String path, DatabaseHandlerInterface mInterface){
        path = BDSharedPreferences.shared.getSelectedPostalAreaCode(context) + "/" + path;
        getItemsDataFromDatabase(context,path, mInterface);
    }

    public void saveItemsDataInSelectedPostalArea(Activity context, String path, Object value, DatabaseErrorHandlerInterface mInterface){
        path = BDSharedPreferences.shared.getSelectedPostalAreaCode(context) + "/" + path;
        pushDataToItemDatabase(context, path, value, mInterface);
    }

    /**getting from database*/

    public void getItemsDataFromDatabase(Context context,String path,DatabaseHandlerInterface mInterface){
        RepositoryItems.getInstance().getReferenceOfDB().child(path).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mInterface.handleData(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context.getApplicationContext(),"Couldn't load data,Please check your internet connectivity",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**saving to database*/

    public void pushDataToItemDatabase(Context context,String path,Object value,DatabaseErrorHandlerInterface mInterface){
        RepositoryItems.getInstance().getReferenceOfDB().child(path).push().setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if(error != null){
                    Toast.makeText(context.getApplicationContext(),ToastMessages.dataSaveError,Toast.LENGTH_SHORT).show();
                }
                else {
                    mInterface.savedDataReference(ref);
                }
            }
        });
    }




}
