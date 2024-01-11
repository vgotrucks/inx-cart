package com.uniwaylabs.buildo.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.uniwaylabs.buildo.LocalDatabase.BDSharedPreferences
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.databinding.FragmentHomeBinding
import com.uniwaylabs.buildo.firebaseDatabase.Database.AdminDB.AdminDatabase
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.ui.home.MaterialList.MaterialListItemAdapter
import com.uniwaylabs.buildo.ui.materialItemsList.MaterialItemsListActivity
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL1ItemInterface
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL2ItemInterface
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialTypeListModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SubCategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseUrls.DatabaseUrls
import com.uniwaylabs.buildo.ui.adminForms.ACTIVITY_DATA
import com.uniwaylabs.buildo.ui.materialItemsList.MaterialCategoryItemsActivity
import com.uniwaylabs.buildo.ui.materialItemsList.MaterialSubCategoryItemsActivity
import com.uniwaylabs.buildo.ui.materialSpecification.MaterialSpecificationActivity


class HomeFragment : Fragment(), MaterialListL1ItemInterface, MaterialListL2ItemInterface {
    private var binding: FragmentHomeBinding? = null
    var adapter: MaterialListItemAdapter? = null
    var progressBar: LottieAnimationView? = null
    var emptyState: ImageView? = null
    var statusText: TextView? = null

    private val materialListActivityWithCategory = registerForActivityResult(MaterialCategoryItemsActivity.Contract(ACTIVITY_DATA), {})
    private val materialListActivityWithSubCategory = registerForActivityResult(MaterialSubCategoryItemsActivity.Contract(ACTIVITY_DATA), {})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val homeViewModel = ViewModelProvider(this).get(
            HomeViewModel::class.java
        )
        binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        AdminDatabase<CategoriesListDataModel>().getItemsObjectDataSnapshot(this.requireActivity(),"$pincode/"){
            showLoading(false, title = "")

            val mData = try {
                it.getValue(CategoriesListDataModel::class.java)
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
        statusText?.text = "OOPs! Currently, We are not at this place"
        emptyState?.visibility = if (show) View.VISIBLE else View.INVISIBLE
        statusText?.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun setMaterialRecycler() {
        val recyclerView = binding!!.materialsRecycle
        adapter = context?.let { MaterialListItemAdapter(it, this, this) }
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onClickL1Item(position: Int, item: CategoryDataModel?) {
       materialListActivityWithCategory.launch(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onClickL2Item(position: Int, item: SubCategoryDataModel?) {
       materialListActivityWithSubCategory.launch(item)
    }

}