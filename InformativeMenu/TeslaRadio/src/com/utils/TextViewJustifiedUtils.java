package com.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import static com.utils.ViewGroupUtils.replaceView;

/**
 * Created by jimbojd72 on 2/26/14.
 */

public class TextViewJustifiedUtils {
    public static void setTextViewJustified(View v, Context context) {

            if ((v instanceof TextView)) {

                TextView textView = (TextView) v;
                WebView mWebView = new WebView(context);

                String text = "<html><body>" + "<p align=\"justify\">"
                + textView.getText().toString() +
                "       " + "</p> " + "</body></html>";
                mWebView.setBackgroundColor(Color.TRANSPARENT);
                mWebView.loadData(text, "text/html", "utf-8");


                replaceView(textView, mWebView);

            }

            else if (v instanceof ViewGroup){
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);
            setTextViewJustified(child, context);

            }
    }
  }
}