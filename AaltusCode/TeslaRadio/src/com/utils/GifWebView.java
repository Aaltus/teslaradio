package com.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebView;
import com.aaltus.teslaradio.R;

/**
 * Created by Simon on 14-04-03.
 */
public class GifWebView extends WebView {

//    public GifWebView(Context context, String path)
//    { super(context); loadUrl(path); }

    public GifWebView(Context context, AttributeSet attrs) {
        super(context,attrs);

        String path  =  null;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GifWebView,
                0, 0);

        try {
            path = a.getString(R.styleable.GifWebView_url);
        } finally {
            a.recycle();
        }
        this.setBackgroundColor(Color.TRANSPARENT);
        loadUrl(path);
    }
}
