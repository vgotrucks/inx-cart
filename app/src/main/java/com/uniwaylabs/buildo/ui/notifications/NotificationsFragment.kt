package com.uniwaylabs.buildo.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.databinding.FragmentNotificationsBinding
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.GETDeliveredOrdersListItemsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.GETOrderItemDetailsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.GETOrdersListItemsModel
import com.uniwaylabs.buildo.ui.history.HistoryListActivity
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.OrderItemDetailsDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.GETPlacedOrdersListItemsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.firebaseDatabase.Repositories.RepositoryData
import com.uniwaylabs.buildo.ui.notifications.OrderedList.OrderListItemInterface
import com.uniwaylabs.buildo.ui.notifications.OrderedList.OrderedListItemAdapter
import java.util.Date

class NotificationsFragment : Fragment(), OrderListItemInterface {

    private var historyListActivity = registerForActivityResult(HistoryListActivity.Contract(), {})
    var progressBar: LottieAnimationView? = null
    var emptyState: ImageView? = null
    var statusText: TextView? = null
    private var binding: FragmentNotificationsBinding? = null
    private var adapter:OrderedListItemAdapter? = null
    private var isOrdersSelected = true
    private var ordersBtn: AppCompatButton? = null
    private var historyBtn: AppCompatButton? = null
    private var pincode: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val notificationsViewModel = ViewModelProvider(this).get(
            NotificationsViewModel::class.java
        )
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        progressBar = binding?.progressbarhistory
        emptyState = binding?.imageViewSadH
        statusText = binding?.textViewResultH
        setMaterialRecycler()
        historyBtn = binding!!.historyBtn
        ordersBtn = binding!!.ordersBtn
        pincode = BDSharedPreferences.shared.getSelectedPostalAreaCode(this.requireContext())
        ordersBtn?.setOnClickListener {
            setOrdersButtonSelected()
            fetchList(false)
        }

        historyBtn?.setOnClickListener {
            setHistoryButtonSelected()
            fetchList(true)
        }

