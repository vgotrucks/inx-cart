package com.uniwaylabs.buildo.ui.adminForms


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SearchView
import android.widget.SearchView.OnCloseListener
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.uniwaylabs.buildo.BaseFragmentActivity
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.ToastMessages
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.StorageHandler.DatabaseStorageHandler

import com.uniwaylabs.buildo.utility.CustomToast


open class TitleListActivityAdaptable : BaseFragmentActivity(), TitleListAdapterInterface {

    var et_title: EditText? = null
    private var resultCode: Int = 0
    var adapter:TitleListAdapter? = null
    var dataPath: String = ""
    var progressBar: LottieAnimationView? = null
    var emptyState: ImageView? = null
    var statusText: TextView? = null
    var selectedTitle: String = ""
    var selectedDataModel: Any? = null
    private var searchView: SearchView? = null
    var imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        uriProfile = data?.data
        uriProfile?.let { uploadImage(it, "$dataPath/$selectedTitle",selectedTitle); }
    }

    private var uriProfile: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title_list_view)
        et_title = findViewById(R.id.et_title)
        progressBar = findViewById(R.id.progressbarhistory)
        emptyState = findViewById(R.id.image_view_sad_h)
        statusText = findViewById(R.id.text_view_result_h)
        searchView = findViewById(R.id.sv_item)
        dataPath = intent.getStringExtra(ACTIVITY_DATA) ?: ""
        resultCode = intent.getIntExtra(ACTIVITY_RESULT_CODE, 0)
        setMaterialRecycler()
        val doneButton = findViewById<AppCompatButton>(R.id.done_button)
        doneButton.setOnClickListener {
            onTapAdd()
        }

        findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        setSearchChangeListener()
    }

    private fun setSearchChangeListener(){

        searchView?.setOnQueryTextListener(object: OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                adapter?.applySearch(p0)
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                adapter?.applySearch(p0)
                return true
            }
        })

        searchView?.setOnCloseListener {
            adapter?.applySearch(null)
            true
        }
    }

    fun showLoading(show: Boolean, title: String){
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        statusText?.text = title
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        emptyState?.visibility = View.INVISIBLE
    }

    fun showEmptyState(show: Boolean){
        statusText?.text = "No items found"
        emptyState?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun setMaterialRecycler() {
        val recyclerView = findViewById<RecyclerView>(R.id.materials_recycle)
        adapter = TitleListAdapter(this, this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        fetchList()
    }


    open fun fetchList(){
        showLoading(true, "Fetching...")
        AdminDatabase<Any>().getItemsMap(this,dataPath) {
            showLoading(false, title = "")
            if(it.isNullOrEmpty()){
                showEmptyState(true)
                return@getItemsMap
            }
            showEmptyState(false)
            adapter?.updateListData(it)
        }
    }

    private fun onTapAdd() {
        val text = et_title?.text.toString()
        if (text.trim().isEmpty()){
            Toast.makeText(this,"Please give title to proceed", Toast.LENGTH_SHORT).show()
            return
        }
        selectedTitle = text
        openFileChooser()
    }

    override fun onClickAdd(title: String, item: Any?) {
        selectedDataModel = item
        selectedTitle = title
        openFileChooser()
    }

    override fun onSelectItem(title: String) {
        val indent = Intent().putExtra(ACTIVITY_DATA, title)
        setResult(resultCode,indent)
        finish()
    }

    open fun onImageUploaded(url: String){
         et_title?.text = null
    }

    fun saveDataToDB(value: Any, path: String){
        showLoading(true, "Saving...")
        AdminDatabase<String>().setDataToItemDatabase(this, path,value){
            showLoading(false, "")
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        imagePickerLauncher.launch(intent)
    }

    private fun uploadImage(
        uriProfile: Uri,
        storagePath: String,
        dataName: String,
    ) {

        showLoading(true, "Saving...")
        DatabaseStorageHandler.instance?.uploadImageToDatabase(
            this,
            uriProfile, storagePath, dataName
        ) {
            showLoading(false, "")
            onImageUploaded(it)
            CustomToast.showToast(
                this@TitleListActivityAdaptable,
                ToastMessages.uploadSuccessfully,
                Toast.LENGTH_LONG
            )
        }
    }

}