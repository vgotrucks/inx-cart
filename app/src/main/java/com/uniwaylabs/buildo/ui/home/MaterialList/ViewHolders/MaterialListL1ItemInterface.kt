package com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders

import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel

interface MaterialListL1ItemInterface {
    fun onClickL1Item(position: Int, item: CategoryDataModel?)
}