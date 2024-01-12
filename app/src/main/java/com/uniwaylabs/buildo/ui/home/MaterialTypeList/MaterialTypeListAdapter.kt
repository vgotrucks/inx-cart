package com.uniwaylabs.buildo.ui.home.MaterialTypeList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoriesListDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialTypeListModel
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL1ItemInterface

class MaterialTypeListAdapter(
    var context: Context?,
    private var itemInterface: MaterialListL1ItemInterface?,
    private var list: Array<CategoryDataModel>
) : RecyclerView.Adapter<MaterialTypeListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.material_list_type_item, parent, false)
        return ViewHolder(view, itemInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.size < position) {
            return
        }
        val materialType = list[position]
        holder.text.text = materialType.title
        holder.setImage(materialType.imageUrl)
        holder.item = materialType
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateListData(list: Array<CategoryDataModel>?) {
        this.list = list ?: emptyArray()
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View, itemInterface: MaterialListL1ItemInterface?) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        var text: TextView
        var itemInterface: MaterialListL1ItemInterface?
        var imageView: ImageView
        var item: CategoryDataModel? = null

        init {
            view.setOnClickListener(this)
            this.itemInterface = itemInterface
            text = view.findViewById<View>(R.id.text_home) as TextView
            imageView = view.findViewById(R.id.item_image)
        }

        fun setImage(url: String?){
            val options = RequestOptions().error(R.drawable.ic_placeholder_image_48)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
            context?.let { Glide.with(it).load(url ?: "").apply(options).into(imageView) }
        }
        override fun onClick(view: View) {
            itemInterface?.onClickL1Item(this.layoutPosition, item)
        }
    }
}