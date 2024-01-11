package com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels

import java.io.Serializable

class UserOrderDetails: Serializable {
    var receiverName: String? = null
    var mobileNumber: String? = null
    var address: String? = null
    var pincode: String? = null
    var userID: String? = null
}