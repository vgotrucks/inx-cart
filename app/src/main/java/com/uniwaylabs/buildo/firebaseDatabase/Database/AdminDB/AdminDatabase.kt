package com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB

import com.google.firebase.database.DatabaseReference
import com.uniwaylabs.buildo.firebaseDatabase.Database.DatabaseAdapter
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryItems

class AdminDatabase<T>: DatabaseAdapter<T>() {

    public override fun getDatabaseReference(): DatabaseReference?{
        return RepositoryItems.instance.referenceOfDB
    }
}