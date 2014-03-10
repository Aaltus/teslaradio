package com.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.galimatias.teslaradio.AnimatedWebview;

import static com.utils.ViewGroupUtils.replaceView;

/**
 * Created by jimbojd72 on 2/26/14.
 */

public class TextViewJustifiedUtils {
    public static void setTextViewJustified(View v, Context context) {

            if ((v instanceof TextView)) {


                TextView textView = (TextView) v;
                //WebView mWebView = new WebView(context);
                AnimatedWebview mWebView = new AnimatedWebview(context);

                ViewGroup.LayoutParams textViewLayoutParams= (ViewGroup.LayoutParams)textView.getLayoutParams();
                mWebView.setLayoutParams(textViewLayoutParams);

                //Typeface textViewTypeface = textView.getTypeface();
                //String sTextViewTypeface = textViewTypeface.toString();

                String textViewData = textView.getText().toString();

                int iTextColor =  textView.getCurrentTextColor();
                String sTextColor = "#" + Integer.toHexString(iTextColor & 0x00FFFFFF);

                float fTextSize = textView.getTextSize();
                String sTextSize = String.valueOf(fTextSize);

                String text = "<html>"+ "<head>" +
                        "<meta name=\"viewport\" content=\"target-densitydpi=device-dpi\"/>" +
                        "</head>"+ "<style>" + "p {color:"+ sTextColor+
                        "; font-size:" + sTextSize + "px;" +
                        "}</style>"+"<body>"  +"<p align=\"justify\">"
                +  textViewData+
                "       " + "</p> " + "</body></html>";
                mWebView.setBackgroundColor(Color.TRANSPARENT);
                //Animation FadeInAnimation = AnimationUtils.loadAnimation(context,R.anim.abc_fade_in);
                //mWebView.startAnimation(FadeInAnimation);
                //mWebView.setVisibility(View.INVISIBLE);

//                mWebView.setWebViewClient(new WebViewClient() {
//
//                    boolean loadingFinished = true;
//                    boolean redirect = false;
//
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
//                        if (!loadingFinished) {
//                            redirect = true;
//                        }
//
//                        loadingFinished = false;
//                        view.loadUrl(urlNewString);
//                        return true;
//                    }
//
//                    @Override
//                    public void onPageStarted(WebView view, String url, Bitmap facIcon) {
//                        loadingFinished = false;
//                        //SHOW LOADING IF IT ISNT ALREADY VISIBLE
//                        //view.setVisibility(View.INVISIBLE);
//                        view.setVisibility(View.INVISIBLE);
//                    }
//
//                    @Override
//                    public void onPageFinished(WebView view, String url) {
//
//                        if(!redirect){
//                            loadingFinished = true;
//                        }
//
//                        if(loadingFinished && !redirect){
//                            //HIDE LOADING IT HAS FINISHED
//                            view.setVisibility(View.VISIBLE);
//
//
//                        } else{
//                            redirect = false;
//                        }
//
//                    }
//                });



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