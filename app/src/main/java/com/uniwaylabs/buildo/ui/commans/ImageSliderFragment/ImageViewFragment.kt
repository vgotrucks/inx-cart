package com.uniwaylabs.buildo.ui.commans.ImageSliderFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.databinding.FragmentImageViewBinding



class ImageViewFragment: Fragment(), OnClickListener {

    private var binding: FragmentImageViewBinding? = null
    private var imageView: ImageView? = null
    private var imagePosition: Int? = null
    var sliderInterface: ImageSliderFragmentInterface? =  null
    private var imageURL: String? = null
    private var deleteButton: ImageButton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentImageViewBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        imageView = binding!!.itemImage
        root.setOnClickListener(this)
        deleteButton = binding?.btnDelete
        deleteButton?.setOnClickListener {
            sliderInterface?.onDeleteTap(imagePosition, imageURL)
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.takeIf { it.containsKey(IMAGE_URL) }?.apply {
            val imageUrlString = getString(IMAGE_URL)
             imageURL = imageUrlString
            if (imageUrlString != null) {
                setImage(imageUrlString)
            }
        }
        arguments?.takeIf { it.containsKey(IMAGE_URL_POSITION) }?.apply {
            imagePosition = getInt(IMAGE_URL_POSITION)
        }
        arguments?.takeIf { it.containsKey(SHOW_DELETE) }?.apply {
            deleteButton?.visibility = if (getBoolean(SHOW_DELETE)) View.VISIBLE else View.GONE
        }
    }

    fun set(url: String, position: Int){
        setImage(url)
        imagePosition = position
        imageURL = url
    }

    private fun setImage(url: String){
        val options = RequestOptions().error(R.drawable.ic_placeholder_image_48)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .transform(CenterCrop())

        context?.let { imageView?.let { it1 -> Glide.with(it).load(url).apply(options).into(it1) } }
    }

    override fun onClick(p0: View?) {
        sliderInterface?.onImageTap(imagePosition, imageURL )
    }
}