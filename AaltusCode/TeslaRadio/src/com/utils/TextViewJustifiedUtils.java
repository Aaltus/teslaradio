package com.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import static com.utils.ViewGroupUtils.replaceView;


/**
 * A public static class that take a viewgroup as parameters
 * and convert every textview in it into a justified webview
 * with html property. It also try to extract textview
 * properties (layout_weight, text size, text color...) to make
 * the text look like the original one.
 *
 * Taking a viewgroup as parameter, the function is recursive
 * and will convert every sub-textview in the view group.
 *
 * This function is slow because Html is loaded sequentially.
 * That's make the UI unresponsive and look buggy
 */
public class TextViewJustifiedUtils {
    public static void setTextViewJustified(View v, Context context) {

            if ((v instanceof TextView)) {


                TextView textView = (TextView) v;
                WebView mWebView = new WebView(context);

                ViewGroup.LayoutParams textViewLayoutParams= (ViewGroup.LayoutParams)textView.getLayoutParams();
                mWebView.setLayoutParams(textViewLayoutParams);

                //Typeface textViewTypeface = textView.getTypeface();
                //String sTextViewTypeface = textViewTypeface.toString();

                //Extract the text
                String textViewData = textView.getText().toString();

                // Extract the text color
                int iTextColor =  textView.getCurrentTextColor();
                String sTextColor = "#" + Integer.toHexString(iTextColor & 0x00FFFFFF);

                //Extract the text size as pixel size
                float fTextSize = textView.getTextSize();
                String sTextSize = String.valueOf(fTextSize);


                String text = "<html>"+ "<head>" +
                        "<meta name=\"viewport\" content=\"target-densitydpi=device-dpi\"/>" + // need to look similar on two different device
                        "</head>"+ "<style>" + "p {color:"+ sTextColor+ //Add css color
                        "; font-size:" + sTextSize + "px;" +            //Add css textsize
                        "}</style>"+"<body>"  +"<p align=\"justify\">" //paragraph is justified
                +  textViewData+
                "       " + "</p> " + "</body></html>";

                mWebView.setBackgroundColor(Color.TRANSPARENT); // you want the text view to look transparent

                mWebView.loadData(text, "text/html", "utf-8");
                replaceView(textView, mWebView);

            }

            //If the view is a Viewgroup, make a recursive call to this function
            else if (v instanceof ViewGroup){
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);
            setTextViewJustified(child, context);

            }
    }
  }
}