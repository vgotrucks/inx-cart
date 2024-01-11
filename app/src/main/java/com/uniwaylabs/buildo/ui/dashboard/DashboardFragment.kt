package com.uniwaylabs.buildo.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.ui.dashboard.CartList.CartListItemAdapter
import com.uniwaylabs.buildo.ui.dashboard.CartList.CartListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.databinding.FragmentDashboardBinding
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.Database.UserDB.UserDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListWithItemDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataWithItemsModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.ui.dashboard.CartList.ViewHolders.CartListActionItemViewInterface
import com.uniwaylabs.buildo.ui.dashboard.CartListMaterial.CartListItemMaterialInterface
import com.uniwaylabs.buildo.ui.materialSpecification.MaterialSpecificationActivity
import com.uniwaylabs.buildo.ui.orderDetailsForm.OrderDetailsFormActivity

class DashboardFragment : Fragment(), CartListItemMaterialInterface, CartListActionItemViewInterface {

    private var binding: FragmentDashboardBinding? = null
    private val materialSpecialityActivity = registerForActivityResult(MaterialSpecificationActivity.Contract(), {})
    private val orderDetailFormActivity = registerForActivityResult(OrderDetailsFormActivity.Contract(), {})
    var progressBar: LottieAnimationView? = null
    var emptyState: ImageView? = null
    var statusText: TextView? = null
    private var adapter: CartListItemAdapter? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val dashboardViewModel = ViewModelProvider(this).get(
            DashboardViewModel::class.java
        )
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        progressBar = binding?.progressbarhistory
        emptyState = binding?.imageViewSadH
        statusText = binding?.textViewResultH
        setMaterialRecycler()
        fetchList()
        return root
    }


    fun fetchList(){

        showLoading(true, "Fetching...")
        val pincode = BDSharedPreferences.shared.getSelectedPostalAreaCode(this.requireActivity())
        UserDatabase<CategoriesListWithItemDataModel>().getItemsObjectDataSnapshot(this.requireActivity(),"${DatabaseUrls.cart_list_path}/$pincode/"){
            showLoading(false, title = "")

            val mData = try {
                it.getValue(CategoriesListWithItemDataModel::class.java)
            }catch (e: Exception){ null }

            if(mData?.categories.isNullOrEmpty()){
                showEmptyState(true)
                adapter?.updateListData(emptyArray())
                return@getItemsObjectDataSnapshot
            }
            showEmptyState(false)
            adapter?.updateListData(mData?.categories?.values?.toTypedArray() ?: emptyArray())
        }
    }

    fun showLoading(show: Boolean, title: String){
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        statusText?.text = title
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        emptyState?.visibility = View.INVISIBLE
    }

    fun showEmptyState(show: Boolean){
        statusText?.text = "No items found in your cart."
        emptyState?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        recyclerView?.visibility = if (show) View.INVISIBLE else View.VISIBLE

    }

    private fun setMaterialRecycler() {
        recyclerView = binding!!.materialsRecycle
        adapter = context?.let { CartListItemAdapter(it, emptyArray(), this, this) }
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onClickCartListItem(model: MaterialListItemModel?) {
        model.let { materialSpecialityActivity.launch(it) }
    }

    override fun onClickBuyNow() {
       orderDetailFormActivity.launch("")
    }
}