package com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels

import java.io.Serializable


class MaterialListItemModel : Serializable {
    @JvmField
    var imageURLs: ArrayList<String>? = null
    @JvmField
    var itemName: String? = null
    var marketPrice: Double? = null
    var itemPrice: Double? = null
    var inStock: Boolean? = null
    var unitOfMeasurement: String? = null
    var category: String? = null
    var sub_category: String? = null
    var specifications: String? = null
    var itemPolicy: String? = null
    var id: String? = null
    var pincode: Int? = null
    var path: String? = null
    var quantity: Double? = null
    var isPartialQuantityAllowed: Boolean? = null
    var deliveryStatus: Int? = 0
    var isFreeDelivery: Boolean? = false
    var sizes: ArrayList<SizeTypeItemDataModel>? = null
    var defaultSize: String? = null
    var cartListPath: String? = null
    constructor() {}

}