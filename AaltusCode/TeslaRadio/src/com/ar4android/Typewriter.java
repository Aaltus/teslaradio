package com.ar4android;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;
import com.ar4android.vuforiaJME.AppGetter;
import com.utils.AppLogger;

import java.util.concurrent.TimeUnit;

/**
 * Created by Batcave on 14-11-27.
 */
public class Typewriter extends TextView {

    private CharSequence mTextToAdd;
    private int mIndex;
    private long mDelay = 500; //Default 500ms delay


    public Typewriter(Context context) {
        this(context,null);
    }

    public Typewriter(Context context, AttributeSet attrSet) {
        super(context,attrSet);
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            if(mIndex < 3) {
                append(mTextToAdd);
                mIndex++;
                mHandler.postDelayed(characterAdder, mDelay);
            } else {
                setText(getText().toString().replace("...", ""));
                mHandler.postDelayed(characterAdder,700);
                mIndex = 0;
            }
        }
    };

    public void animateText(CharSequence text) {
        this.mTextToAdd = text;
        mIndex = 0;

        this.setText(this.getText().toString());
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder,mDelay);

    }

    public void removeAnimatedString() {

        mHandler.removeCallbacks(characterAdder);
        this.setText(getText().toString().replace("...", ""));
    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }
}
