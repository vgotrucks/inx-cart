package com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB

import com.google.firebase.database.DatabaseReference
import com.uniwaylabs.buildo.firebaseDatabase.Database.DatabaseAdapter
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryData
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryItems

class UserDatabase<T>: DatabaseAdapter<T>() {

    public override fun getDatabaseReference(): DatabaseReference?{
        return RepositoryData.getInstance().referenceOfDB
    }
}