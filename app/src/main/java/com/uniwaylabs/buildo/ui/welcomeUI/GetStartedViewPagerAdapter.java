package com.uniwaylabs.buildo.ui.welcomeUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.uniwaylabs.buildo.R;

public class GetStartedViewPagerAdapter extends PagerAdapter {

    int[] images;
    Context context;

    public GetStartedViewPagerAdapter(){

    }

    public GetStartedViewPagerAdapter(Context context, int[] images){
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if( layoutInflater == null)
            return super.instantiateItem(container, position);
        View itemView = layoutInflater.inflate(R.layout.layout_getstarted_viewpager,container,false);
        ImageView image = (ImageView) itemView.findViewById(R.id.getstarted_poster_imageview);
        image.setImageResource(images[position]);
        container.addView(itemView);
        if(position == 1)
            itemView.setBackgroundColor(context.getColor(R.color.white));
        return itemView;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
       container.removeView((LinearLayout) object);
    }
}
