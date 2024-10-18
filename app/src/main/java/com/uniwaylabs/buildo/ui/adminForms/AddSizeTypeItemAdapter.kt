package com.uniwaylabs.buildo.ui.adminForms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SizeTypeItemDataModel

class AddSizeTypeItemAdapter(
    var context: Context?,
    private var list: Array<SizeTypeItemDataModel>
) : RecyclerView.Adapter<AddSizeTypeItemAdapter.ViewHolder>() {

    private var selectedSizeText: String? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_size_type_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.size < position) {
            return
        }
        val model = list.get(position)
        holder.set(model)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateListData(list: Array<SizeTypeItemDataModel>?) {
        this.list = list ?: emptyArray()
        notifyDataSetChanged()
    }

    fun addToLast(list: Array<SizeTypeItemDataModel>?){
        var listItems = list?.toMutableList()
        listItems?.add(SizeTypeItemDataModel())
        this.list = listItems?.toTypedArray() ?: emptyArray()
        notifyDataSetChanged()
    }




    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view){

        val sizeNameET: EditText
        val itemPriceET: EditText
        val marketPriceET: EditText
        val inStockSwitch: Switch

        private var item: SizeTypeItemDataModel? = null

        init {
            sizeNameET = view.findViewById(R.id.size_name_et)
            itemPriceET = view.findViewById(R.id.item_price_et)
            marketPriceET = view.findViewById(R.id.item_market_price_et)
            inStockSwitch = view.findViewById(R.id.quantity_measurement_switch)
        }

        fun set(model: SizeTypeItemDataModel?){
            this.item = model
            sizeNameET.setText(model?.displayText ?: "")
            itemPriceET.setText((model?.itemPrice ?: "").toString())
            marketPriceET.setText((model?.marketPrice ?: "").toString())
            inStockSwitch.isChecked = model?.inStock ?: true
        }

    }
}