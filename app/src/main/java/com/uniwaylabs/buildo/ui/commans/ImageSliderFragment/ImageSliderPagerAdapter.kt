package com.uniwaylabs.buildo.ui.commans.ImageSliderFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

class ImageSliderPagerAdapter(fragment: Fragment, images: Array<String>, var showDelete: Boolean = false, sliderInterface: ImageSliderFragmentInterface?): FragmentStateAdapter(fragment) {

    private var images: Array<String>
    private var sliderInterface: ImageSliderFragmentInterface? =  null

    init {
        this.images = images
        this.sliderInterface = sliderInterface
    }

    override fun getItemCount(): Int {
        return images.count()
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ImageViewFragment()
        fragment.sliderInterface = sliderInterface
        val bundle = Bundle()
        fragment.arguments = bundle.apply { putString(IMAGE_URL, images[position]) }.apply{ putInt(IMAGE_URL_POSITION, position) }.apply { putBoolean(SHOW_DELETE, showDelete) }
        return  fragment
    }

    fun setImages(images: Array<String>){
        this.images = images
        notifyDataSetChanged()
    }

}