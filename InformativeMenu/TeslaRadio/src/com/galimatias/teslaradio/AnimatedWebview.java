package com.galimatias.teslaradio;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by jimbojd72 on 2/27/14.
 */
public class AnimatedWebview extends WebView{

    boolean loadingFinished = true;
    boolean redirect = false;

    public AnimatedWebview(Context context) {
        super(context);
    }

    public AnimatedWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedWebview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


    }

    //    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        setVisibility(VISIBLE);
//        ScaleAnimation anim = new ScaleAnimation(0,1,0,1);
//        anim.setDuration(1000);
//        anim.setFillAfter(true);
//        this.startAnimation(anim);
//    }
}
