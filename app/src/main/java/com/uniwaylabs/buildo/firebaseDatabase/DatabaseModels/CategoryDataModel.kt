package com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels

import java.io.Serializable

class CategoryDataModel(var title: String? = null,  var imageUrl: String? = null): Serializable {

   var subCategories: Map<String,SubCategoryDataModel>? = null
   var material_items: Map<String,MaterialListItemModel>? = null

}