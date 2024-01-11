package com.uniwaylabs.buildo.ui.notifications.OrderedList

import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import java.io.Serializable

class OrderedListItemModel(var itemType: String, var list: Array<MaterialListItemModel>): Serializable