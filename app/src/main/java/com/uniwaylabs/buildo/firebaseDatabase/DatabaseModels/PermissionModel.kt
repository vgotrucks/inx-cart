package com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels

import java.io.Serializable

class PermissionModel: Serializable {
    var isEdit: Boolean? = false
    var isCreate: Boolean? = false
    var isView: Boolean? = false
}