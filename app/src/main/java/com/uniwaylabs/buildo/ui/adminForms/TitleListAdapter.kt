package com.uniwaylabs.buildo.ui.adminForms

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.CategoryDataModel
import com.uniwaylabs.buildo.firebaseDatabase.DatabaseModels.SubCategoryDataModel

public interface TitleListAdapterInterface{
    fun onSelectItem(title: String)
    fun onClickAdd(title: String, item: Any?)
}

class TitleListAdapter(
    var context: Context,
    var itemInterface: TitleListAdapterInterface?) : RecyclerView.Adapter<TitleListAdapter.ViewHolder>() {

    private var resultantTitles: ArrayList<String> = ArrayList()
        set(value) {
            titles = value
            field = value
        }

    private var titles: ArrayList<String> = ArrayList()

    private var resultantList: Map<String, Any>? = emptyMap()
        set(value: Map<String, Any>?){
            field = value
            resultantTitles = (value?.map { it.key as String }) as ArrayList<String>
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.title_list_item, parent, false)
        return ViewHolder(view, itemInterface)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (titles.size < position) {
            return
        }

        var titleText = titles[position]
        holder.setData(resultantList?.get(titleText))
        holder.text.text = titleText
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun applySearch(text: String?){

        val query = (text ?: "").trim()
        titles = if(query.isEmpty()) resultantTitles else resultantTitles.filter { it.contains(query, true) } as ArrayList<String>
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateListData(list: Map<String, Any>?) {
        this.resultantList = list ?: emptyMap()
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View, var itemInterface: TitleListAdapterInterface?) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var text: TextView
        var imageView: ImageView
        var addButton: AppCompatButton
        var modelData: Any? = null

        init {
            view.setOnClickListener(this)
            text = view.findViewById(R.id.title_tv)
            imageView = view.findViewById(R.id.img_profile)
            addButton = view.findViewById(R.id.button_add)
            addButton.setOnClickListener {
                itemInterface?.onClickAdd(text?.text.toString() ?: "", modelData)
            }
        }

        fun setData(modelData: Any?){
            this.modelData = modelData
            if(modelData is CategoryDataModel){
                setImage(modelData.imageUrl)
            }
            else if (modelData is SubCategoryDataModel){
                setImage(modelData.imageUrl)
            }

        }

        private fun setImage(url: String?){
            val options = RequestOptions().error(R.drawable.ic_placeholder_image_48)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
            Glide.with(context).load(url ?: "").apply(options).into(imageView)
        }

        override fun onClick(p0: View?) {
            itemInterface?.onSelectItem(text.text.toString())
        }
    }
}