package com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels

import java.io.Serializable

class SubCategoryDataWithItemsModel(var title: String? = null,  var imageUrl: String? = null): Serializable {
    var material_items: Map<String,MaterialListItemModel>? = null
    var category: String? = null
}