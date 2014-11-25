package com.aaltus.teslaradio;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.utils.AppLogger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Batcave on 14-11-19.
 * Code taken from https://github.com/blessenm/AndroidAutoScrollListView/blob/master/src/com/blessan/VerticalSlideshow.java
 */
public class CreditsFragment extends Fragment {

    private static final String TAG = CreditsFragment.class.getSimpleName();

    private LinearLayout verticalOuterLayout;
    private ScrollView verticalScrollview;
    private int verticalScrollMax;
    private Timer scrollTimer		=	null;
    private TimerTask scrollerSchedule;
    private int scrollPos =	0;
    private TimerTask clickSchedule;
    private int scrollSpeed = 3;

    private MediaPlayer creditSong;

    public CreditsFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
        creditSong = MediaPlayer.create(this.getActivity(), R.raw.credit);
        creditSong.setLooping(true);
        creditSong.start();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.credits_layout, container, false);

        view.setVerticalScrollBarEnabled(false);

        verticalScrollview  =   (ScrollView) view.findViewById(R.id.credits_scroller);
        verticalOuterLayout =	(LinearLayout) view.findViewById(R.id.credits_layout);

        ViewTreeObserver vto 		=	verticalOuterLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                verticalOuterLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                getScrollMaxAmount();
                startAutoScrolling();
            }
        });

        return view;

    }

    @Override
    public void onDestroy(){
        creditSong.stop();
        clearTimerTaks(clickSchedule);
        clearTimerTaks(scrollerSchedule);
        clearTimers(scrollTimer);

        clickSchedule         = null;
        scrollerSchedule      = null;
        scrollTimer           = null;

        super.onDestroy();
    }

    @Override
    public void onPause() {
        creditSong.stop();
        clearTimerTaks(clickSchedule);
        clearTimerTaks(scrollerSchedule);
        clearTimers(scrollTimer);

        clickSchedule         = null;
        scrollerSchedule      = null;
        scrollTimer           = null;

        super.onPause();
    }

    private void clearTimers(Timer timer){
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void clearTimerTaks(TimerTask timerTask){
        if(timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    public void getScrollMaxAmount(){
        int actualWidth = (verticalOuterLayout.getMeasuredHeight()-(256*3));
        verticalScrollMax   = actualWidth;
    }

    public void startAutoScrolling(){
        if (scrollTimer == null) {
            scrollTimer					=	new Timer();
            final Runnable Timer_Tick 	= 	new Runnable() {
                public void run() {
                    moveScrollView();
                }
            };

            if(scrollerSchedule != null){
                scrollerSchedule.cancel();
                scrollerSchedule = null;
            }
            scrollerSchedule = new TimerTask(){
                @Override
                public void run(){
                    getActivity().runOnUiThread(Timer_Tick);
                }
            };

            scrollTimer.schedule(scrollerSchedule, 30, 30);
        }
    }

    public void moveScrollView(){
        scrollPos							= 	(int) (verticalScrollview.getScrollY() + scrollSpeed);
        if(scrollPos >= verticalScrollMax){
            scrollPos = verticalScrollMax;
        }
        verticalScrollview.scrollTo(0,scrollPos);
        Log.e("moveScrollView","moveScrollView");
    }

    public void stopAutoScrolling(){
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer	=	null;
        }
    }
}
