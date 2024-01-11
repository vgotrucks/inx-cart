package com.uniwaylabs.buildo.ui.home.MaterialList

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
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.MaterialListItemModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SubCategoryDataModel
import com.uniwaylabs.buildo.ui.home.MaterialList.ViewHolders.MaterialListL2ItemInterface

class MaterialListL2Adapter(
    var context: Context?,
    private var itemInterface: MaterialListL2ItemInterface?,
    private var list: Array<SubCategoryDataModel>
) : RecyclerView.Adapter<MaterialListL2Adapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.material_list_l2_sub_item, parent, false)
        return ViewHolder(view, itemInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list.size < position) {
            return
        }
       holder.set(list.get(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateListData(list: Array<SubCategoryDataModel>?) {
        this.list = list ?: emptyArray()
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View, itemInterface: MaterialListL2ItemInterface?) :
        RecyclerView.ViewHolder(view), View.OnClickListener {

        val textView: TextView
        val imageView: ImageView
        var itemInterface: MaterialListL2ItemInterface?
        private var item: SubCategoryDataModel? = null

        init {
            view.setOnClickListener(this)
            this.itemInterface = itemInterface
            textView = view.findViewById<TextView>(R.id.text_item_name)
            imageView = view.findViewById<ImageView>(R.id.item_image)
        }

        fun set(model: SubCategoryDataModel?){
            this.item = model
            textView.text = model?.title
            val options = RequestOptions().error(R.drawable.ic_placeholder_image_48)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transform(RoundedCorners(10), CenterCrop())
            context?.let { Glide.with(it).load(model?.imageUrl).apply(options).into(imageView) }
        }

        override fun onClick(view: View) {
            itemInterface?.onClickL2Item(this.layoutPosition, item)
        }
    }
}