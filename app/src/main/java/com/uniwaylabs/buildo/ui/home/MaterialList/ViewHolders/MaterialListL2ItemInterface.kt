package com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders

import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SubCategoryDataModel

interface MaterialListL2ItemInterface {
    fun onClickL2Item(position: Int, item: SubCategoryDataModel?)
}