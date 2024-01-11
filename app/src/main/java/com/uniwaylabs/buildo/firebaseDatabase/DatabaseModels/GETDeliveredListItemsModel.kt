package com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels

import java.io.Serializable

class GETDeliveredOrdersListItemsModel: Serializable {
    var delivered_orders:Map<String, GETOrdersListItemsModel>? = null
}
