package com.uniwaylabs.buildo.firebaseDatabase.Repositories

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls

class RepositoryItems private constructor() {
    val referenceOfDB: DatabaseReference

    init {
        referenceOfDB = FirebaseDatabase.getInstance().getReference(DatabaseUrls.items_path)
    }

    companion object {
        @JvmStatic
        var instance: RepositoryItems = RepositoryItems()
    }
}