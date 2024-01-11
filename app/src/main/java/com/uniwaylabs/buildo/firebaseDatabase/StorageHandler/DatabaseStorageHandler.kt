package com.uniwaylabs.buildo.firebaseDatabase.StorageHandler

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.uniwaylabs.buildo.ToastMessages
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseHandler
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.DrawerMenuDataModels.UserAccountModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryData

class DatabaseStorageHandler {


    fun uploadImageToDatabase(
        context: Context?,
        imageUri: Uri?,
        storagePath: String?,
        fileName: String,
        mInterface: (data: String) -> Unit
    ) {
        val userId = RepositoryData.getInstance().userId
        if (imageUri != null && context != null) {
            val imgName = fileName + "_" + userId + "." + getFileExtension(context, imageUri)
            val mStorageRef =
                 FirebaseStorage.getInstance().reference.child(DatabaseUrls.items_path).child(storagePath ?: "Unknown").child(imgName)
            mStorageRef.putFile(imageUri)
                .addOnSuccessListener {
                    mStorageRef.downloadUrl.addOnSuccessListener { p0 ->
                        mInterface(p0.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(context, ToastMessages.noDataChoosen, Toast.LENGTH_LONG).show()
        }
    }

    private fun getFileExtension(context: Context?, uri: Uri): String? {
        return if (context != null) {
            val cR = context.contentResolver
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(cR.getType(uri))
        } else {
            "jpg"
        }
    }

    companion object {
        private val databaseStorageHandler: DatabaseStorageHandler? = DatabaseStorageHandler()
        val instance: DatabaseStorageHandler?
            get() = databaseStorageHandler ?: DatabaseStorageHandler()
    }
}