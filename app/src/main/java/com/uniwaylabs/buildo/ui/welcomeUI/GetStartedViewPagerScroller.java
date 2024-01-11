package com.uniwaylabs.buildo.ui.welcomeUI;

import android.content.Context;
import android.widget.Scroller;

public class GetStartedViewPagerScroller extends Scroller {

    private final int customDuration = 500;

    public GetStartedViewPagerScroller(Context context) {
        super(context);
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, customDuration);
    }

}
