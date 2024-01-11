package com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels

import java.io.Serializable

class CategoriesListDataModel(): Serializable {
    var categories: HashMap<String, CategoryDataModel>? = null
}