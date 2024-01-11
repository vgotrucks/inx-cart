package com.uniwaylabs.buildo.utility;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class VTAnimations {

    public static final VTAnimations shared = new VTAnimations();

    public Animation getButtonAnimation(){
        final Animation animation = new AlphaAnimation(0.0f,0.8f);
        animation.setDuration(50);
        animation.setStartOffset(20);
        animation.setRepeatMode(Animation.REVERSE);
        return animation;
    }
}
