package com.uniwaylabs.buildo.ui.history

import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.GETOrderItemDetailsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel

public interface HistoryListItemMaterialInterface {
    fun onClickHistoryListItem(model: MaterialListItemModel?)
    fun didTapChangeStatus(model: MaterialListItemModel?)
}