package com.utils;


import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;
import com.galimatias.teslaradio.R;

/**
 * Created by jimbojd72 on 10/21/14.
 */
public class CustomVideoView extends VideoView {

    private PlayPauseListener mListener;

    public CustomVideoView(Context context) {
        super(context);
        initVideoViewListener();
        loadVideoURI(context,null);

    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoViewListener();
        loadVideoURI(context,attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoViewListener();
        loadVideoURI(context,attrs);
    }

    private void loadVideoURI(Context context, AttributeSet attrs){

        if(context != null && attrs != null){
            String uri;
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CustomVideoView,
                    0, 0);

            try {
                uri = a.getString(R.styleable.CustomVideoView_uri);
                //Log.e("Chat", "Test uri:" + uri);
                //this.setVideoPath(uri);
                this.setVideoURI(Uri.parse(uri));
                //this.start();
                //this.pause();

            } finally {
                a.recycle();
            }
        }


    }

    private void initVideoViewListener(){
        this.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {

            @Override
            public void onPlay() {
                //System.out.println("Play!");
            }

            @Override
            public void onPause() {
                //System.out.println("Pause!");
            }
        });

        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //        Log.d("TagVideo", "Video 1 clicked");
                if (isPlaying()) {
                    pause();
                    //Log.d("TagVideo", "Video pause");
                } else {
                    start();
                    //Log.d("TagVideo", "Video start");

                }
                return false;
            }
        });

        this.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
                //mp.pause();
            }
        });
    }


    public void setPlayPauseListener(PlayPauseListener listener) {
        mListener = listener;
    }

    @Override
    public void pause() {
        super.pause();
        if (mListener != null) {
            mListener.onPause();
        }
    }

    @Override
    public void start() {
        super.start();
        if (mListener != null) {
            mListener.onPlay();
        }
    }

    public static interface PlayPauseListener {
        void onPlay();
        void onPause();
    }

}
