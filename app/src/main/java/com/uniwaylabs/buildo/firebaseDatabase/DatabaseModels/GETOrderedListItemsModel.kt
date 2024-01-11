package com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels

import java.io.Serializable

class GETPlacedOrdersListItemsModel: Serializable {
    var placed_orders:Map<String,GETOrdersListItemsModel>? = null
}
class GETOrdersListItemsModel: Serializable{
    var orders: Map<String, GETOrderItemDetailsModel>? = null
}
