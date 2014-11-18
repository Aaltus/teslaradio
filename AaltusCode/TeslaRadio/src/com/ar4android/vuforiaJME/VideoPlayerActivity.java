package com.ar4android.vuforiaJME;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;
import com.aaltus.teslaradio.R;

/**
 * Created by jimbojd72 on 10/27/2014.
 */
public class VideoPlayerActivity extends Activity implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,View.OnTouchListener {

    public static final String FILE_RES_ID = "fileRes";
    private VideoView mVV;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.videoplayer);

        int fileRes=0;
        Bundle e = getIntent().getExtras();
        if (e!=null) {
            fileRes = e.getInt(FILE_RES_ID);
        }

        mVV = (VideoView)findViewById(R.id.myvideoview);
        mVV.setOnCompletionListener(this);
        mVV.setOnPreparedListener(this);
        mVV.setOnTouchListener(this);

        if (!playFileRes(fileRes)) return;

        mVV.start();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int fileRes = 0;
        Bundle e = getIntent().getExtras();
        if (e != null) {
            fileRes = e.getInt(FILE_RES_ID);
        }
        playFileRes(fileRes);
    }

    private boolean playFileRes(int fileRes) {
        if (fileRes==0) {
            stopPlaying();
            return false;
        } else {
            mVV.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + fileRes));
            return true;
        }
    }

    public void stopPlaying() {
        mVV.stopPlayback();
        this.finish();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        stopPlaying();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setLooping(true);
    }
}
