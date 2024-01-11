package com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels

import java.io.Serializable

class OrderItemDetailsDataModel: Serializable {
    var orderDetails: UserOrderDetails? = null
    var materials: CategoriesListWithItemDataModel? = null
    var orderedDate: Long? = null
    var materialListPath: String? = null
    var orderID: String? = null
    var invoiceAmount: Double? = null
    var offAmount: Double? = null
    var deliveryStatus: Int? = 0
    var deliveredDate: Long? = null
}