        fetchList(false)
        setOrdersButtonSelected()
        return root
    }

    private fun setHistoryButtonSelected(){
        ordersBtn?.background = resources.getDrawable(R.drawable.boxline_white_color_rounded, context?.theme)
        ordersBtn?.setTextColor(resources.getColor(R.color.white, context?.theme))
        historyBtn?.background = resources.getDrawable(R.drawable.background_button_white_20dp, context?.theme)
        historyBtn?.setTextColor(resources.getColor(R.color.navbarColor, context?.theme))
        isOrdersSelected = false
    }

    private fun setOrdersButtonSelected(){
        historyBtn?.background = resources.getDrawable(R.drawable.boxline_white_color_rounded, context?.theme)
        historyBtn?.setTextColor(resources.getColor(R.color.white, context?.theme))
        ordersBtn?.background = resources.getDrawable(R.drawable.background_button_white_20dp, context?.theme)
        ordersBtn?.setTextColor(resources.getColor(R.color.navbarColor, context?.theme))
        isOrdersSelected = true
    }

    private fun fetchList(isDelivered: Boolean){

        val isAdminView = BDSharedPreferences.shared.getPermissionModel(this.requireContext())?.isView ?: false

        if(isDelivered){
            if (isAdminView) fetchDeliveredList() else fetchUserOrdersList(true)
        }
        else{
            if (isAdminView) fetchOrdersList() else fetchUserOrdersList(false)
        }
    }

    private fun fetchUserOrdersList(isDelivered: Boolean){

        val orderPath = if(isDelivered) DatabaseUrls.delivered_orders else DatabaseUrls.placed_orders
        val path = "$pincode/$orderPath/${RepositoryData.getInstance().userId}/"

        showLoading(true, "Fetching...")
        AdminDatabase<Map<String, Any?>>().getItemsObjectDataSnapshot(this.requireContext(), path) { it ->
            showLoading(false, "")
            val mData = try {
                it.getValue(GETOrdersListItemsModel::class.java)
            }catch (e: Exception){ null }

            if ((isDelivered && isOrdersSelected) || (!isDelivered && !isOrdersSelected)) return@getItemsObjectDataSnapshot

            if(mData?.orders.isNullOrEmpty()){
                showEmptyState(true)
                adapter?.updateListData(emptyArray())
                return@getItemsObjectDataSnapshot
            }
            showEmptyState(false)
            adapter?.updateListData( mData?.orders?.values?.toTypedArray() ?: emptyArray())
        }
    }

    private fun fetchOrdersList(){

        showLoading(true, "Fetching...")
        AdminDatabase<Map<String, Any?>>().getItemsObjectDataSnapshot(this.requireContext(), "$pincode/") { it ->
            showLoading(false, "")
            val mData = try {
                it.getValue(GETPlacedOrdersListItemsModel::class.java)
            }catch (e: Exception){ null }


            if (!isOrdersSelected) { return@getItemsObjectDataSnapshot}

            if(mData?.placed_orders.isNullOrEmpty()){
                showEmptyState(true)
                adapter?.updateListData(emptyArray())
                return@getItemsObjectDataSnapshot
            }
            showEmptyState(false)
            val data = mData?.placed_orders?.values?.toTypedArray()?.flatMap { it.orders?.values?.asIterable() ?: emptyList() }
            adapter?.updateListData( data?.toTypedArray() ?: emptyArray())
        }
    }

    private fun fetchDeliveredList(){

        showLoading(true, "Fetching...")
        AdminDatabase<Map<String, Any?>>().getItemsObjectDataSnapshot(this.requireContext(), "$pincode/") {
            showLoading(false, "")
            val mData = try {
                it.getValue(GETDeliveredOrdersListItemsModel::class.java)
            }catch (e: Exception){ null }


            if (isOrdersSelected) { return@getItemsObjectDataSnapshot}

            if(mData?.delivered_orders.isNullOrEmpty()){
                showEmptyState(true)
                adapter?.updateListData(emptyArray())
                return@getItemsObjectDataSnapshot
            }
            showEmptyState(false)
            val data = mData?.delivered_orders?.values?.toTypedArray()?.flatMap { it.orders?.values?.asIterable() ?: emptyList() }
            adapter?.updateListData( data?.toTypedArray() ?: emptyArray())
        }
    }

    fun showLoading(show: Boolean, title: String){
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        statusText?.text = title
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        emptyState?.visibility = View.INVISIBLE
    }

    fun showEmptyState(show: Boolean){
        statusText?.text = "OOPs! No Orders Found"
        emptyState?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }


    private fun setMaterialRecycler() {
        val recyclerView = binding!!.materialsRecycle
        adapter = OrderedListItemAdapter(context, emptyArray(), this)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onClickListItem(model: GETOrderItemDetailsModel?) {
        model.let { historyListActivity.launch(it) }
    }

    override fun didTapChangeStatus(model: GETOrderItemDetailsModel?) {

        if (BDSharedPreferences.shared.getPermissionModel(this.requireContext())?.isEdit != true ) return

        val placedOrdersPath =  "${model?.orderDetails?.pincode ?: pincode}/${DatabaseUrls.placed_orders}/${model?.orderDetails?.userID}/${DatabaseUrls.orders}/${model?.orderID}"
        val deliveredOrderPath = "${model?.orderDetails?.pincode ?: pincode}/${DatabaseUrls.delivered_orders}/${model?.orderDetails?.userID}/${DatabaseUrls.orders}/${model?.orderID}"


        val pathToSave = if (model?.deliveryStatus == 1) placedOrdersPath else deliveredOrderPath
        val deletePath = if (model?.deliveryStatus == 1) deliveredOrderPath else placedOrdersPath

        showLoading(true, "Fetching...")
        AdminDatabase<String>().getItemsObjectDataSnapshotWithoutEventListener(this.requireContext(),deletePath){
            showLoading(true, "Fetching...")
            val mData = try {
                it.getValue(OrderItemDetailsDataModel::class.java)
            }catch (e: Exception){ null }

            mData?.deliveryStatus = if (model?.deliveryStatus == 0) 1 else 0
            mData?.deliveredDate = if(mData?.deliveryStatus == 0) null else Date().time
            mData?.materialListPath = "$pathToSave/materials"
            AdminDatabase<String>().setDataToItemDatabase(this.requireContext(), pathToSave,mData){}
            AdminDatabase<String>().deleteFromDatabase(deletePath)
        }

    }


}