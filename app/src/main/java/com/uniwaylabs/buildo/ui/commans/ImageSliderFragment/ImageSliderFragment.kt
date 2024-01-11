package com.uniwaylabs.buildo.ui.commans.ImageSliderFragment

import android.content.res.Resources.Theme
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.load.engine.Resource
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.databinding.FragmentItemImageSliderBinding

public const val SLIDING_IMAGES = "SLIDING_IMAGES"
const val IMAGE_URL = "IMAGE_URL"
const val IMAGE_URL_POSITION = "IMAGE_URL_POSITION"
const val SHOW_DELETE = "SHOW_DELETE"

public interface ImageSliderFragmentInterface{
    fun onImageTap(position: Int?, url: String?)
    fun onDeleteTap(position: Int?, url: String?)
}

class ImageSliderFragment(): Fragment() {

    private var binding: FragmentItemImageSliderBinding? = null
    private var adapter: ImageSliderPagerAdapter? = null
    private var tabView: TabLayout? = null
    var sliderInterface: ImageSliderFragmentInterface? =  null
    private var pager: ViewPager2? = null
    private var showDelete = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentItemImageSliderBinding.inflate(inflater, container, false)
        val root: View = binding!!.root
        pager = binding!!.imgPager
        adapter = ImageSliderPagerAdapter(this, emptyArray(), false, sliderInterface)
        pager?.adapter = adapter
        tabView = binding!!.tabView
        TabLayoutMediator(tabView!!, pager!!, true) { tab,position ->
            tab.setIcon(R.drawable.tab_selector)
        }.attach()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.takeIf { it.containsKey(SLIDING_IMAGES) }?.apply {
            val imageUrls = getStringArray(SLIDING_IMAGES)
            if (imageUrls != null) {
                adapter?.setImages(imageUrls)
                adapter?.notifyDataSetChanged()
            }
        }
        arguments?.takeIf { it.containsKey(SHOW_DELETE) }?.apply {
            adapter?.showDelete = getBoolean(SHOW_DELETE)
            showDelete = getBoolean(SHOW_DELETE)
            adapter?.notifyDataSetChanged()
        }
    }

    fun resetImage(images: Array<String>){
        adapter = ImageSliderPagerAdapter(this, images, showDelete, sliderInterface)
        pager?.adapter = adapter
        TabLayoutMediator(tabView!!, pager!!, true) { tab,position ->
            tab.setIcon(R.drawable.tab_selector)
        }.attach()
    }
}