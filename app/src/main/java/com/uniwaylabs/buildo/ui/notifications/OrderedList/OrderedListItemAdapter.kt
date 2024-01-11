package com.uniwaylabs.buildo.ui.notifications.OrderedList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.GETOrderItemDetailsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.OrderItemDetailsDataModel
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

public interface OrderListItemInterface{
    fun onClickListItem(model: GETOrderItemDetailsModel?)
    fun didTapChangeStatus(model: GETOrderItemDetailsModel?)
}

class OrderedListItemAdapter(var context: Context?, var list: Array<GETOrderItemDetailsModel>, var listInterface: OrderListItemInterface?) :
    RecyclerView.Adapter<OrderedListItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.order_list_item, parent, false)
        return ViewHolder(view, listInterface, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.size < position) {
            return
        }
        holder.setListData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateListData(list: Array<GETOrderItemDetailsModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View?, var listItemInterface: OrderListItemInterface?, var context: Context?) : RecyclerView.ViewHolder(view!!), OnClickListener {

        var model: GETOrderItemDetailsModel? = null
        var orderIdTV: TextView? = null
        var orderedOnTV: TextView? = null
        var expectedDeliveryTV: TextView? = null
        var addressTV: TextView? = null
        var invoiceAmountTV: TextView? = null
        var statusBadge: TextView? = null
        var receiverName: TextView? = null
        var phoneNumber: TextView? = null

        init {
            view?.setOnClickListener(this)
            orderIdTV = view?.findViewById(R.id.order_id_text)
            orderedOnTV = view?.findViewById(R.id.order_on_text)
            expectedDeliveryTV = view?.findViewById(R.id.expected_on_text)
            addressTV = view?.findViewById(R.id.delivery_address_text)
            invoiceAmountTV = view?.findViewById(R.id.order_amount_text)
            statusBadge = view?.findViewById(R.id.status_text)
            receiverName = view?.findViewById(R.id.receiver_name)
            phoneNumber = view?.findViewById(R.id.mobile_number)
            statusBadge?.setOnClickListener {
                listItemInterface?.didTapChangeStatus(model)
            }
        }
        fun setListData(model: GETOrderItemDetailsModel?) {
            this.model = model
            orderIdTV?.text = model?.orderID
            orderedOnTV?.text = "NA"
            if (model?.orderedDate != null){
                val orderedOn = model?.orderedDate ?: 0
                val date = Date(orderedOn)
                val format = SimpleDateFormat("dd MMM, YYYY hh:mm aa")
                orderedOnTV?.text = "Ordered On - ${format.format(date)}"
            }
            invoiceAmountTV?.text = "Invoice Amount - ₹${model?.invoiceAmount ?: 0} /-"
            expectedDeliveryTV?.text = "Delivered On - NA"
            if (model?.deliveredDate != null){
                val orderedOn = model?.deliveredDate ?: 0
                val date = Date(orderedOn)
                val format = SimpleDateFormat("dd MMM, YYYY hh:mm aa")
                expectedDeliveryTV?.text = "Delivered On - ${format.format(date)}"
            }
            addressTV?.text = "${model?.orderDetails?.address ?: ""}, ${model?.orderDetails?.pincode ?: ""}"

            if (model?.deliveryStatus == 1){
                statusBadge?.text = "Delivered"
                statusBadge?.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_round_corners_lightgreen10dp) }
                context?.let { statusBadge?.setTextColor(ContextCompat.getColor(it, R.color.systemGreenColor))}
            }
            else{
                statusBadge?.text = "Delivery Pending"
                statusBadge?.background = context?.let { ContextCompat.getDrawable(it,R.drawable.background_cash) }
                context?.let { statusBadge?.setTextColor(ContextCompat.getColor(it, R.color.black))}
            }
            receiverName?.text = "Receiver's Name - ${model?.orderDetails?.receiverName ?: "NA"}"
            phoneNumber?.text = "Phone - ${model?.orderDetails?.mobileNumber ?: "NA"}"
        }

        override fun onClick(p0: View?) {
            listItemInterface?.onClickListItem(model)
        }

    }
}