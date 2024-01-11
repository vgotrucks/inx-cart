package com.uniwaylabs.buildo.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialTypeListModel

class HomeViewModel : ViewModel() {

    private val liveListData: MutableLiveData<Array<MaterialTypeListModel>> = MutableLiveData()

    init {
        val lisData = arrayOf( MaterialTypeListModel("IR"),
            MaterialTypeListModel("CM"),
            MaterialTypeListModel("SE"),
            MaterialTypeListModel("YE"),
            MaterialTypeListModel("AB"),
            MaterialTypeListModel("TW")
        )

        liveListData.value = lisData
    }

    val listData: LiveData<Array<MaterialTypeListModel>>
        get() = liveListData
}